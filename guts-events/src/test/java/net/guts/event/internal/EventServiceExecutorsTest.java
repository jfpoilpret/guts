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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.easymock.Capture;
import org.easymock.classextension.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;
import static org.easymock.classextension.EasyMock.verify;

import net.guts.event.Channel;
import net.guts.event.ConsumerExceptionHandler;
import net.guts.event.Consumes;
import net.guts.event.ErrorHandler;
import net.guts.event.EventService;
import net.guts.event.InDeferredThread;
import net.guts.event.InEDT;
import net.guts.event.internal.AnnotationProcessor;
import net.guts.event.internal.AnnotationProcessorFactory;
import net.guts.event.internal.ChannelKey;
import net.guts.event.internal.Cleaner;
import net.guts.event.internal.EventServiceImpl;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

@Test(groups = "utest")
public class EventServiceExecutorsTest
{
	@BeforeMethod public void setup()
	{
		_errorHandler = createMock(ErrorHandler.class);
		_exceptionHandler = createMock(ConsumerExceptionHandler.class);
		_cleaner = createNiceMock(Cleaner.class);
		final Map<Class<? extends Annotation>, Provider<Executor>> executors =
			new HashMap<Class<? extends Annotation>, Provider<Executor>>();
		_exec1 = createMock(Executor.class);
		_exec2 = createMock(Executor.class);
		executors.put(InDeferredThread.class, new ExecutorProvider(_exec1));
		executors.put(InEDT.class, new ExecutorProvider(_exec2));
		AnnotationProcessorFactory factory = new AnnotationProcessorFactory()
		{
			public AnnotationProcessor create(Set<ChannelKey> channels)
			{
				return new AnnotationProcessor(_errorHandler, channels, executors);
			}
		};
		_service = new EventServiceImpl(factory, _exceptionHandler, _cleaner);
		_service.registerChannel(TypeLiteral.get(Integer.class), null);
	}
	
	public void checkConsumerWithTwoExecutors()
	{
		Consumer1 mock = createStrictMock(Consumer1.class);
		Consumer2 consumer = new Consumer2(mock);
		_service.registerConsumers(consumer);
		Channel<Integer> channel = 
			_service.getChannel(TypeLiteral.get(Integer.class), null);
		
		Capture<Runnable> capture1 = new Capture<Runnable>();
		_exec1.execute(EasyMock.capture(capture1));
		Capture<Runnable> capture2 = new Capture<Runnable>();
		_exec2.execute(EasyMock.capture(capture2));

		// Check that both executors were called during publish()
		replay(_exec1, _exec2);
		Integer event = 1;
		channel.publish(event);
		verify(_exec1, _exec2);
		
		mock.push(event, InDeferredThread.class);
		replay(mock);
		capture1.getValue().run();
		verify(mock);

		reset(mock);
		mock.push(event, InEDT.class);
		replay(mock);
		capture2.getValue().run();
		verify(mock);
	}
	
	static public interface Consumer1
	{
		public void push(Integer event, Class<?> annotation);
	}
	static public class Consumer2
	{
		Consumer2(Consumer1 mock)
		{
			_mock = mock;
		}
		@InDeferredThread @Consumes public void push1(Integer event)
		{
			_mock.push(event, InDeferredThread.class);
		}
		@InEDT @Consumes public void push2(Integer event)
		{
			_mock.push(event, InEDT.class);
		}
		final private Consumer1 _mock;
	}
	
	static public class ExecutorProvider implements Provider<Executor>
	{
		ExecutorProvider(Executor mock)
		{
			_mock = mock;
		}
		public Executor get()
		{
			return _mock;
		}
		
		private final Executor _mock;
	}

	private EventService _service;
	private ErrorHandler _errorHandler;
	private ConsumerExceptionHandler _exceptionHandler;
	private Cleaner _cleaner;
	private Executor _exec1;
	private Executor _exec2;
}
