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
 * This package contains classes to handle {@link javax.swing.JMenu} and 
 * {@link javax.swing.JPopupMenu} creation based on names of {@code @Action}s 
 * annotated methods, managed by the {@link net.guts.gui.action.ActionManager} 
 * service.
 * <p/>
 * The package just defines one single {@link net.guts.gui.menu.MenuFactory} 
 * service. You can inject this service through Guice wherever you need by simply 
 * using Guice {@link com.google.inject.Inject} annotation:
 * <pre>
 * &#64;Inject private MenuManager _menuManager;
 * </pre>
 * but you might not always need that since it is already injected into 
 * {@link net.guts.gui.application.AbstractGuiceApplication} where you should 
 * use it to construct your main menu bar (in the 
 * {@link org.jdesktop.application.Application#startup()} method).
 * <p/>
 * Default implementation details can be found in package 
 * {@link net.guts.gui.menu.impl}.
 */
package net.guts.gui.menu;
