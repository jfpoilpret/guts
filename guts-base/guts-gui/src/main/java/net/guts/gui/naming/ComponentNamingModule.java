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

package net.guts.gui.naming;

import java.awt.Component;

import net.guts.common.injection.AbstractSingletonModule;
import net.guts.common.injection.InjectionListeners;
import net.guts.common.injection.Matchers;
import net.guts.common.injection.OneTypeListener;

/**
 * Guice {@link com.google.inject.Module} for Guts-GUI component automatic
 * naming feature. 
 * This module must be added to the list of modules passed to 
 * {@link com.google.inject.Guice#createInjector}:
 * <pre>
 * Injector injector = Guice.createInjector(new ComponentNamingModule(), ...);
 * </pre>
 * Once installed, this module will, for any {@link java.awt.Component} injected by 
 * Guice, call {@link ComponentNamingService#setComponentName} in order to ensure
 * automatic naming of the injected {@code Component} and all its fields.
 * <p/>
 * If the naming policy implemented by {@link ComponentNamingService} default 
 * implementation doesn't fit your needs, you can bind your own implementation:
 * <pre>
 * bind(ComponentNamingService.class).to(MyNamingService.class);
 * </pre>
 * However, the default {@link ComponentNamingService} offers better and easier 
 * options for component naming customization.
 * <p/>
 * <b>Important!</b> Note that {@code ComponentNamingModule} is not part of the 
 * modules automatically registered when using Guts-GUI 
 * {@link net.guts.gui.application.AbstractApplication}, you have to add it to the
 * {@code Module}s list explicitly.
 *
 * @see ComponentNamingService
 * @author Jean-Francois Poilpret
 */
public final class ComponentNamingModule extends AbstractSingletonModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		// Add type listener to automatically register Guice-instantiated objects
		// of Component type
		ComponentNamingInjectionListener injectionListener = 
			InjectionListeners.requestInjection(
				binder(), new ComponentNamingInjectionListener());
		OneTypeListener<Component> typeListener = 
			new OneTypeListener<Component>(Component.class, injectionListener);
		bindListener(Matchers.isSubtypeOf(Component.class), typeListener);
	}
}
