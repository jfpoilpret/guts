//  Copyright 2009 Jean-Francois Poilpret
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package net.guts.event.internal;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.guts.event.Channel;
import net.guts.event.ConsumerExceptionHandler;

public class ChannelImpl<T> implements Channel<T>, Cleanable
{
	public ChannelImpl(Type eventType, String topic, 
		ConsumerExceptionHandler exceptionHandler, Cleaner cleanup)
	{
		_eventType = eventType;
		_topic = topic;
		_exceptionHandler = exceptionHandler;
		_cleanup = cleanup;
		_cleanup.addCleanable(this);
	}
	
	public void addConsumer(Object instance, Method consumer, int priority, Method filter, 
		Executor executor)
	{
		Lock write = _lock.writeLock();
		write.lock();
		try
		{
			SortedSet<Consumer> consumers = _consumers.get(executor);
			if (consumers == null)
			{
				consumers = new TreeSet<Consumer>();
				_consumers.put(executor, consumers);
			}
			consumers.add(new Consumer(instance, consumer, filter, priority));
		}
		finally
		{
			write.unlock();
		}
	}
	
	public void publish(final T event)
	{
		//  Find all interested consumers (based on filters)
		Map<Executor, EventPublisher> consumers = new HashMap<Executor, EventPublisher>();
		Lock read = _lock.readLock();
		read.lock();
		try
		{
			for (Map.Entry<Executor, SortedSet<Consumer>> entry: _consumers.entrySet())
			{
				EventPublisher publisher = new EventPublisher(event);
				consumers.put(entry.getKey(), publisher);
				for (Consumer consumer: entry.getValue())
				{
					if (consumer._instance.get() == null)
					{
						_needsCleanup = true;
					}
					else if (filter(consumer, event))
					{
						publisher._consumers.add(consumer);
					}
				}
			}
		}
		finally
		{
			read.unlock();
		}

		if (_needsCleanup)
		{
			// Enqueue this for later cleanup
			_cleanup.enqueueCleanable(this);
		}
		
		// Now call executors
		for (Map.Entry<Executor, EventPublisher> entry: consumers.entrySet())
		{
			// Don't call an executor if there's no consumer listening!
			if (!entry.getValue()._consumers.isEmpty())
			{
				entry.getKey().execute(entry.getValue());
			}
		}
	}

	private boolean needCleanup()
	{
		if (_needsCleanup)
		{
			return true;
		}
		
		Lock read = _lock.readLock();
		read.lock();
		try
		{
			for (Map.Entry<Executor, SortedSet<Consumer>> entry: _consumers.entrySet())
			{
				for (Consumer consumer: entry.getValue())
				{
					if (consumer._instance.get() == null)
					{
						return true;
					}
				}
			}
			return false;
		}
		finally
		{
			read.unlock();
		}
	}
	
	public void cleanup()
	{
		if (!needCleanup())
		{
			return;
		}
		Lock write = _lock.writeLock();
		write.lock();
		try
		{
			// First remove Consumers from each individual list for all executors
			for (SortedSet<Consumer> consumers: _consumers.values())
			{
				Iterator<Consumer> i = consumers.iterator();
				while (i.hasNext())
				{
					if (i.next()._instance.get() == null)
					{
						i.remove();
					}
				}
			}
			
			// Then remove any entries for which the consumer list is empty
			Iterator<Map.Entry<Executor, SortedSet<Consumer>>> i = 
				_consumers.entrySet().iterator();
			while (i.hasNext())
			{
				if (i.next().getValue().isEmpty())
				{
					i.remove();
				}
			}
			_needsCleanup = false;
		}
		finally
		{
			write.unlock();
		}
	}
	
	private boolean filter(Consumer consumer, T event)
	{
		if (consumer._filter != null)
		{
			return invoke(consumer._instance.get(), consumer._filter, event, false);
		}
		else
		{
			return true;
		}
	}
	
	private void notify(Consumer consumer, T event)
	{
		invoke(consumer._instance.get(), consumer._consumer, event, (Void) null);
	}
	
	//CSOFF: IllegalCatchCheck
	@SuppressWarnings("unchecked")
	private <U> U invoke(Object instance, Method method, T event, U defaultValue)
	{
		if (instance == null)
		{
			return defaultValue;
		}
		try
		{
			return (U) method.invoke(instance, event);
		}
		catch (InvocationTargetException e)
		{
			_exceptionHandler.handleException(
				e.getTargetException(), method, instance, _eventType, _topic);
		}
		catch (Exception e)
		{
			_exceptionHandler.handleException(e, method, instance, _eventType, _topic);
		}
		return defaultValue;
	}
	//CSON: IllegalCatchCheck

	static private class Consumer implements Comparable<Consumer>
	{
		Consumer(Object instance, Method consumer, Method filter, int priority)
		{
			_instance = new WeakReference<Object>(instance);
			_consumer = consumer;
			_filter = filter;
			_priority = priority;
		}

		public int compareTo(Consumer that)
		{
			int diff = this._priority - that._priority;
			return (diff != 0 ? diff : this._order - that._order);
		}

		final private WeakReference<Object> _instance;
		final private Method _consumer;
		final private Method _filter;
		final private int _priority;
		final private int _order = _total++;
		static private int _total = 0; 
	}
	
	private class EventPublisher implements Runnable
	{
		public EventPublisher(T event)
		{
			_event = event;
		}
		
		public void run()
		{
			for (Consumer consumer: _consumers)
			{
				ChannelImpl.this.notify(consumer, _event);
			}
		}
		
		private final T _event;
		private final SortedSet<Consumer> _consumers = new TreeSet<Consumer>();
	}

	final private Type _eventType;
	final private String _topic;
	final private ConsumerExceptionHandler _exceptionHandler;

	// Ordered list (by priority and registration time) of consumers for this channel
	// (grouped by Executor)
	final private Map<Executor, SortedSet<Consumer>> _consumers = 
		new IdentityHashMap<Executor, SortedSet<Consumer>>();
	// RW lock to access _consumers
	final private ReadWriteLock _lock = new ReentrantReadWriteLock();
	
	private boolean _needsCleanup = false;
	private final Cleaner _cleanup;
}
