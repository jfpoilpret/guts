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

import static net.guts.common.type.PrimitiveHelper.toWrapper;
import net.guts.event.internal.ChannelProvider;
import net.guts.event.internal.EventImpl;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.util.Types;

/**
 * Utility class used to define bindings of event {@link Channel}s within a 
 * {@link com.google.inject.Module}. It is also used for defining bindings
 * of annotations (to be used by consumer methods) with 
 * {@link java.util.concurrent.Executor}.
 * 
 * @author Jean-Francois Poilpret
 */
public final class Events
{
	private Events()
	{
	}
	
	/**
	 * Creates a Guice {@link com.google.inject.Binding} for an event {@link Channel}, based
	 * on the type of event; no topic is associated to the newly bound {@link Channel}.
	 * <p/>
	 * This must be called from {@link com.google.inject.Module#configure(Binder)}.
	 * <p/>
	 * The bound channel can then be injected into any class instantiated by Guice 
	 * {@link com.google.inject.Injector}:
	 * <pre>
	 * &#64;Inject private Channel&lt;Integer&gt; channel;
	 * </pre>
	 * 
	 * @param <T> Type of the event of the newly bound {@link Channel}
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param eventType the exact type of the event for which to bind a {@link Channel}
	 */
	static public <T> void bindChannel(Binder binder, Class<T> eventType)
	{
		bindChannel(binder, TypeLiteral.get(eventType), null);
	}

	/**
	 * Creates a Guice {@link com.google.inject.Binding} for an event {@link Channel}, based
	 * on the type of event and the given topic.
	 * <p/>
	 * This must be called from {@link com.google.inject.Module#configure(Binder)}.
	 * <p/>
	 * The bound channel can then be injected into any class instantiated by Guice 
	 * {@link com.google.inject.Injector}:
	 * <pre>
	 * &#64;Inject &#64;Event(topic = TOPIC) private Channel&lt;Integer&gt; channel;
	 * </pre>
	 * Note that the {@link Event} binding annotation must be used to explicitly state the
	 * topic for the injected {@link Channel}.
	 * 
	 * @param <T> Type of the event of the newly bound {@link Channel}
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param eventType the exact type of the event for which to bind a {@link Channel}
	 * @param topic topic of the event {@link Channel} to be bound
	 */
	static public <T> void bindChannel(Binder binder, Class<T> eventType, String topic)
	{
		bindChannel(binder, TypeLiteral.get(eventType), topic);
	}

	/**
	 * Creates a Guice {@link com.google.inject.Binding} for an event {@link Channel}, based
	 * on the type of event; no topic is associated to the newly bound {@link Channel}.
	 * <p/>
	 * This must be called from {@link com.google.inject.Module#configure(Binder)}.
	 * <p/>
	 * The bound channel can then be injected into any class instantiated by Guice 
	 * {@link com.google.inject.Injector}:
	 * <pre>
	 * &#64;Inject private Channel&lt;List&lt;String&gt;&gt; channel;
	 * </pre>
	 * 
	 * @param <T> Type of the event of the newly bound {@link Channel}
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param eventType the exact type of the event for which to bind a {@link Channel}
	 */
	static public <T> void bindChannel(Binder binder, TypeLiteral<T> eventType)
	{
		bindChannel(binder, eventType, null);
	}

	/**
	 * Creates a Guice {@link com.google.inject.Binding} for an event {@link Channel}, based
	 * on the type of event and the given topic.
	 * <p/>
	 * This must be called from {@link com.google.inject.Module#configure(Binder)}.
	 * <p/>
	 * The bound channel can then be injected into any class instantiated by Guice 
	 * {@link com.google.inject.Injector}:
	 * <pre>
	 * &#64;Inject &#64;Event(topic = TOPIC) private Channel&lt;List&lt;String&gt;&gt; channel;
	 * </pre>
	 * Note that the {@link Event} binding annotation must be used to explicitly state the
	 * topic for the injected {@link Channel}.
	 * 
	 * @param <T> Type of the event of the newly bound {@link Channel}
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param eventType the exact type of the event for which to bind a {@link Channel}
	 * @param topic topic of the event {@link Channel} to be bound
	 */
	@SuppressWarnings("unchecked")
	static public <T> void bindChannel(Binder binder, TypeLiteral<T> eventType, String topic)
	{
		// Special handling of primitive event types
		Type realType = toWrapper(eventType.getRawType());
		boolean isPrimitive = (realType != eventType.getRawType());
		if (!isPrimitive)
		{
			realType = eventType.getType();
		}
		Type channelType = Types.newParameterizedType(Channel.class, realType);
		AnnotatedBindingBuilder<Channel<T>> binding = binder.bind(
			(TypeLiteral<Channel<T>>) TypeLiteral.get(channelType));
		if (topic != null && !"".equals(topic))
		{
			binding.annotatedWith(new EventImpl(topic, isPrimitive));
		}
		else if (isPrimitive)
		{
			binding.annotatedWith(new EventImpl("", true));
		}
		binding.toProvider(new ChannelProvider<T>(eventType, topic)).in(Scopes.SINGLETON);
	}

