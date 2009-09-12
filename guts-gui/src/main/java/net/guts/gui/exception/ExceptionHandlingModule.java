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
 * TODO
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
