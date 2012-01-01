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

/**
 * This package contains Guts system to manage the display of any window.
 * It defines two services:
 * <ul>
 * <li>{@link net.guts.gui.window.WindowController} service which you can 
 * inject in any of your own classes in order to initialize and display windows</li>
 * <li>{@link net.guts.gui.window.ActiveWindow} holds the currently active
 * window (the one in foreground)</li>
 * </ul>
 * In order to use these services, one must use {@link net.guts.gui.window.WindowModule} 
 * when creating Guice {@code Injector}, which is automatically done when using
 * {@link net.guts.gui.application.AbstractApplication}.
 * <p/>
 * The package also defines various utility classes that help build a 
 * {@link net.guts.gui.window.RootPaneConfig}, which is necessary when using
 * {@link net.guts.gui.window.WindowController#show}:
 * <ul>
 * <li>{@link net.guts.gui.window.JAppletConfig}</li>
 * <li>{@link net.guts.gui.window.JDialogConfig}</li>
 * <li>{@link net.guts.gui.window.JFrameConfig}</li>
 * <li>{@link net.guts.gui.window.JInternalFrameConfig}</li>
 * </ul>
 * <p/>
 * Finally, the package defines {@link net.guts.gui.window.WindowProcessor}
 * interface and various implementations, which perform the actual work of
 * {@code WindowController}.
 */
package net.guts.gui.window;
