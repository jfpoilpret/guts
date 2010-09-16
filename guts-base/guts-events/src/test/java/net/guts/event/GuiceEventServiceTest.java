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
import java.util.List;
import java.util.concurrent.Executor;

import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

import net.guts.common.injection.InjectionListeners;
import net.guts.event.Channel;
import net.guts.event.Consumes;
import net.guts.event.EventModule;
import net.guts.event.EventService;
import net.guts.event.Events;
import net.guts.event.Event;
import net.guts.event.internal.AnnotationProcessor;
import net.guts.event.internal.AnnotationProcessorFactory;
import net.guts.event.internal.ChannelFactory;
import net.guts.event.internal.ChannelImpl;

import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;
import com.google.inject.multibindings.MapBinder;

@Test(groups = "itest")
public class GuiceEventServiceTest
{
	@Test(expectedExceptions = ConfigurationException.class)
	public void checkEventServiceRequiresExceptionHandler()
	{
		Guice.createInjector().getInstance(EventService.class);
	}

	public void checkEventServiceInjection()
	{
		EventService service = Guice.createInjector(_executorModule)
			.getInstance(EventService.class);
		assertThat(service).as("EventService").isNotNull();
	}

	public void checkChannelsBindings()
	{
		_injector = Guice.createInjector(_executorModule, new AbstractModule()
		{
			@Override protected void configure()
			{
				Events.bindChannel(binder(), Integer.class);
				Events.bindChannel(binder(), _typeListString);
			}
		});
	}
	
	@Test(dependsOnMethods = "checkChannelsBindings")
	public void checkIntegerChannelInjection()
	{
		EventService service = _injector.getInstance(EventService.class);
		assertThat(service).as("EventService").isNotNull();
		Channel<Integer> channel = _injector.getInstance(Key.get(_typeChannelInt));
		assertThat(channel).as("Channel<Integer>").isNotNull();
	}

	@Test(dependsOnMethods = "checkChannelsBindings")
	public void checkListStringChannelInjection()
	{
		EventService service = _injector.getInstance(EventService.class);
		assertThat(service).as("EventService").isNotNull();
		Channel<List<String>> channel = _injector.getInstance(Key.get(_typeChannelListString));
		assertThat(channel).as("Channel<List<String>>").isNotNull();
	}
	
	public void checkEventModule()
	{
		EventService service = Guice.createInjector(new EventModule())
			.getInstance(EventService.class);
		assertThat(service).as("EventService").isNotNull();
	}

	public void checkEventModuleConsumerProcessing()
	{
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				Events.bindChannel(binder(), Integer.class);
				Events.bindChannel(binder(), _typeListString);
			}
		});
		InjectionListeners.injectListeners(injector);
		Consumer1 consumer = injector.getInstance(Consumer1.class);
		assertThat(consumer).as("Consumer").isNotNull();
	}
	
	static public class Consumer1
	{
		@Consumes public void push(Integer event)
		{
		}
	}

	public void checkTopicChannelInjection()
	{
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				Events.bindChannel(binder(), Integer.class);
				Events.bindChannel(binder(), Integer.class, TOPIC);
			}
		});
		InjectionListeners.injectListeners(injector);
		ChannelInjected instance = injector.getInstance(ChannelInjected.class);
		assertThat(instance).as("ChannelInjected").isNotNull();
		assertThat(instance._topicChannel).as("injected topic channel").isNotNull();
		assertThat(instance._typeChannel).as("injected type channel").isNotNull();
		assertThat(instance._topicChannel).as("injected topic channel").isNotSameAs(instance._typeChannel);
	}
	
	static public class ChannelInjected
	{
		@Inject public ChannelInjected(
			Channel<Integer> typeChannel, @Event(topic = TOPIC) Channel<Integer> topicChannel)
		{
			_typeChannel = typeChannel;
			_topicChannel = topicChannel;
		}
		
		final public Channel<Integer> _typeChannel;
		final public Channel<Integer> _topicChannel;
	}

	static private final String TOPIC = "topic";
	
	private TypeLiteral<Channel<Integer>> _typeChannelInt = 
		new TypeLiteral<Channel<Integer>>(){};
	private TypeLiteral<Channel<List<String>>> _typeChannelListString = 
		new TypeLiteral<Channel<List<String>>>(){};
	private TypeLiteral<List<String>> _typeListString = 
		new TypeLiteral<List<String>>(){};
	private Injector _injector;
	private Module _executorModule = new AbstractModule()
	{
		@Override protected void configure()
		{
			MapBinder.newMapBinder(binder(), 
				new TypeLiteral<Class<? extends Annotation>>() {}, 
				new TypeLiteral<Executor>() {});
			MapBinder.newMapBinder(binder(), 
				new TypeLiteral<TypeLiteral<?>>() {}, 
				new TypeLiteral<ConsumerReturnHandler<?>>() {});
			bind(AnnotationProcessorFactory.class).toProvider(FactoryProvider.newFactory(
				AnnotationProcessorFactory.class, AnnotationProcessor.class));
			bind(ChannelFactory.class).toProvider(FactoryProvider.newFactory(
				ChannelFactory.class, ChannelImpl.class));
		}
	};
}
