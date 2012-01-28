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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.createMock;
import static org.fest.assertions.Assertions.assertThat;

import net.guts.common.injection.InjectionListeners;
import net.guts.event.Channel;
import net.guts.event.Consumes;
import net.guts.event.EventModule;
import net.guts.event.Events;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

@Test(groups = "itest")
public class BenchMarksTest
{
	static final private int NUM_LOOPS = 10;
	static final private int NUM_EVENTS = 1000000;
	static final private long TOTAL_EVENTS = 1L * NUM_LOOPS * NUM_EVENTS;
	
	public void checkDirectCallToOneConsumerWithoutGuice()
	{
		Consumer1 consumer = new Consumer1();
		// Send large number of events
		Timer timer = Timer.start();
		for (int i = 0; i < NUM_LOOPS; i++)
		{
			long shift = i * NUM_EVENTS;
			for (int j = 0; j < NUM_EVENTS; j++)
			{
				consumer.push(shift + j);
			}
		}
		logTime("Direct java calls", timer.end(), TOTAL_EVENTS, 1);
		// Check all events were received
		assertThat(consumer.getNumEvents()).as("Count of received events").isEqualTo(TOTAL_EVENTS);
		assertThat(consumer.getLastEvent()).as("Last received event").isEqualTo(TOTAL_EVENTS - 1);
	}

	//TODO factor out common code
	public void checkDirectCallToOneConsumerWithoutEventChannel()
	{
		final ConsumerExceptionHandler handler = createMock(ConsumerExceptionHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(handler);
				Events.bindChannel(binder(), Long.class);
			}
		});
		InjectionListeners.injectListeners(injector);
		EasyMock.replay(handler);
		// Get Consumer & Suppliers
		Consumer1 consumer = injector.getInstance(Consumer1.class);

		// Send large number of events
		Timer timer = Timer.start();
		for (int i = 0; i < NUM_LOOPS; i++)
		{
			long shift = i * NUM_EVENTS;
			for (int j = 0; j < NUM_EVENTS; j++)
			{
				consumer.push(shift + j);
			}
		}
		logTime("Direct calls to Guice-constructed instance", timer.end(), TOTAL_EVENTS, 1);
		// Check all events were received
		assertThat(consumer.getNumEvents()).as("Count of received events").isEqualTo(TOTAL_EVENTS);
		assertThat(consumer.getLastEvent()).as("Last received event").isEqualTo(TOTAL_EVENTS - 1);
		EasyMock.verify(handler);
	}
	
	public void checkCallToOneConsumerThroughEventChannel()
	{
		final ConsumerExceptionHandler handler = createMock(ConsumerExceptionHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(handler);
				Events.bindChannel(binder(), Long.class);
			}
		});
		InjectionListeners.injectListeners(injector);
		EasyMock.replay(handler);
		// Get Consumer & Suppliers
		Consumer1 consumer = injector.getInstance(Consumer1.class);
		Supplier supplier = injector.getInstance(Supplier.class);
		Channel<Long> channel = supplier.channel;

		// Send large number of events
		Timer timer = Timer.start();
		for (int i = 0; i < NUM_LOOPS; i++)
		{
			long shift = i * NUM_EVENTS;
			for (int j = 0; j < NUM_EVENTS; j++)
			{
				channel.publish(shift + j);
			}
		}
		logTime("Calls through Event Channel", timer.end(), TOTAL_EVENTS, 1);
		// Check all events were received
		assertThat(consumer.getNumEvents()).as("Count of received events").isEqualTo(TOTAL_EVENTS);
		assertThat(consumer.getLastEvent()).as("Last received event").isEqualTo(TOTAL_EVENTS - 1);
		EasyMock.verify(handler);
	}

	public void checkCallToSeveralConsumersThroughEventChannel()
	{
		final ConsumerExceptionHandler handler = createMock(ConsumerExceptionHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(handler);
				Events.bindChannel(binder(), Long.class);
			}
		});
		InjectionListeners.injectListeners(injector);
		EasyMock.replay(handler);
		// Get Consumer & Suppliers
		List<Consumer1> consumers = new ArrayList<Consumer1>();
		for (int i = 0; i < NUM_LOOPS; i++)
		{
			Consumer1 consumer = injector.getInstance(Consumer1.class);
			consumers.add(consumer);
		}
		Supplier supplier = injector.getInstance(Supplier.class);
		Channel<Long> channel = supplier.channel;

		// Send large number of events
		Timer timer = Timer.start();
		for (long j = 0; j < NUM_EVENTS; j++)
		{
			channel.publish(j);
		}
		logTime("Calls to many consumers through Event Channel", timer.end(), NUM_EVENTS, NUM_LOOPS);
		// Check all events were received
		for (Consumer1 consumer: consumers)
		{
			assertThat(consumer.getNumEvents()).as("Count of received events").isEqualTo(NUM_EVENTS);
			assertThat(consumer.getLastEvent()).as("Last received event").isEqualTo(NUM_EVENTS - 1);
		}
		EasyMock.verify(handler);
	}

	public void checkDeferredCallToOneConsumerThroughEventChannel() throws Exception
	{
		final ConsumerExceptionHandler handler = createMock(ConsumerExceptionHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(handler);
				Events.bindChannel(binder(), Long.class);
			}
		});
		InjectionListeners.injectListeners(injector);
		EasyMock.replay(handler);
		// Get Consumer & Suppliers
		Consumer2 consumer = injector.getInstance(Consumer2.class);
		Supplier supplier = injector.getInstance(Supplier.class);
		Channel<Long> channel = supplier.channel;

		// Send large number of events
		long numEvents = NUM_EVENTS;
		numEvents /= 10;
		Timer timer = Timer.start();
		for (long j = 0; j < numEvents; j++)
		{
			channel.publish(j);
		}
		logTime("Deferred calls through Event Channel", timer.end(), numEvents, 1);
		Thread.sleep(1000L);
		// Check all events were received
		assertThat(consumer.getNumEvents()).as("Count of received events").isEqualTo(numEvents);
		// In deferred mode, we cannot check the last event received because order may not be preserved
