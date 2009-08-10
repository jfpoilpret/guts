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

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.createMock;
import static org.fest.assertions.Assertions.assertThat;

import net.guts.event.Consumes;
import net.guts.event.EventModule;
import net.guts.event.Events;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

@Test(groups = "itest")
public class EventModuleTypeListenerBugTest
{
	public void checkTypeListenerIsAlwaysInjected()
	{
		final ConsumerExceptionHandler handler = createMock(ConsumerExceptionHandler.class);
		EasyMock.replay(handler);
		final InjectedMain main = new InjectedMain();
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(handler);
				Events.bindChannel(binder(), Integer.class);
				bind(InjectedMain.class).toInstance(main);
			}
		});
		assertThat(main._consumer).as("Injected consumer").isNotNull();
	}
	static public class Consumer
	{
		@Consumes public void push(Integer event) {}
	}
	static public class InjectedMain
	{
		@Inject public Consumer _consumer;
		// Injecting the Channel is required so that it registers itself to the EventService
		@Inject public Channel<Integer> _channel;
	}
}
