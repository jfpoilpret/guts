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
 * This package contains classes to help managing {@link javax.swing.Action} used 
 * in GUI application based on the Guts-GUI framework.
 * <p/>
 * Guts-gui {@code action} package is based on {@link net.guts.gui.action.GutsAction}
 * abstract base class, to be used for all GUI actions.
 * <p/>
 * Actual {@code GutsAction} instances must be registered with 
 * {@link net.guts.gui.action.ActionRegistrationManager} to make sure that their
 * localization resources are properly injected (by 
 * {@link net.guts.gui.resource.ResourceInjector}).
 * <p/>
 * Guts-GUI will automatically find out any {@code GutsAction} field of classes
 * instantiated (or injected) by Guice, and register them with 
 * {@code ActionRegistrationManager}.
 * <p/>
 * The only thing to do to get those {@code GutsAction} fields registered is to create
 * Guice {@link com.google.inject.Injector} with {@link net.guts.gui.action.ActionModule},
 * which is done automatically by {@link net.guts.gui.application.AbstractAppLauncher}
 * when using the whole Guts-GUI framework.
 * <p/>
 * This package also defines two convenient {@code GutsAction} subclasses,
 * {@link net.guts.gui.action.TaskAction} and {@link net.guts.gui.action.TasksGroupAction},
 * that provide a link from the Guts-GUI {@code action} package to the 
 * {@link net.guts.gui.task} package.
 * <p/>
 * Typically you would create a GutsAction wherever you need it in your application, that 
 * could be inside a panel, in a separate class containing all actions for a given 
 * context... What is perfectly acceptable is to create a {@code public final GutsAction} 
 * field in a class so that it can be used in other places of your application to add it
 * to a menu item, a button...
 * <pre>
 * public class CustomerActions
 * {
 *     final public GutsAction createCustomer = new GutsAction("create-customer") {
 *         &#64;Override public void perform() {
 *             // Show a dialog to create a new customer here
 *         }
 *     };
 *     
 *     final public GutsAction deleteCustomer = new GutsAction("delete-customer") {
 *         ...
 *     }
 *     ...
 * }
 * ...
 * // In a method intializing the menu bar...
 * CustomerActions actions = getCustomerActions();
 * customerMenu.add(actions.createCustomer.action());
 * customerMenu.add(actions.deleteCustomer.action());
 * ...
 * </pre>
 * In the snippet above, you should note the following:
 * <ul>
 * <li>You must name every GutsAction: that name is used for internationalization 
 * (injection of resources -text, icon, etc- into the {@code Action}.</li>
 * <li>GutsAction has an {@code action()} method that returns a {@link javax.swing.Action}
 * that can be passed to Swing components.</li>
 * </ul>
 * 
 * @see net.guts.gui.action.TaskAction
 */
package net.guts.gui.action;
