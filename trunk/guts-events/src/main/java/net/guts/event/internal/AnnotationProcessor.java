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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
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
			// Analyze each consumer method
			Consumes consumes = m.getAnnotation(Consumes.class);
			if (consumes != null)
			{
				ChannelKey key = analyzeMethod(false, m, consumes.topic(), consumes.type());
				if (key != null)
				{
					// Temporarily store this info somewhere for later use by caller
					consumers.add(new Consumer(key, m, consumes.priority(), 
						searchThreadPolicy(m, key.getType(), key.getTopic()), 
						consumes.filterId()));
				}
			}
		}
		return consumers;
	}

	private Annotation searchThreadPolicy(Method method, Type type, String topic)
	{
		Annotation policy = null;
		for (Annotation annotation: method.getAnnotations())
		{
			if (_executors.containsKey(annotation.annotationType()))
			{
				if (policy == null)
				{
					policy = annotation;
				}
				else
				{
					_handler.handleError(
						ConsumerClassError.CONSUMES_CANNOT_HAVE_SEVERAL_THREAD_ANNOTATIONS, 
						method, type, topic);
					break;
				}
			}
		}
		return policy;
	}

	private List<Filter> findFilters(Class<?> clazz)
	{
		List<Filter> allFilters = new ArrayList<Filter>();
		for (Method m: clazz.getMethods())
		{
			// Analyze each consumer method
			Filters filters = m.getAnnotation(Filters.class);
			if (filters != null)
			{
				ChannelKey key = analyzeMethod(true, m, filters.topic(), filters.type());
				if (key != null)
				{
					// Temporarily store this info somewhere for later use
					allFilters.add(new Filter(key, m, filters.id()));
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
				if (	filter._key.equals(consumer._key)
					&&	filter._id.equals(consumer._idFilter))
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

	private ChannelKey analyzeMethod(boolean isFilter, Method m, String topic, Class<?> type)
	{
		// Check method is void and has one parameter
		if (isFilter && m.getReturnType() != boolean.class)
		{
			handleError(isFilter, InternalError.BAD_RETURN_TYPE, m, null, null);
			return null;
		}
		if (m.getParameterTypes().length != 1)
		{
			handleError(isFilter, InternalError.BAD_ARG_NUMBER, m, null, null);
			return null;
		}

		// Find type of event
		TypeLiteral<?> argType = TypeLiteral.get(m.getGenericParameterTypes()[0]);
		TypeLiteral<?> eventType = argType;
		if (type != SameAsArgumentType.class)
		{
			// Check that the declared type is a superclass of the argument type
			if (!type.isAssignableFrom(argType.getRawType()))
			{
				handleError(isFilter, InternalError.EXPLICIT_TYPE_NOT_ARG_SUPERTYPE, 
					m, type, topic);
				return null;
			}
			eventType = argType.getSupertype(type);
		}
		// Check that there exists one registered Event Channel of
		// this type!
		ChannelKey key = new ChannelKey(eventType.getType(), topic);
		if (!_channels.contains(key))
		{
			handleError(isFilter, InternalError.EVENT_NOT_REGISTERED, 
				m, eventType.getType(), topic);
			return null;
		}
		return key;
	}
	
	private void handleError(boolean isFilter,
		InternalError error, Method m, Type type, String topic)
	{
		_handler.handleError(error.getError(isFilter), m, type, topic);
	}

	//CSOFF: HideUtilityClassConstructorCheck
	static private class Consumer
	{
		Consumer(ChannelKey key, Method method, int priority, 
			Annotation threadPolicy, String idFilter)
		{
			_idFilter = idFilter;
			_key = key;
			_method = method;
			_priority = priority;
			_threadPolicy = threadPolicy;
		}
		
		final private String _idFilter;
		final private ChannelKey _key;
		final private Method _method;
		final private int _priority;
		final private Annotation _threadPolicy;
	}
	
	static private class Filter
	{
		Filter(ChannelKey key, Method method, String id)
		{
			_id = id;
			_key = key;
			_method = method;
		}

		final private String _id;
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
