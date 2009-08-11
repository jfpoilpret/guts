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

package net.guts.gui.application;

import net.guts.gui.application.impl.DefaultExceptionHandlerManager;

import com.google.inject.ImplementedBy;

/**
 * Global manager for handling exceptions in the application. It is automatically
 * called by the GUI framework when an exception occurs. It does not handle
 * exceptions by itself but delegates this to all 
 * {@link ExceptionHandler}s that have been registered with it.
 * <p/>
 * All registered {@link ExceptionHandler}s are chained in the order they
 * were added. Any caught exception is sent to the first registered handler, the
 * second, the third... until one handler actually claims to have handled the
 * exception, in which case, the remaining handlers are not called.
 * <p/>
 * This service is directly injectable through Guice facilities.
 * 
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(DefaultExceptionHandlerManager.class)
public interface ExceptionHandlerManager extends ExceptionHandler
{
	/**
	 * Adds a new {@link ExceptionHandler} implementation to the chain of
	 * handlers.
	 * 
	 * @param handler a new exception handler instance
	 */
	public void addExceptionHandler(ExceptionHandler handler);
}
