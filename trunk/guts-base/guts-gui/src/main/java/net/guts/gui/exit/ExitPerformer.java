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
 * Service that performs the last step of the actual shutdown of the application:
 * exiting the application!
 * <p/>
 * Default implementation simply performs {@code System.exit(0);}. You can override
 * this behavior by providing and binding your own implementation:
 * <pre>
 * bind(ExitPerformer.class).to(MyExitPerformer.class);
 * </pre>
 * This can be useful in some circumstances where you don't want your application to
 * quit by exiting the JVM (for instance if your application is being GUI-tested, 
 * exiting the JVM would mean exiting the tests runner itself!)
 * <p/>
 * In general however, you won't often need to override default behavior.
 *
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(DefaultExitPerformer.class)
public interface ExitPerformer
{
	/**
	 * This method performs the last step of shutting down the application. It is 
	 * called from the Event Dispatch Thread.
	 */
	public void exitApplication();
}
