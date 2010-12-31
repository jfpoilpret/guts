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
 * Default implementation relies on a chain of {@link WindowProcessor}s, each being
 * registered with Guice through {@link Windows#bindWindowProcessor}.
 * <p/>
 * Thus, every single aspect of preparing and showing a {@code RootPaneContainer} is 
 * handled by a specific {@code WindowProcessor} implementation.
 * <p/>
 * You can add your own {@link WindowProcessor}s if you need, but you can't change
 * any pre-registered one.
 * <p/>
 * Based on various GUTS {@link com.google.inject.Module}s that are used to create the
 * Guice {@code Injector}, {@code WindowController} default implementation will manage:
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
	 * Show the given {@code root} after preparation work according to {@code config},
	 * eg setting its location and size, injecting resources...
	 * <p/>
	 * The following snippet shows how to show a frame:
	 * <pre>
	 * JFrame myFrame = new JFrane();
	 * ...
	 * windowController.show(myFrame, 
	 *     JFrameConfig.create().bounds(BoundsPolicy.PACK_ONLY).config());
	 * </pre>
	 * 
	 * @param root the {@code RootPaneContainer} to be displayed; this can be a
	 * {@link javax.swing.JFrame}, {@link javax.swing.JDialog}, {@link javax.swing.JApplet},
	 * {@link javax.swing.JInternalFrame} or {@link javax.swing.JWindow}.
	 * @param config the configuration for displaying {@code root}; this must be created
	 * with one of {@link JFrameConfig}, {@link JDialogConfig}, {@link JAppletConfig}
	 * or {@link JInternalFrameConfig} builder classes.
	 */
	public <T extends RootPaneContainer> void show(T root, RootPaneConfig<T> config);
}
