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
import java.lang.reflect.Type;
import java.util.concurrent.Executor;

import net.guts.event.internal.ChannelProvider;
import net.guts.event.internal.TopicImpl;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.util.Types;

public final class Events
{
	private Events()
	{
	}
	
	static public <T> void bindChannel(Binder binder, Class<T> eventType)
	{
		bindChannel(binder, TypeLiteral.get(eventType), null);
	}

	static public <T> void bindChannel(Binder binder, Class<T> eventType, String topic)
	{
		bindChannel(binder, TypeLiteral.get(eventType), topic);
	}

	static public <T> void bindChannel(Binder binder, TypeLiteral<T> eventType)
	{
		bindChannel(binder, eventType, null);
	}

	@SuppressWarnings("unchecked")
	static public <T> void bindChannel(Binder binder, TypeLiteral<T> eventType, String topic)
	{
		Type channelType = Types.newParameterizedType(Channel.class, eventType.getType());
		AnnotatedBindingBuilder<Channel<T>> binding = binder.bind(
			(TypeLiteral<Channel<T>>) TypeLiteral.get(channelType));
		if (topic != null && !"".equals(topic))
		{
			binding.annotatedWith(new TopicImpl(topic));
		}
		binding.toProvider(new ChannelProvider<T>(eventType, topic)).in(Scopes.SINGLETON);
	}
	
	static public void bindExecutor(
		Binder binder, Class<? extends Annotation> threadPolicy, Executor executor)
	{
		// First check that threadPolicy annotation is itself annotated with @ThreadPolicy
		if (threadPolicy.isAnnotationPresent(ThreadPolicy.class))
		{
			MapBinder<Class<? extends Annotation>, Executor> map =
				MapBinder.newMapBinder(binder, EXECUTORS_KEY, EXECUTORS_VALUE);
			map.addBinding(threadPolicy).toInstance(executor);
		}
		else
		{
			binder.addError(MSG_EXECUTOR_BINDING, threadPolicy);
		}
	}

	static final private String MSG_EXECUTOR_BINDING =
		"GUTS-Events cannot bind %s annotation to an Executor:\n" + 
		"it is not annotated with @ThreadPolicy\n";
	static final private TypeLiteral<Class<? extends Annotation>> EXECUTORS_KEY = 
		new TypeLiteral<Class<? extends Annotation>>() {};
	static final private TypeLiteral<Executor> EXECUTORS_VALUE = 
		new TypeLiteral<Executor>() {};
}
