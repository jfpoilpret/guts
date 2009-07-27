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
public class AutoTypeFilterTest
{
	public void checkConsumeIntegerOnNumberChannel()
	{
		final ConsumerExceptionHandler handler = createMock(ConsumerExceptionHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(handler);
				Events.bindChannel(binder(), Number.class);
			}
		});
		// Get Consumer & Suppliers
		Consumer1 consumer1 = EasyMock.createMock(Consumer1.class);
		Consumer2 consumer2 = injector.getInstance(Consumer2.class);
		consumer2.setMock(consumer1);
		Supplier supplier = injector.getInstance(Supplier.class);

		// Prepare test
		consumer1.push(1);
		consumer1.push(3);
		
		// Replay test
		EasyMock.replay(handler, consumer1);
		supplier.generate(1, 2L, 3);
		EasyMock.verify(handler, consumer1);
	}

	public void checkFilterIntegerOnNumberChannel()
	{
		final ConsumerExceptionHandler handler = createMock(ConsumerExceptionHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(handler);
				Events.bindChannel(binder(), Number.class);
			}
		});
		// Get Consumer & Suppliers
		Consumer1 consumer1 = EasyMock.createMock(Consumer1.class);
		Consumer3 consumer2 = injector.getInstance(Consumer3.class);
		consumer2.setMock(consumer1);
		Supplier supplier = injector.getInstance(Supplier.class);

		// Prepare test
		EasyMock.expect(consumer1.filter(1)).andReturn(false);
		EasyMock.expect(consumer1.filter(3)).andReturn(true);
		consumer1.push(3);
		
		// Replay test
		EasyMock.replay(handler, consumer1);
		supplier.generate(1, 2L, 3);
		EasyMock.verify(handler, consumer1);
	}

	public interface Consumer1
	{
		public void push(Number event);
		public boolean filter(Number event);
	}
	
	static public class Consumer2
	{
		public void setMock(Consumer1 mock)
		{
			_mock = mock;
		}
		
		@Consumes(type = Number.class) public void push(Integer event)
		{
			_mock.push(event);
		}
		
		private Consumer1 _mock;
	}
	
	static public class Consumer3
	{
		public void setMock(Consumer1 mock)
		{
			_mock = mock;
		}
		
		@Consumes(type = Number.class) public void push(Integer event)
		{
			_mock.push(event);
		}
		
		@Filters(type = Number.class) public boolean filter(Integer event)
		{
			return _mock.filter(event);
		}
		
		private Consumer1 _mock;
	}
	
	static public class Supplier
	{
		@Inject public Supplier(Channel<Number> channel)
		{
			_channel = channel;
		}
		
		public void generate(Number... numbers)
		{
			for (Number i: numbers)
			{
				_channel.publish(i);
			}
		}
		
		final private Channel<Number> _channel;
	}
}
