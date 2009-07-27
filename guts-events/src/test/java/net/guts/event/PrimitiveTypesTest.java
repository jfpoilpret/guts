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

package net.guts.event;

import static org.easymock.EasyMock.createMock;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import net.guts.event.Channel;
import net.guts.event.Consumes;
import net.guts.event.EventModule;
import net.guts.event.Events;
import net.guts.event.Filters;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

@Test(groups = "itest")
public class PrimitiveTypesTest
{
	public void checkPrimitiveInjectPublishFilterConsume()
	{
		final ConsumerExceptionHandler handler = createMock(ConsumerExceptionHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(handler);
				Events.bindChannel(binder(), int.class);
			}
		});
		// Get Consumer & Suppliers
		Consumer1 consumer1 = EasyMock.createMock(Consumer1.class);
		Consumer2 consumer2 = injector.getInstance(Consumer2.class);
		consumer2.setMock(consumer1);
		Supplier1 supplier = injector.getInstance(Supplier1.class);

		// Prepare test
		EasyMock.expect(consumer1.filter1(1)).andReturn(true);
		consumer1.push1(1);
		EasyMock.expect(consumer1.filter1(2)).andReturn(false);
		EasyMock.expect(consumer1.filter1(3)).andReturn(true);
		consumer1.push1(3);
		
		// Replay test
		EasyMock.replay(handler, consumer1);
		supplier.generate(1, 2, 3);
		EasyMock.verify(handler, consumer1);
	}

	public interface Consumer1
	{
		public void push1(int event);
		public boolean filter1(int event);
	}
	
	static public class Consumer2
	{
		public void setMock(Consumer1 mock)
		{
			_mock = mock;
		}
		
		@Consumes public void push1(int event)
		{
			_mock.push1(event);
		}
		
		@Filters public boolean filter1(int event)
		{
			return _mock.filter1(event);
		}
		
		private Consumer1 _mock;
	}
	
	static public class Supplier1
	{
		@Inject public Supplier1(@Event(primitive = true) Channel<Integer> channel)
		{
			_channel = channel;
		}
		
		public void generate(int... numbers)
		{
			for (int i: numbers)
			{
				_channel.publish(i);
			}
		}
		
		final private Channel<Integer> _channel;
	}
	
	public void checkPrimitiveAndWrapperConflicts()
	{
		final ConsumerExceptionHandler handler = createMock(ConsumerExceptionHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(handler);
				Events.bindChannel(binder(), int.class);
				Events.bindChannel(binder(), Integer.class);
			}
		});
		// Get Consumer & Suppliers
		Consumer3 consumer1 = EasyMock.createMock(Consumer3.class);
		Consumer4 consumer2 = injector.getInstance(Consumer4.class);
		consumer2.setMock(consumer1);
		Supplier2 supplier = injector.getInstance(Supplier2.class);

		// Prepare test for primitive type
		EasyMock.expect(consumer1.filterPrimitive(1)).andReturn(true);
		consumer1.pushPrimitive(1);
		EasyMock.expect(consumer1.filterPrimitive(2)).andReturn(false);
		EasyMock.expect(consumer1.filterPrimitive(3)).andReturn(true);
		consumer1.pushPrimitive(3);
		
		// Replay test
		EasyMock.replay(handler, consumer1);
		supplier.generatePrimitive(1, 2, 3);
		EasyMock.verify(handler, consumer1);

		// Prepare test for wrapper type
		EasyMock.reset(handler, consumer1);
		EasyMock.expect(consumer1.filterWrapper(1)).andReturn(true);
		consumer1.pushWrapper(1);
		EasyMock.expect(consumer1.filterWrapper(2)).andReturn(false);
		EasyMock.expect(consumer1.filterWrapper(3)).andReturn(true);
		consumer1.pushWrapper(3);
		
		// Replay test
		EasyMock.replay(handler, consumer1);
		supplier.generateWrapper(1, 2, 3);
		EasyMock.verify(handler, consumer1);
	}

	public interface Consumer3
	{
		public void pushPrimitive(int event);
		public boolean filterPrimitive(int event);
		public void pushWrapper(Integer event);
		public boolean filterWrapper(Integer event);
	}
	
	static public class Consumer4
	{
		public void setMock(Consumer3 mock)
		{
			_mock = mock;
		}
		
		@Consumes public void pushPrimitive(int event)
		{
			_mock.pushPrimitive(event);
		}
		
		@Filters public boolean filterPrimitive(int event)
		{
			return _mock.filterPrimitive(event);
		}
		
		@Consumes public void pushWrapper(Integer event)
		{
			_mock.pushWrapper(event);
		}
		
		@Filters public boolean filterWrapper(Integer event)
		{
			return _mock.filterWrapper(event);
		}
		
		private Consumer3 _mock;
	}
	
	static public class Supplier2
	{
		@Inject public Supplier2(@Event(primitive = true) Channel<Integer> primitiveChannel, 
			Channel<Integer> wrapperChannel)
		{
			_primitiveChannel = primitiveChannel;
			_wrapperChannel = wrapperChannel;
		}
		
		public void generatePrimitive(int... numbers)
		{
			for (int i: numbers)
			{
				_primitiveChannel.publish(i);
			}
		}
		
		public void generateWrapper(Integer... numbers)
		{
			for (Integer i: numbers)
			{
				_wrapperChannel.publish(i);
			}
		}
		
		final private Channel<Integer> _primitiveChannel;
		final private Channel<Integer> _wrapperChannel;
	}

	public void checkPrimitiveTopicConflicts()
	{
		final ConsumerExceptionHandler handler = createMock(ConsumerExceptionHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(handler);
				Events.bindChannel(binder(), int.class);
				Events.bindChannel(binder(), int.class, TOPIC);
			}
		});
		// Get Consumer & Suppliers
		Consumer5 consumer1 = EasyMock.createMock(Consumer5.class);
		Consumer6 consumer2 = injector.getInstance(Consumer6.class);
		consumer2.setMock(consumer1);
		Supplier3 supplier = injector.getInstance(Supplier3.class);

		// Prepare test for primitive non-topic type
		EasyMock.expect(consumer1.filter1(1)).andReturn(true);
		consumer1.push1(1);
		EasyMock.expect(consumer1.filter1(2)).andReturn(false);
		EasyMock.expect(consumer1.filter1(3)).andReturn(true);
		consumer1.push1(3);
		
		// Replay test
		EasyMock.replay(handler, consumer1);
		supplier.generate(1, 2, 3);
		EasyMock.verify(handler, consumer1);

		// Prepare test for primitive topic type
		EasyMock.reset(handler, consumer1);
		EasyMock.expect(consumer1.filter2(1)).andReturn(true);
		consumer1.push2(1);
		EasyMock.expect(consumer1.filter2(2)).andReturn(false);
		EasyMock.expect(consumer1.filter2(3)).andReturn(true);
		consumer1.push2(3);
		
		// Replay test
		EasyMock.replay(handler, consumer1);
		supplier.generateTopic(1, 2, 3);
		EasyMock.verify(handler, consumer1);
	}

	public interface Consumer5
	{
		public void push1(int event);
		public boolean filter1(int event);
		public void push2(int event);
		public boolean filter2(int event);
	}
	
	static public class Consumer6
	{
		public void setMock(Consumer5 mock)
		{
			_mock = mock;
		}
		
		@Consumes public void push1(int event)
		{
			_mock.push1(event);
		}
		
		@Filters public boolean filter1(int event)
		{
			return _mock.filter1(event);
		}
		
		@Consumes(topic = TOPIC) public void push2(int event)
		{
			_mock.push2(event);
		}
		
		@Filters(topic = TOPIC) public boolean filter2(int event)
		{
			return _mock.filter2(event);
		}
		
		private Consumer5 _mock;
	}
	
	static public class Supplier3
	{
		@Inject public Supplier3(@Event(primitive = true) Channel<Integer> channel, 
				@Event(topic = TOPIC, primitive = true) Channel<Integer> topicChannel)
		{
			_channel = channel;
			_topicChannel = topicChannel;
		}
		
		public void generate(int... numbers)
		{
			for (int i: numbers)
			{
				_channel.publish(i);
			}
		}
		
		public void generateTopic(int... numbers)
		{
			for (int i: numbers)
			{
				_topicChannel.publish(i);
			}
		}
		
		final private Channel<Integer> _channel;
		final private Channel<Integer> _topicChannel;
	}

	static private final String TOPIC = "TOPIC";
}
