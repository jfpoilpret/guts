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

import net.guts.common.cleaner.Cleanable;
import net.guts.common.cleaner.Cleaner;
import net.guts.common.type.Nullable;
import net.guts.event.Channel;
import net.guts.event.ConsumerExceptionHandler;
import net.guts.event.ConsumerReturnHandler;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.Assisted;

public class ChannelImpl<T> implements Channel<T>, Cleanable
{
	@Inject
	public ChannelImpl(@Assisted Type eventType, @Assisted @Nullable String topic, 
		ConsumerExceptionHandler exceptionHandler, Cleaner cleanup,
		Map<TypeLiteral<?>, ConsumerReturnHandler<?>> returnHandlers)
	{
		_eventType = eventType;
		_eventClass = TypeLiteral.get(_eventType).getRawType();
		_topic = topic;
		_exceptionHandler = exceptionHandler;
		_cleanup = cleanup;
		_cleanup.addCleanable(this);
		_returnHandlers = returnHandlers;
	}
	
	public void addConsumer(Object instance, Method consumer, int priority, Method filter, 
		Executor executor)
	{
		Lock write = _lock.writeLock();
		write.lock();
		try
		{
			SortedSet<Consumer> consumers = _executorConsumers.get(executor);
			if (consumers == null)
			{
				consumers = new TreeSet<Consumer>();
				_executorConsumers.put(executor, consumers);
			}
			consumers.add(new Consumer(instance, consumer, filter, priority));
		}
		finally
		{
			write.unlock();
		}
	}
	
	@Override public void publish(final T event)
	{
		//  Find all interested consumers (based on filters)
		Map<Executor, EventPublisher> consumers = new HashMap<Executor, EventPublisher>();
		Lock read = _lock.readLock();
		read.lock();
		try
		{
			for (Map.Entry<Executor, SortedSet<Consumer>> entry: _executorConsumers.entrySet())
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
			for (Map.Entry<Executor, SortedSet<Consumer>> entry: _executorConsumers.entrySet())
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
	
	@Override public void cleanup()
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
			for (SortedSet<Consumer> consumers: _executorConsumers.values())
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
				_executorConsumers.entrySet().iterator();
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
		Class<?> actualType = (event == null ? _eventClass : event.getClass());
		if (consumer._filter != null)
		{
			// Check the event type matches _filter argument type
			Class<?> argType = consumer._filter.getParameterTypes()[0];
			if ((!argType.isPrimitive()) && !argType.isAssignableFrom(actualType))
			{
				return false;
			}
			if (!invoke(consumer._instance.get(), consumer._filter, event, false))
			{
				return false;
			}
		}
		// Check the event matches _consumer argument type
		Class<?> argType = consumer._consumer.getParameterTypes()[0];
		return (argType.isPrimitive() || argType.isAssignableFrom(actualType));
	}
	
	//CSOFF: IllegalCatchCheck
	@SuppressWarnings({"unchecked", "rawtypes"})
	private void notify(Consumer consumer, T event)
	{
		Object instance = consumer._instance.get();
		Object result = invoke(instance, consumer._consumer, event, null);
		if (result != null)
		{
			// Check if there is a handler for the returned type of the consumer method
			TypeLiteral<?> type = TypeLiteral.get(consumer._consumer.getGenericReturnType());
			ConsumerReturnHandler handler = findHandler(type);
			if (handler != null)
			{
				// Call the registered handler for that return type
				try
				{
					handler.handle(result);
				}
				catch (Exception e)
				{
					_exceptionHandler.handleException(
						e, consumer._consumer, instance, _eventType, _topic);
				}
			}
		}
	}
	//CSON: IllegalCatchCheck

	//TODO potential enhancements: also match generic wildcards, supertypes, interfaces
	private ConsumerReturnHandler<?> findHandler(TypeLiteral<?> type)
	{
		return _returnHandlers.get(type);
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
			method.setAccessible(true);
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

		@Override public int compareTo(Consumer that)
		{
			int diff = this._priority - that._priority;
			return (diff != 0 ? diff : this._order - that._order);
		}
		
		@Override public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + _order;
			result = prime * result + _priority;
			return result;
		}

		@Override public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			Consumer other = (Consumer) obj;
			return compareTo(other) == 0;
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
		
		@Override public void run()
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
	final private Class<?> _eventClass;
	final private String _topic;
	final private ConsumerExceptionHandler _exceptionHandler;
	final private Map<TypeLiteral<?>, ConsumerReturnHandler<?>> _returnHandlers;

	// Ordered list (by priority and registration time) of consumers for this channel
	// (grouped by Executor)
	final private Map<Executor, SortedSet<Consumer>> _executorConsumers = 
		new IdentityHashMap<Executor, SortedSet<Consumer>>();
	// RW lock to access _consumers
	final private ReadWriteLock _lock = new ReentrantReadWriteLock();
	
	private boolean _needsCleanup = false;
	private final Cleaner _cleanup;
}
