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

import java.lang.reflect.Method;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.same;
import static org.easymock.EasyMock.verify;

import org.testng.annotations.Test;

import net.guts.event.Channel;
import net.guts.event.ConsumerExceptionHandler;
import net.guts.event.Consumes;
import net.guts.event.EventModule;
import net.guts.event.Events;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

@Test(groups = "itest")
public class ClearWeakConsumerRefTest
{
	public void checkStrongReferenceNotCleared()
	{
		final ConsumerExceptionHandler handler = createMock(ConsumerExceptionHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(handler);
				Events.bindChannel(binder(), Integer.class);
			}
		});
		// Get Consumer & Suppliers
		Consumer consumer = injector.getInstance(Consumer.class);
		Channel<Integer> supplier = injector.getInstance(Key.get(new TypeLiteral<Channel<Integer>>(){}));
		
		handler.handleException(isA(PushException.class), (Method) anyObject(), 
				same(consumer), same(Integer.class), eq(""));
		replay(handler);
		supplier.publish(10);
		verify(handler);
	}

	public void checkWeakReferenceCleared()
	{
		final ConsumerExceptionHandler handler = createMock(ConsumerExceptionHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(handler);
				Events.bindChannel(binder(), Integer.class);
			}
		});
		// Get Consumer & Suppliers
		Consumer consumer = injector.getInstance(Consumer.class);
		Channel<Integer> supplier = injector.getInstance(Key.get(new TypeLiteral<Channel<Integer>>(){}));
		
		replay(handler);
		consumer = null;
		System.gc();
		supplier.publish(10);
		verify(handler);
	}

	static public class Consumer
	{
		@Consumes public void push(Integer event)
		{
			throw new PushException();
		}
	}
	
	static public class PushException extends RuntimeException
	{
	}
}
