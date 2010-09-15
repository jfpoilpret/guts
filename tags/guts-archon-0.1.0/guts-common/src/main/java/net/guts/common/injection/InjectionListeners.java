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
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

/**
 * 
 *
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
	
	static public void injectListeners(Injector injector)
	{
		Set<InjectableInjectionListener<?>> listeners = injector.getInstance(LISTENER_SET_KEY);
		for (InjectableInjectionListener<?> listener: listeners)
		{
			listener.flush();
		}
	}

	static final private TypeLiteral<InjectableInjectionListener<?>> LISTENER_TYPE =
		new TypeLiteral<InjectableInjectionListener<?>>() {};
	static final private Key<Set<InjectableInjectionListener<?>>> LISTENER_SET_KEY =
		Key.get(new TypeLiteral<Set<InjectableInjectionListener<?>>>() {});
}
