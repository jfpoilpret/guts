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

import java.lang.annotation.Annotation;

import org.easymock.classextension.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;
import static org.easymock.classextension.EasyMock.verify;

import net.guts.common.injection.InjectionListeners;
import net.guts.event.Channel;
import net.guts.event.Consumes;
import net.guts.event.EventModule;
import net.guts.event.Events;
import net.guts.event.InEDT;
import net.guts.event.InDeferredThread;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

@Test(groups = "itest")
public class GuiceEventServiceThreadsTest
{
	@BeforeMethod
	public void initInjectorChannelAndConsumers()
	{
		_handler = createMock(ConsumerExceptionHandler.class);
		_injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(_handler);
				Events.bindChannel(binder(), Integer.class);
			}
		});
		InjectionListeners.injectListeners(_injector);
		_channel = _injector.getInstance(Key.get(new TypeLiteral<Channel<Integer>>(){}));
		_consumer = _injector.getInstance(Consumer2.class);
		_consumer.setMock(_mock);
	}

	public void checkTwoThreadPolicies() throws Exception
	{
		Integer event = 1;
		reset(_mock);
		EasyMock.makeThreadSafe(_mock, true);
		_mock.push(event, InEDT.class);
		_mock.push(event, InDeferredThread.class);
		
		replay(_mock, _handler);
		_channel.publish(event);
		Thread.sleep(100);
		verify(_mock, _handler);
	}
	
	static public interface Consumer1
	{
		public void push(Integer event, Class<? extends Annotation> threadPolicy);
	}
	
	static public class Consumer2
	{
		public void setMock(Consumer1 mock)
		{
			_mock = mock;
		}

		@Consumes @InEDT
		public void push1(Integer event)
		{
			_mock.push(event, InEDT.class);
		}

		@Consumes @InDeferredThread
		public void push2(Integer event)
		{
			_mock.push(event, InDeferredThread.class);
		}
		
		private Consumer1 _mock;
	}

	private Injector _injector;
	private Channel<Integer> _channel;
	private ConsumerExceptionHandler _handler;
	private Consumer1 _mock = createMock(Consumer1.class);
	private Consumer2 _consumer;
}
