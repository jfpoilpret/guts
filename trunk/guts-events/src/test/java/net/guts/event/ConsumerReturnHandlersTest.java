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

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createMock;

import net.guts.common.injection.InjectionListeners;
import net.guts.event.Channel;
import net.guts.event.Consumes;
import net.guts.event.EventModule;
import net.guts.event.Events;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

@Test(groups = "itest")
public class ConsumerReturnHandlersTest
{
	public void checkPrimitiveHandler()
	{
		final ConsumerExceptionHandler exceptionHandler = createMock(ConsumerExceptionHandler.class);
		final ConsumerReturnHandler<Integer> handler = createMock(ConsumerReturnHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(exceptionHandler);
				Events.bindChannel(binder(), TypeLiteral.get(Integer.class));
				Events.bindHandler(binder(), int.class).toInstance(handler);
			}
		});
		InjectionListeners.injectListeners(injector);
		
		handler.handle(10);
		handler.handle(20);
		EasyMock.replay(exceptionHandler, handler);

		// Get Consumer & Suppliers
		Consumer1 consumer = injector.getInstance(Consumer1.class);
		Supplier supplier = injector.getInstance(Supplier.class);
		// Send events
		supplier.generate(10, 20);
		EasyMock.verify(exceptionHandler, handler);
	}

	static public class Consumer1
	{
		@Consumes public int push(Integer event)
		{
			return event;
		}
	}
	
	public void checkStringHandler()
	{
		final ConsumerExceptionHandler exceptionHandler = createMock(ConsumerExceptionHandler.class);
		final ConsumerReturnHandler<String> handler = createMock(ConsumerReturnHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(exceptionHandler);
				Events.bindChannel(binder(), TypeLiteral.get(Integer.class));
				Events.bindHandler(binder(), String.class).toInstance(handler);
			}
		});
		InjectionListeners.injectListeners(injector);
		
		handler.handle("10");
		handler.handle("20");
		EasyMock.replay(exceptionHandler, handler);

		// Get Consumer & Suppliers
		Consumer2 consumer = injector.getInstance(Consumer2.class);
		Supplier supplier = injector.getInstance(Supplier.class);
		// Send events
		supplier.generate(10, 20);
		EasyMock.verify(exceptionHandler, handler);
	}

	static public class Consumer2
	{
		@Consumes public String push(Integer event)
		{
			return "" + event;
		}
	}
	
	public void checkGenericTypeHandler()
	{
		final ConsumerExceptionHandler exceptionHandler = createMock(ConsumerExceptionHandler.class);
		final ConsumerReturnHandler<List<Integer>> handler = createMock(ConsumerReturnHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(exceptionHandler);
				Events.bindChannel(binder(), TypeLiteral.get(Integer.class));
				Events.bindHandler(binder(), new TypeLiteral<List<Integer>>(){}).toInstance(handler);
			}
		});
		InjectionListeners.injectListeners(injector);
		
		handler.handle(Arrays.asList(10));
		handler.handle(Arrays.asList(20));
		EasyMock.replay(exceptionHandler, handler);

		// Get Consumer & Suppliers
		Consumer3 consumer = injector.getInstance(Consumer3.class);
		Supplier supplier = injector.getInstance(Supplier.class);
		// Send events
		supplier.generate(10, 20);
		EasyMock.verify(exceptionHandler, handler);
	}

	static public class Consumer3
	{
		@Consumes public List<Integer> push(Integer event)
		{
			return Arrays.asList(event);
		}
	}
	
	public void checkWildcardTypeResolution()
	{
		final ConsumerExceptionHandler exceptionHandler = createMock(ConsumerExceptionHandler.class);
		final ConsumerReturnHandler<List<?>> handler = createMock(ConsumerReturnHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(exceptionHandler);
				Events.bindChannel(binder(), TypeLiteral.get(Integer.class));
				Events.bindHandler(binder(), new TypeLiteral<List<?>>(){}).toInstance(handler);
			}
		});
		InjectionListeners.injectListeners(injector);
		
		handler.handle(Arrays.asList(10));
		handler.handle(Arrays.asList(20));
		EasyMock.replay(exceptionHandler, handler);

		// Get Consumer & Suppliers
		Consumer4 consumer = injector.getInstance(Consumer4.class);
		Supplier supplier = injector.getInstance(Supplier.class);
		// Send events
		supplier.generate(10, 20);
		EasyMock.verify(exceptionHandler, handler);
	}

	static public class Consumer4
	{
		@Consumes public List<?> push(Integer event)
		{
			return Arrays.asList(event);
		}
	}
	
	static public class Supplier
	{
		@Inject public Supplier(Channel<Integer> channel)
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
}
