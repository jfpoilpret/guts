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
 * This package contains classes and interfaces that enable GUI exception handling
 * in a Guts-GUI application.
 * <p/>
 * It defines the following:
 * <ul>
 * <li>{@link net.guts.gui.exception.HandlesException} with which you can annotate 
 * a method so that it will be notified of any exception received by the GUI 
 * framework but never caught by any of your own event listeners.</li>
 * <li>{@link net.guts.gui.exception.ExceptionHandlingModule} which must be added
 * to the list of Guice {@link com.google.inject.Module}s used by your application.</li>
 * </ul>
 */
package net.guts.gui.exception;
