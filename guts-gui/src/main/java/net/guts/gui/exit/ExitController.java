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

import com.google.inject.ImplementedBy;

/**
 * This Guts-GUI service allows any part of the application to request the 
 * application shutdown.
 * 
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(ExitControllerImpl.class)
public interface ExitController
{
	/**
	 * This is the name of the event sent (through guts-events) just before the 
	 * application shuts down. It gives all consumers of that event a last chance 
	 * to clean up before the application exits.
	 * <p/>
	 * TODO example of consumer method (type =  Void.class?)
	 * TODO check if guts-event supports consumer method with no args?
	 */
	static final public String SHUTDOWN_EVENT = "net.guts.gui.exit.ExitController.shutdown";
	
	/**
	 * Request application shutdown. All exit process is performed from inside 
	 * the EDT. The method first checks with all registered exit listener methods
	 * (methods annotated with {@link ExitChecker}) that shutdown is allowed.
	 * <p/>
	 * Note that it is perfectly possible that this method returns before shutdown
	 * has occurred yet.
	 */
	public void shutdown();
	
	/**
	 * Registers a new {@link ExitChecker} implementing object, to be called 
	 * before application shutdown can take place.
	 * <p/>
	 * @param checker the object to be called before application shutdown;
	 * {@code ExitController} will retain a weak reference to this object,
	 * hence it is the caller's responsibility to hold a strong reference to
	 * {@code checker} to ascertain that {@link ExitChecker#acceptExit()} method 
	 * will be called prior to application shutdown.
	 */
	public void registerExitChecker(ExitChecker checker);
}
