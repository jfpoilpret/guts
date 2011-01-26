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

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.guts.event.Channel;
import net.guts.event.EventService;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

@Singleton
public class EventServiceImpl implements EventService
{
	@Inject
	public EventServiceImpl(
		AnnotationProcessorFactory processorFactory, ChannelFactory channelFactory)
	{
		_processor = processorFactory.create(Collections.unmodifiableSet(_channels.keySet()));
		_factory = channelFactory;
	}
	
	@Override public <T> void registerChannel(TypeLiteral<T> type, String topic)
	{
		ChannelKey key = new ChannelKey(type.getType(), topic);
		Lock write = _lock.writeLock();
		write.lock();
		try
		{
			if (!_channels.containsKey(key))
			{
				_channels.put(key, new ChannelImplHolder());
			}
		}
		finally
		{
			write.unlock();
		}
	}

	@Override @SuppressWarnings("unchecked")
	public <T> Channel<T> getChannel(TypeLiteral<T> type, String topic)
		throws IllegalArgumentException
	{
		return (Channel<T>) getChannelImpl(type.getType(), topic);
	}

	private ChannelImpl<?> getChannelImpl(Type type, String topic)
		throws IllegalArgumentException
	{
		ChannelKey key = new ChannelKey(type, topic);
		Lock read = _lock.readLock();
		read.lock();
		try
		{
			ChannelImplHolder holder = _channels.get(key);
			if (holder == null)
			{
				String message = String.format(
					"No registered Event Channel for type '%s' and topic '%s'", type, topic);
				throw new IllegalArgumentException(message);
			}
			//TODO use double-check locking for better performance?
			synchronized (holder)
			{
				ChannelImpl<?> channel = holder.getChannel();
				if (channel == null)
				{
					channel = _factory.create(type, topic);
					holder.setChannel(channel);
				}
				return channel;
			}
		}
		finally
		{
			read.unlock();
		}
	}

	@Override public void registerConsumers(Object instance)
	{
		Class<?> clazz = instance.getClass();
		Lock read = _lock.readLock();
		read.lock();
		try
		{
			List<ConsumerFilter> events = _processor.process(clazz);
			// Add all consumers (and filters) to the matching channels 
			for (ConsumerFilter event: events)
			{
				ChannelImpl<?> channel = 
					getChannelImpl(event.getKey().getType(), event.getKey().getTopic());
				channel.addConsumer(instance, event.getConsumer(), event.getPriority(), 
					event.getFilter(), event.getExecutor());
			}
		}
		finally
		{
			read.unlock();
		}
	}
	
	static public class ChannelImplHolder
	{
		ChannelImpl<?> getChannel()
		{
			return _channel;
		}

		void setChannel(ChannelImpl<?> channel)
		{
			_channel = channel;
		}

		private ChannelImpl<?> _channel;
	}

	final private AnnotationProcessor _processor;
	final private ChannelFactory _factory;

	// Map of all registered event channels
	final private Map<ChannelKey, ChannelImplHolder> _channels = 
		new HashMap<ChannelKey, ChannelImplHolder>();
	// RW lock to access _consumers
	final private ReadWriteLock _lock = new ReentrantReadWriteLock();
}
