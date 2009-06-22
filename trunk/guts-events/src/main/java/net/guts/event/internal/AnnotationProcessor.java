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

package net.guts.event.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import net.guts.event.ConsumerClassError;
import net.guts.event.Consumes;
import net.guts.event.ErrorHandler;
import net.guts.event.Filters;
import net.guts.event.ThreadPolicy;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

public class AnnotationProcessor
{
	@Inject
	public AnnotationProcessor(ErrorHandler handler, @Assisted Set<ChannelKey> channels,
		Map<Class<? extends Annotation>, Provider<Executor>> executors)
	{
		_handler = handler;
		_channels = channels;
		_executors = executors;
	}
	
	public List<ConsumerFilter> process(Class<?> clazz)
	{
		// First check if clazz has been processed already
		List<ConsumerFilter> events = _inspectedClasses.get(clazz);
		if (events == null)
		{
			List<Consumer> consumers = findConsumers(clazz);
			List<Filter> filters = findFilters(clazz);
			events = mixConsumerAndFilters(consumers, filters);
			_inspectedClasses.put(clazz, events);
		}
		return events;
	}
	
	private List<Consumer> findConsumers(Class<?> clazz)
	{
		List<Consumer> consumers = new ArrayList<Consumer>();
		for (Method m: clazz.getMethods())
		{
			// Analyse each consumer method
			Consumes consumes = m.getAnnotation(Consumes.class);
			if (consumes == null)
			{
				continue;
			}
			// Check method is void and has one parameter
			if (m.getReturnType() != void.class)
			{
				_handler.handleError(
					ConsumerClassError.CONSUMES_MUST_RETURN_VOID, m, null, null);
			}
			else if (m.getParameterTypes().length != 1)
			{
				_handler.handleError(
					ConsumerClassError.CONSUMES_MUST_HAVE_ONE_ARG, m, null, null);
			}
			else
			{
				// Check that there exists one registered Event Channel of
				// this type!
				Type type = m.getGenericParameterTypes()[0];
				String topic = consumes.topic();
				ChannelKey key = new ChannelKey(type, topic);
				if (!_channels.contains(key))
				{
					_handler.handleError(
						ConsumerClassError.CONSUMES_EVENT_MUST_BE_REGISTERED, m, type, topic);
				}
				else
				{
					// Temporarily store this info somewhere for later use
					consumers.add(new Consumer(
						key, m, consumes.priority(), searchThreadPolicy(m)));
				}
			}
		}
		return consumers;
	}
	
	private Annotation searchThreadPolicy(Method method)
	{
		Annotation policy = null;
		for (Annotation annotation: method.getAnnotations())
		{
			if (annotation.annotationType().isAnnotationPresent(ThreadPolicy.class))
			{
				if (policy == null)
				{
					policy = annotation;
				}
				else
				{
					_handler.handleError(
						ConsumerClassError.CONSUMES_CANNOT_HAVE_SEVERAL_THREAD_ANNOTATIONS, 
						method, null, null);
					break;
				}
			}
		}
		return policy;
	}

	//TODO refactor commonalities between findConsumers/findFilters!
	private List<Filter> findFilters(Class<?> clazz)
	{
		List<Filter> allFilters = new ArrayList<Filter>();
		for (Method m: clazz.getMethods())
		{
			// Analyse each consumer method
			Filters filters = m.getAnnotation(Filters.class);
			if (filters == null)
			{
				continue;
			}
			// Check method is boolean and has one parameter
			if (m.getReturnType() != boolean.class && m.getReturnType() != Boolean.class)
			{
				_handler.handleError(
					ConsumerClassError.FILTERS_MUST_RETURN_BOOLEAN, m, null, null);
			}
			else if (m.getParameterTypes().length != 1)
			{
				_handler.handleError(
					ConsumerClassError.FILTERS_MUST_HAVE_ONE_ARG, m, null, null);
			}
			else
			{
				// Check that there exists one registered Event Channel of
				// this type!
				Type type = m.getGenericParameterTypes()[0];
				String topic = filters.topic();
				ChannelKey key = new ChannelKey(type, topic);
				if (!_channels.contains(key))
				{
					_handler.handleError(
						ConsumerClassError.FILTERS_EVENT_MUST_BE_REGISTERED, m, type, topic);
				}
				else
				{
					// Temporarily store this info somewhere for later use
					allFilters.add(new Filter(key, m));
				}
			}
		}
		return allFilters;
	}

	private List<ConsumerFilter> mixConsumerAndFilters(
		List<Consumer> consumers, List<Filter> filters)
	{
		List<ConsumerFilter> events = new ArrayList<ConsumerFilter>();
		// Check every consumer
		for (Consumer consumer: consumers)
		{
			// Find executor to be used
			Annotation policy = consumer._threadPolicy;
			Executor executor = null;
			if (policy != null)
			{
				executor = _executors.get(policy.annotationType()).get();
			}
			if (executor == null)
			{
				executor = _defaultExecutor;
			}
			
			boolean hasFilter = false;
			for (Filter filter: filters)
			{
				if (filter._key.equals(consumer._key))
				{
					events.add(new ConsumerFilter(consumer._key, consumer._method, 
						filter._method, consumer._priority, executor));
					hasFilter = true;
				}
			}
			// When there's no filter method, we should add the consumer only
			if (!hasFilter)
			{
				events.add(new ConsumerFilter(consumer._key, consumer._method, 
					null, consumer._priority, executor));
			}
		}
		return events;
	}
	
	//CSOFF: HideUtilityClassConstructorCheck
	static private class Consumer
	{
		Consumer(ChannelKey key, Method method, int priority, Annotation threadPolicy)
		{
			_key = key;
			_method = method;
			_priority = priority;
			_threadPolicy = threadPolicy;
		}
		
		final private ChannelKey _key;
		final private Method _method;
		final private int _priority;
		final private Annotation _threadPolicy;
	}
	
	static private class Filter
	{
		Filter(ChannelKey key, Method method)
		{
			_key = key;
			_method = method;
		}
		
		final private ChannelKey _key;
		final private Method _method;
	}
	//CSON: HideUtilityClassConstructorCheck

	final private ErrorHandler _handler;
	final private Map<Class<?>, List<ConsumerFilter>> _inspectedClasses = 
		new HashMap<Class<?>, List<ConsumerFilter>>();
	final private Set<ChannelKey> _channels;
	final private Map<Class<? extends Annotation>, Provider<Executor>> _executors;
	final private Executor _defaultExecutor = new InCurrentThreadExecutor();
}
