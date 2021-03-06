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

package net.guts.gui.exit;

import net.guts.common.injection.AbstractSingletonModule;
import net.guts.common.injection.InjectionListeners;
import net.guts.common.injection.Matchers;
import net.guts.common.injection.OneTypeListener;
import net.guts.event.EventModule;
import net.guts.event.Events;

/**
 * Guice {@link com.google.inject.Module} for Guts-GUI Exit Management system. 
 * This module must be added to the list of modules passed to 
 * {@link com.google.inject.Guice#createInjector}:
 * <pre>
 * Injector injector = Guice.createInjector(new ExitModule(), ...);
 * </pre>
 * If you use Guts-GUI {@link net.guts.gui.application.AbstractApplication}, then
 * {@code ExitModule} is automatically added to the list of {@code Module}s used
 * by Guts-GUI to create Guice {@code Injector}.
 * <p/>
 * Hence you would care about {@code ExitModule} only if you intend to use 
 * Guts-GUI Exit management system but don't want to use the whole Guts-GUI 
 * framework.
 * 
 * @author Jean-Francois Poilpret
 */
public final class ExitModule extends AbstractSingletonModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		// Make sure EventModule is installed
		install(new EventModule());
		// Declare event published at shutdown time
		Events.bindChannel(binder(), Void.class, ExitController.SHUTDOWN_EVENT);
		// Add automatic listeners to automatically add ExitChecker instances to 
		// the ExitController
		ExitCheckerInjectionListener injectionListener = 
			InjectionListeners.requestInjection(
				binder(), new ExitCheckerInjectionListener());
		OneTypeListener<ExitChecker> typeListener = 
			new OneTypeListener<ExitChecker>(ExitChecker.class, injectionListener);
		bindListener(Matchers.isSubtypeOf(ExitChecker.class), typeListener);
	}
}
