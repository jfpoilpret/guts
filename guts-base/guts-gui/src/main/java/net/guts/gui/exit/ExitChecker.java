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

/**
 * Interface you should implement if you want to be notified of attempts to
 * shutdown the application (by {@link ExitController#shutdown()}. Implementing
 * classes will have the option to abort a shutdown attempt. These classes
 * must be registered with {@link ExitController}, either explicitly with
 * {@link ExitController#registerExitChecker} or automatically for instances
 * injected by Guice.
 *
 * @author Jean-Francois Poilpret
 */
public interface ExitChecker
{
	/**
	 * Notified of a shutdown attempt, this method shall check whether shutdown
	 * can be authorized or not, possibly by first asking user feedback.
	 * <p/>
	 * Note that although this method may return {@code true} to accept shutdown,
	 * this doesn't mean that shutdown will actually occur, because other 
	 * {@link ExitChecker}s may be registered and refuse shutdown; hence never
	 * take any action in this method that depends on actual application shutdown!
	 * For that, you can use {@link ExitController#SHUTDOWN_EVENT}.
	 * <p/>
	 * This method is always called from the Event Dispatch Thread, hence:
	 * <ul>
	 * <li>you should avoid long processing here</li>
	 * <li>you can act on UI (e.g. show a message box) from here</li>
	 * <ul>
	 * 
	 * @return {@code true} if shutdown is authorized, {@code false} if shutdown 
	 * must be aborted
	 */
	public boolean acceptExit();
}
