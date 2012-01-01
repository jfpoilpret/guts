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

package net.guts.common.injection;

import java.util.Set;

import com.google.inject.Binder;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

/**
 * Utility class that allows better injection of 
 * {@link com.google.inject.spi.InjectionListener} implementations, ensuring 
 * they are operational <b>during</b> {@link Injector} creation. Since Guice 
 * guarantees no specific order of instances construction during {@link Injector} 
 * creation, it is possible that a bound {@link com.google.inject.spi.InjectionListener} 
 * instance is called after injection of a given object but <b>after</b> its own 
 * injection, which generally leads to a {@code NullPointerException}.
 * <p/>
 * The following snippet shows an example of an injectable {@code InjectionListener}:
 * <pre>
 * public class ConsumerInjectionListener extends AbstractInjectionListener&lt;Object&gt;
 * {
 *     &#64;Inject public void setEventService(EventService service)
 *     {
 *         _service = service;
 *     }
 *     
 *     &#64;Override protected void registerInjectee(Object injectee)
 *     {
 *         _service.registerConsumers(injectee);
 *     }
 * 
 *     private EventService _service = null;
 * }
 * </pre>
 * And here is an excerpt of how to declare this listener in a {@code Module}:
 * <pre>
 * ConsumerInjectionListener injectionListener =
 *     InjectionListeners.requestInjection(binder(), new ConsumerInjectionListener());
 * OneTypeListener&lt;Object&gt; typeListener =
 *     new OneTypeListener&lt;Object&gt;(Object.class, injectionListener);
 * bindListener(Matchers.hasMethodAnnotatedWith(Consumes.class), typeListener);
 * </pre>
 * And don't forget to call {@link #injectListeners(Injector)} immediately after
 * creating Guice {@code Injector}:
 * <pre>
 * Injector injector = Guice.createInjector(modules);
 * InjectionListeners.injectListeners(injector);
 * </pre>
 *
 * @see OneTypeListener
 * @see InjectableInjectionListener
 * @see AbstractInjectionListener
 * @author Jean-Francois Poilpret
 */
public final class InjectionListeners
{
	private InjectionListeners()
	{
	}
	
	static public <T, U extends InjectableInjectionListener<T>> U requestInjection(
		Binder binder, U listener)
	{
		Multibinder.newSetBinder(binder, LISTENER_TYPE).addBinding().toInstance(listener);
		return listener;
	}
	
	//CSOFF: EmptyBlock
	static public void injectListeners(Injector injector)
	{
		try
		{
			Set<InjectableInjectionListener<?>> listeners = 
				injector.getInstance(LISTENER_SET_KEY);
			for (InjectableInjectionListener<?> listener: listeners)
			{
				listener.flush();
			}
		}
		catch (ConfigurationException e)
		{
			// Getting this exception means that requestInjection() was never called
			// before, hence there's no registered listener to flush.
			// Do nothing at all
		}
	}
	//CSON: EmptyBlock

	static final private TypeLiteral<InjectableInjectionListener<?>> LISTENER_TYPE =
		new TypeLiteral<InjectableInjectionListener<?>>() {};
	static final private Key<Set<InjectableInjectionListener<?>>> LISTENER_SET_KEY =
		Key.get(new TypeLiteral<Set<InjectableInjectionListener<?>>>() {});
}
