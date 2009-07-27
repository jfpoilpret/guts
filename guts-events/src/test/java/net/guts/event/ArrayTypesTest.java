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
public class ArrayTypesTest
{
	public void checkPrimitiveArrayInjectPublishFilterConsume()
	{
		final ConsumerExceptionHandler handler = createMock(ConsumerExceptionHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(handler);
				Events.bindChannel(binder(), int[].class);
			}
		});
		// Get Consumer & Suppliers
		Consumer1 consumer1 = EasyMock.createMock(Consumer1.class);
		Consumer2 consumer2 = injector.getInstance(Consumer2.class);
		consumer2.setMock(consumer1);
		Supplier1 supplier = injector.getInstance(Supplier1.class);

		// Prepare test
		EasyMock.expect(consumer1.filter1(EasyMock.aryEq(new int[0]))).andReturn(false);
		EasyMock.expect(consumer1.filter1(EasyMock.aryEq(new int[]{1, 2, 3}))).andReturn(true);
		consumer1.push1(EasyMock.aryEq(new int[]{1, 2, 3}));
		EasyMock.expect(consumer1.filter1(EasyMock.aryEq(new int[]{5, 6}))).andReturn(true);
		consumer1.push1(EasyMock.aryEq(new int[]{5, 6}));
		
		// Replay test
		EasyMock.replay(handler, consumer1);
		supplier.generate();
		supplier.generate(1, 2, 3);
		supplier.generate(5, 6);
		EasyMock.verify(handler, consumer1);
	}

	public interface Consumer1
	{
		public void push1(int[] event);
		public boolean filter1(int[] event);
	}
	
	static public class Consumer2
	{
		public void setMock(Consumer1 mock)
		{
			_mock = mock;
		}
		
		@Consumes public void push1(int[] event)
		{
			_mock.push1(event);
		}
		
		@Filters public boolean filter1(int[] event)
		{
			return _mock.filter1(event);
		}
		
		private Consumer1 _mock;
	}
	
	static public class Supplier1
	{
		@Inject public Supplier1(Channel<int[]> channel)
		{
			_channel = channel;
		}
		
		public void generate(int... numbers)
		{
			_channel.publish(numbers);
		}
		
		final private Channel<int[]> _channel;
	}
	
	public void checkArrayInjectPublishFilterConsume()
	{
		final ConsumerExceptionHandler handler = createMock(ConsumerExceptionHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(handler);
				Events.bindChannel(binder(), Integer[].class);
			}
		});
		// Get Consumer & Suppliers
		Consumer3 consumer1 = EasyMock.createMock(Consumer3.class);
		Consumer4 consumer2 = injector.getInstance(Consumer4.class);
		consumer2.setMock(consumer1);
		Supplier2 supplier = injector.getInstance(Supplier2.class);

		// Prepare test
		EasyMock.expect(consumer1.filter1(EasyMock.aryEq(new Integer[0]))).andReturn(false);
		EasyMock.expect(consumer1.filter1(EasyMock.aryEq(new Integer[]{1, 2, 3}))).andReturn(true);
		consumer1.push1(EasyMock.aryEq(new Integer[]{1, 2, 3}));
		EasyMock.expect(consumer1.filter1(EasyMock.aryEq(new Integer[]{5, 6}))).andReturn(true);
		consumer1.push1(EasyMock.aryEq(new Integer[]{5, 6}));
		
		// Replay test
		EasyMock.replay(handler, consumer1);
		supplier.generate();
		supplier.generate(1, 2, 3);
		supplier.generate(5, 6);
		EasyMock.verify(handler, consumer1);
	}

	public interface Consumer3
	{
		public void push1(Integer[] event);
		public boolean filter1(Integer[] event);
	}
	
	static public class Consumer4
	{
		public void setMock(Consumer3 mock)
		{
			_mock = mock;
		}
		
		@Consumes public void push1(Integer[] event)
		{
			_mock.push1(event);
		}
		
		@Filters public boolean filter1(Integer[] event)
		{
			return _mock.filter1(event);
		}
		
		private Consumer3 _mock;
	}
	
	static public class Supplier2
	{
		@Inject public Supplier2(Channel<Integer[]> channel)
		{
			_channel = channel;
		}
		
		public void generate(Integer... numbers)
		{
			_channel.publish(numbers);
		}
		
		final private Channel<Integer[]> _channel;
	}
}
