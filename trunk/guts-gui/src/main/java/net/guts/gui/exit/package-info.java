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
 * This package contains classes and interfaces that centralizes management of
 * the exit of a Guts-GUI application.
 * <p/>
 * Guts-GUI Exit management system is quite simple:
 * <ul>
 * <li>{@link ExitController} is used to request application shutdown</li>
 * <li>{@link ExitChecker} instances, registered with {@link ExitController},
 * are called in sequence to check if shutdown can be authorized or not</li>
 * <li>if shutdown can proceed, then {@link ExitController#SHUTDOWN_EVENT} event
 * is sent to all registered {@link net.guts.event.Consumes} methods, as a last
 * chance opportunity to perform cleanup before actual shutdown</li>
 * <li>Finally, {@link ExitPerformer} is called to exit the application (and the JVM)</li>
 * </ul>
 */
package net.guts.gui.exit;
