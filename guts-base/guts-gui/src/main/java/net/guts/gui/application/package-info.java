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
 * This package contains the main classes and interfaces to be used to start a 
 * GUI application.
 * <p/>
 * It defines the following:
 * <ul>
 * <li>{@link net.guts.gui.application.AbstractApplication} which your application 
 * main class should derive from</li>
 * <li>{@link net.guts.gui.application.AppLifecycleStarter} interface which you
 * must implement to actually "start up" your UI</li>
 * </ul>
 * <p/>
 * {@code AbstractApplication} performs several initializations in order to put 
 * various Guts-GUI standard services at your disposal:
 * <ul>
 * <li>{@link net.guts.gui.window.WindowController}, an injectable service that
 * controls window display, making sure windows get their resources injected (by
 * {@link net.guts.gui.resource.ResourceInjector}) and their "geography" restored and
 * saved (by {@link net.guts.gui.session.SessionManager}</li>
 * <li>{@link net.guts.gui.exit.ExitController}, an injectable service that controls 
 * the shutdown of the application, and to which you can register 
 * {@link net.guts.gui.exit.ExitChecker} implementations that will be called
 * prior to any shutdown in order to possibly prevent it</li>
 * <li>{@link net.guts.gui.exception.HandlesException}, an annotation that
 * allows any method of any class, bound in a Guice {@link com.google.inject.Module},
 * to be notified of uncaught exceptions (uncaught exceptions may occur in the Event
 * Dispatch Thread and should be processed by any application, e.g. logging
 * the exception or showing a message to the user)</li>
 * <li>{@link net.guts.gui.application.GutsApplicationActions} an injectable class
 * that contains {@link net.guts.gui.action.GutsAction} for common actions (cut, copy,
 * paste, quit) that can be added to your menu bar</li>
 * <li>{@link net.guts.gui.action.ActionModule} and {@link net.guts.gui.task.TasksModule}
 * that allow effective {@link javax.swing.Action} definition with automatic resource 
 * injection</li>
 * </ul>
 */
package net.guts.gui.application;
