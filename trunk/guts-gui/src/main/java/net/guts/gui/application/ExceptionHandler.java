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

/**
 * Interface to be implemented by any class willing to handle exceptions that 
 * occur in the application.
 * <p/>
 * Any implementing class must then be added as a handler to the 
 * {@link ExceptionHandlerManager} service.
 * 
 * @author Jean-Francois Poilpret
 */
public interface ExceptionHandler
{
	/**
	 * This method is called whenever an exception is thrown by the application.
	 * 
	 * @param e the thrown exception
	 * @return {@code true} if the exception has been handled, {@code false} 
	 * otherwise (in this case, the next registered handler will be called)
	 */
	public boolean handle(Throwable e);
}
