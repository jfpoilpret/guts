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

package net.guts.gui.action;

import net.guts.common.injection.InjectionListeners;
import net.guts.common.injection.Matchers;
import net.guts.common.injection.OneTypeListener;
import net.guts.gui.resource.ResourceModule;
import net.guts.gui.resource.Resources;

import com.google.inject.AbstractModule;

/**
 * Guice {@link com.google.inject.Module} for Guts-GUI action management system. 
 * This module must be added to the list of modules passed to 
 * {@link com.google.inject.Guice#createInjector}:
 * <pre>
 * Injector injector = Guice.createInjector(new ActionModule(), ...);
 * </pre>
 * If you use Guts-GUI {@link net.guts.gui.application.AbstractAppLauncher}, then
 * {@code ActionModule} is automatically added to the list of {@code Module}s used
 * by Guts-GUI to create Guice {@code Injector}.
 * <p/>
 * Hence you would care about {@code ActionModule} only if you intend to use 
 * Guts-GUI action management system but don't want to use the whole Guts-GUI 
 * framework.
 * <p/>
 * {@code ActionModule} makes sure that objects, when injected by Guice, if they
 * contain {@link GutsAction} fields, will be automatically registered with
 * {@link ActionRegistrationManager}.
 * <p/>
 * {@code ActionModule} also installs {@link ResourceModule} which it depends on.
 *
 * @author Jean-Francois Poilpret
 */
public final class ActionModule extends AbstractModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		// Make sure Resource Injection system is installed
		install(new ResourceModule());
		// Add special Injector for GutsAction
		Resources.bindInstanceInjector(binder(), GutsAction.class)
			.to(GutsActionInjector.class).asEagerSingleton();

		// Add type listener to automatically register Action fields 
		// of Guice-instantiated objects
		ActionRegisterInjectionListener injectionListener = 
			InjectionListeners.requestInjection(
				binder(), new ActionRegisterInjectionListener());
		OneTypeListener<Object> typeListener = 
			new OneTypeListener<Object>(Object.class, injectionListener);
		bindListener(Matchers.hasFieldsOfType(GutsAction.class), typeListener);
	}

	@Override public boolean equals(Object other)
	{
		return other instanceof ActionModule;
	}

	@Override public int hashCode()
	{
		return ActionModule.class.hashCode();
	}
}