	/**
	 * Initializes a binding of a "Thread Policy" annotation to a 
	 * {@link java.util.concurrent.Executor}.
	 * <p/>
	 * This allows to define your own threading policy for events notification to 
	 * consumer methods.
	 * <p/>
	 * This is based on usual Guice EDSL for bindings:
	 * <pre>
	 * Events.bindExecutor(binder(), InEDT.class).to(InEDTExecutor.class);
	 * </pre>
	 * <p/>
	 * This must be called from {@link com.google.inject.Module#configure(Binder)}.
	 * 
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param threadPolicy type of an annotation that will be bound to {@code executor}
	 * @return a {@link com.google.inject.binder.LinkedBindingBuilder} to bind the 
	 * {@code threadPolicy} annotation to an {@link java.util.concurrent.Executor};
	 * all consumer methods annotated with {@code threadPolicy} will be called from this
	 * {@code Executor}
	 */
	static public LinkedBindingBuilder<Executor> bindExecutor(
		Binder binder, Class<? extends Annotation> threadPolicy)
	{
		MapBinder<Class<? extends Annotation>, Executor> map =
			MapBinder.newMapBinder(binder, EXECUTORS_KEY, EXECUTORS_VALUE);
		return map.addBinding(threadPolicy);
	}

	/**
	 * Initializes a binding from a consumer method return type to a 
	 * {@link ConsumerReturnHandler} for that type.
	 * <p/>
	 * This allows special processing of results returned by consumer methods.
	 * <p/>
	 * This is based on usual Guice EDSL for bindings:
	 * <pre>
	 * Events.bindHandler(binder(), new TypeLiteral&lt;SwingWorker&lt;?, ?&gt;&gt;{}())
	 *     .to(SwingWorkerHandler.class);
	 * </pre>
	 * <p/>
	 * This must be called from {@link com.google.inject.Module#configure(Binder)}.
	 * @see ConsumerReturnHandler
	 * 
	 * @param <T> Consumer methods return type to bind to a 
	 * {@code ConsumerReturnHandler<T>} 
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param returnType the exact type (declared as return types from consumer 
	 * methods) for which to bind a special {@code ConsumerReturnHandler<T>}
	 * @return a {@link com.google.inject.binder.LinkedBindingBuilder} to bind the 
	 * {@code returnType} of consumer methods to an {@link ConsumerReturnHandler}
	 */
	@SuppressWarnings("unchecked")
	static public <T> LinkedBindingBuilder<ConsumerReturnHandler<? extends T>> bindHandler(
		Binder binder, TypeLiteral<T> returnType)
	{
		LinkedBindingBuilder builder = getHandlerMap(binder).addBinding(returnType);
		return builder;
	}
			
	/**
	 * Initializes a binding from a consumer method return type to a 
	 * {@link ConsumerReturnHandler} for that type.
	 * <p/>
	 * This allows special processing of results returned by consumer methods.
	 * <p/>
	 * This is based on usual Guice EDSL for bindings:
	 * <pre>
	 * Events.bindHandler(binder(), SwingWorker.class).to(SwingWorkerHandler.class);
	 * </pre>
	 * <p/>
	 * This must be called from {@link com.google.inject.Module#configure(Binder)}.
	 * @see ConsumerReturnHandler
	 * 
	 * @param <T> Consumer methods return type to bind to a 
	 * {@code ConsumerReturnHandler<T>} 
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param returnType the exact type (declared as return types from consumer 
	 * methods) for which to bind a special {@code ConsumerReturnHandler<T>}
	 * @return a {@link com.google.inject.binder.LinkedBindingBuilder} to bind the 
	 * {@code returnType} of consumer methods to an {@link ConsumerReturnHandler}
	 */
	static public <T> LinkedBindingBuilder<ConsumerReturnHandler<? extends T>> bindHandler(
		Binder binder, Class<T> returnType)
	{
		return bindHandler(binder, TypeLiteral.get(returnType));
	}
			
	static MapBinder<TypeLiteral<?>, ConsumerReturnHandler<?>> getHandlerMap(Binder binder)
	{
		return MapBinder.newMapBinder(binder, HANDLERS_KEY, HANDLERS_VALUE);
	}

	static final private TypeLiteral<Class<? extends Annotation>> EXECUTORS_KEY = 
		new TypeLiteral<Class<? extends Annotation>>() {};
	static final private TypeLiteral<Executor> EXECUTORS_VALUE = 
		new TypeLiteral<Executor>() {};
	static final private TypeLiteral<TypeLiteral<?>> HANDLERS_KEY = 
		new TypeLiteral<TypeLiteral<?>>() {};
	static final private TypeLiteral<ConsumerReturnHandler<?>> HANDLERS_VALUE = 
		new TypeLiteral<ConsumerReturnHandler<?>>() {};
}
