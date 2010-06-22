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
 * This package contains useful abstract {@link net.guts.gui.application.AppLifecycleStarter}
 * classes, relevant to different types of typical GUI applications:
 * <ul>
 * <li>Applications based on a single main frame (with a menu bar and a main content)</li>
 * <li>Applications based on multiple frames (without a "main" frame)</li>
 * <li>MDI-based (Multiple Document Interface) applications</li>
 * <li>Applications based on a main dialog (e.g. a wizard for an installer)</li>
 * </ul>
 * <p/>
 * All provided classes work the same way, they are all abstract and must thus be 
 * subclassed, then they must be bound, in a Guice {@link com.google.inject.Module}, to 
 * {@link net.guts.gui.application.AppLifecycleStarter}.
 */
package net.guts.gui.application.support;
