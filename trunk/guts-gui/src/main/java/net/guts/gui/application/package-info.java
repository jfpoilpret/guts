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
 * <li>{@link net.guts.gui.application.AbstractAppLauncher} which your application 
 * main class should derive from</li>
 * <li>{@link net.guts.gui.application.AppLifecycleStarter} interface which you
 * must implement to actually "start up" your UI</li>
 * <li>{@link net.guts.gui.application.WindowController} service which you can 
 * inject in any of your own classes in order to initialize and display windows</li>
 * </ul>
 * <p/>
 * {@code AbstractAppLauncher} performs several initialization in order to put
 * various Guts-GUI standard services at your disposal:
 * <ul>
 * <li>{@link net.guts.gui.exit.ExitController}, an injectable service that controls 
 * the shutdown of the application, and to which you can register 
 * {@link net.guts.gui.exit.ExitChecker} implementations that will be called
 * prior to any shutdown in order to possibly prevent it</li>
 * <li>{@link net.guts.gui.exception.HandlesException}, an annotation that
 * allows any method of any class, bound in a Guice {@link com.google.inject.Module},
 * to be notified of uncaught exceptions (uncaught exceptions may occur in the Event
 * Dispatch Thread and should be processed by any application, e.g. logging
 * the exception or showing a message to the user)</li>
 * <li></li>
 * </ul>
 */
package net.guts.gui.application;