//		assertThat(consumer.getLastEvent()).as("Last received event").isEqualTo(numEvents - 1);
		EasyMock.verify(handler);
	}

	public void checkPooledCallToOneConsumerThroughEventChannel() throws Exception
	{
		final ConsumerExceptionHandler handler = createMock(ConsumerExceptionHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(handler);
				Events.bindChannel(binder(), Long.class);
				Events.bindExecutor(binder(), InPooledThread.class)
					.toInstance(Executors.newFixedThreadPool(1));
			}
		});
		InjectionListeners.injectListeners(injector);
		EasyMock.replay(handler);
		// Get Consumer & Suppliers
		Consumer3 consumer = injector.getInstance(Consumer3.class);
		Supplier supplier = injector.getInstance(Supplier.class);
		Channel<Long> channel = supplier.channel;

		// Send large number of events
		Timer timer = Timer.start();
		for (int i = 0; i < NUM_LOOPS; i++)
		{
			long shift = i * NUM_EVENTS;
			for (int j = 0; j < NUM_EVENTS; j++)
			{
				channel.publish(shift + j);
			}
		}
		logTime("Pooled calls through Event Channel", timer.end(), TOTAL_EVENTS, 1);
		Thread.sleep(1000L);//FIXME wait for executor to terminate
		// Check all events were received
		assertThat(consumer.getNumEvents()).as("Count of received events").isEqualTo(TOTAL_EVENTS);
		assertThat(consumer.getLastEvent()).as("Last received event").isEqualTo(TOTAL_EVENTS - 1);
		EasyMock.verify(handler);
	}

	static abstract class AbstractConsumer
	{
		protected void pushEvent(Long event)
		{
			_numEvents.incrementAndGet();
			_lastEvent = event;
		}
		
		public long getNumEvents()
		{
			return _numEvents.get();
		}
		
		public Long getLastEvent()
		{
			return _lastEvent;
		}

		private AtomicLong _numEvents = new AtomicLong(0);
		private Long _lastEvent = null;
	}
	
	static public class Consumer1 extends AbstractConsumer
	{
		@Consumes public void push(Long event)
		{
			pushEvent(event);
		}
	}
	
	static public class Consumer2 extends AbstractConsumer
	{
		@Consumes @InDeferredThread public void push(Long event)
		{
			pushEvent(event);
		}
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface InPooledThread
	{
	}
	
	static public class Consumer3 extends AbstractConsumer
	{
		@Consumes @InPooledThread public void push(Long event)
		{
			pushEvent(event);
		}
	}

	static public class Supplier
	{
		@Inject public Channel<Long> channel;
	}
	
	static private class Timer
	{
		static Timer start()
		{
			return new Timer();
		}
		
		long end()
		{
			return System.nanoTime() - _start;
		}
		
//		long endLog()
//		{
//			long time = end();
//			System.out.printf("Time figures for '%s'\n", _action);
//			System.out.printf("\tTime elapsed for %d events: %d ns\n", TOTAL_EVENTS, time);
//			System.out.printf("\tTime elapsed per event: %d ns\n", time / TOTAL_EVENTS);
//			System.out.printf("\tEvents per second: %d\n", 1000000000L * TOTAL_EVENTS / time);
//			return time;
//		}

		final private long _start = System.nanoTime();
	}
	
	static private void logTime(String action, long time, long numEvents, long numConsumers)
	{
		System.out.printf("Time figures for '%s'\n", action);
		System.out.printf("\tTime elapsed for %d events: %d ns\n", numEvents, time);
		System.out.printf("\tTime elapsed per event: %d ns\n", time / numEvents);
		System.out.printf("\tTime elapsed per event per consumer: %d ns\n", time / numEvents / numConsumers);
		System.out.printf("\tEvents per second: %d\n", 1000000000L * numEvents / time);
	}
}
