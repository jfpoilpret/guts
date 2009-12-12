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

package net.guts.gui.exception;

import net.guts.common.injection.InjectionListeners;
import net.guts.common.injection.Matchers;
import net.guts.common.injection.OneTypeListener;

import com.google.inject.AbstractModule;

/**
 * Guice {@link com.google.inject.Module} for Guts-GUI Exception Handling
 * system. This module must be added to the list of modules passed to 
 * {@link com.google.inject.Guice#createInjector}:
 * <pre>
 * Injector injector = Guice.createInjector(new ExceptionHandlingModule(), ...);
 * </pre>
 * If you use Guts-GUI {@link net.guts.gui.application.AbstractAppLauncher}, then
 * {@code ExceptionHandlingModule} is automatically added to the list of 
 * {@code Module}s used by Guts-GUI to create Guice {@code Injector}.
 * <p/>
 * Hence you would care about {@code ExceptionHandlingModule} only if you intend 
 * to use Guts-GUI Exception Handling system but don't want to use the whole 
 * Guts-GUI framework.
 * <p/>
 * Guts-GUI Exception Handling catches all exceptions thrown in the Event
 * Dispatch Thread and gives your application a chance to handle them in
 * any suitable way, thanks to {@link HandlesException} annotation.
 * <p/>
 * Any instance injected by Guice, if it contains methods annotated with
 * {@link HandlesException}, will be automatically registered with 
 * {@link ExceptionHandlerManager}, hence these methods will be notified of
 * uncaught exceptions.
 *
 * @author Jean-Francois Poilpret
 */
public final class ExceptionHandlingModule extends AbstractModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		// Add automatic listeners to automatically add HandlesException method
		// instances to the ExceptionHandlerManager
		ExceptionHandlerInjectionListener injectionListener = 
			InjectionListeners.requestInjection(
				binder(), new ExceptionHandlerInjectionListener());
		OneTypeListener<Object> typeListener = 
			new OneTypeListener<Object>(Object.class, injectionListener);
		bindListener(
			Matchers.hasMethodAnnotatedWith(HandlesException.class), typeListener);
		// Registers static injection of SwingExceptionHandler class
		requestStaticInjection(SwingExceptionHandler.class);
	}
}
