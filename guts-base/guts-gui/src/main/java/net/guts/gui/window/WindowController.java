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

package net.guts.gui.window;

import javax.swing.RootPaneContainer;

import com.google.inject.ImplementedBy;

/**
 * Main service API that deals with windows display. Any {@link javax.swing.RootPaneContainer}
 * should be made visible through this service (rather than directly calling 
 * {@code setVisible(true)} on these.
 * <p/>
 * TODO working details ("WP plugins").
 * <p/>
 * Guts-GUI implementation of {@code WindowController} manages:
 * <ul>
 * <li>Automatic resource injection through 
 * {@link net.guts.gui.resource.ResourceInjector}</li>
 * <li>Automatic resource re-injection when {@link java.util.Locale} changes</li>
 * <li>Automatic GUI session state persistence through 
 * {@link net.guts.gui.session.SessionManager}</li>
 * <li>Location and size computation of displayed windows according to caller's 
 * request</li>
 * <li>Correct "stacking" of dialogs on windows</li>
 * </ul>
 * Whenever you need to display a window, you should {@code @Inject} 
 * {@code WindowController} and use its {@link #show} method.
 *
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(WindowControllerImpl.class)
public interface WindowController
{
	/**
	 * TODO rework this javadoc.
	 * Show the given {@code root} after setting its location and size according
	 * to {@code policy}. {@code root} will have its resources automatically 
	 * injected (according to {@link net.guts.gui.resource.ResourceInjector} 
	 * principles). Depending on {@code restoreState} value, {@code frame} GUI 
	 * session state will be restored if it was previously persisted; in this case, 
	 * {@code policy} has no effect.
	 * <p/>
	 * TODO sample code with building config correctly.
	 * 
	 * @param root the {@code RootPaneContainer} to be displayed
	 * @param config the configuration for displaying {@code root}
	 */
	public <T extends RootPaneContainer> void show(T root, RootPaneConfig<T> config);
}
