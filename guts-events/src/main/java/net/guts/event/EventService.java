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

import net.guts.event.internal.EventServiceImpl;

import com.google.inject.ImplementedBy;
import com.google.inject.TypeLiteral;

/**
 * {@code EventService} is the core of GUTS-Events, although it is rather used under
 * the covers by GUTS-Events infrastructure and only seldom needs to be used directly.
 * This is a singleton automatically created when using {@link EventModule}.
 * <p/>
 * It is possible, however, to inject {@code EventService} in your own classes if you
 * really need it. This may be useful when you directly instantiate one class (without
 * using Guice) and this class has methods annotated with {@link Consumes}, hence you
 * want its instances to be registered as consumers for a given event, in which case 
 * you would use {@link #registerConsumers}.
 * 
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(EventServiceImpl.class)
public interface EventService
{
	/**
	 * Registers a new Event {@link Channel} for the given {@code type} and 
	 * {@code topic}. Once registered, you can get a reference to the new 
	 * {@link Channel} by calling {@link #getChannel}.
	 * <p/>
	 * If a {@link Channel} has already been registered for {@code type} and
	 * {@code topic}, then this method does nothing.
	 *  
	 * @param <T> type of the event managed by the newly registered 
	 * {@link Channel}
	 * @param type type of the event managed by the newly registered 
	 * {@link Channel}
	 * @param topic topic of the newly registered {@link Channel}; for an
	 * unnamed {@link Channel}, just pass {@code null} or {@code ""}.
	 */
	public<T> void registerChannel(TypeLiteral<T> type, String topic);
	
	/**
	 * Lazily evaluates and returns the Event {@link Channel} for the given
	 * {@code type} and {@code topic}. A {@link Channel} for {@code type} and
	 * {@code topic} must first have been registered with {@link #registerChannel}.
	 * 
	 * @param <T> type of the event managed by the {@link Channel} to return
	 * @param type type of the event managed by the {@link Channel} to return
	 * @param topic topic of event managed by the {@link Channel} to return; for an
	 * unnamed {@link Channel}, just pass {@code null} or {@code ""}.
	 * @return a {@code Channel} matching event {@code type} and {@code topic}
	 * @throws IllegalArgumentException if no {@link Channel} was registered for
	 * {@code type} and {@code topic}
	 */
	public<T> Channel<T> getChannel(TypeLiteral<T> type, String topic)
		throws IllegalArgumentException;

	/**
	 * Processes the given {@code instance} searching for methods annotated with
	 * {@link Consumes} and {@link Filters} and automatically registers the found
	 * consumer methods with the matching {@link Channel}s.
	 * <p/>
	 * You don't need to call this method for instances that have been created by
	 * Guice; however, if you want to process instances you have directly 
	 * instantiated, then you must use this method.
	 * <p/>
	 * Note that {@code instance} processing may generate errors (misuse of
	 * {@link Consumes} and {@link Filters} annotations). Errors are processed by
	 * the Guice-bound instance of {@link ErrorHandler}; default implementation
	 * (if you don't explicitly set it in one of your {@link Module}s) will simply 
	 * throw {@link IllegalArgumentException}.
	 * <p/>
	 * You can define your own implementation of {@link ErrorHandler} in your Guice
	 * {@link Module}:
	 * <pre>
	 * &#64;Override protected void configure()
	 * {
	 *     bind(ErrorHandler.class).to(MyErrorHandler.class);
	 * }
	 * </pre>
	 * 
	 * @param instance the object which consumer methods must be processed and 
	 * registered with matching {@link Channel}s
	 */
	public void registerConsumers(Object instance);
}
