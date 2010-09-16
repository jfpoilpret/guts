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
 * This package contains the API that allows automatic naming of 
 * {@link java.awt.Component}s based on the names of fields that hold them.
 * <p/>
 * Naming is done through {@link java.awt.Component#setName}.
 * <p/>
 * In order to enable this feature, you just need to add 
 * {@link net.guts.gui.naming.ComponentNamingModule} to your list of Guice 
 * {@link com.google.inject.Module}s.
 * <p/>
 * When this feature is enabled, any {@link java.awt.Component} subclass instance,
 * injected by Guice:
 * <ul>
 * <li>will have its name automatically set (if {@code null});</li>
 * <li>all its fields of type {@link java.awt.Component} will have their names 
 * automatically set (if {@code null});</li>
 * <li>all its fields of type {@link net.guts.gui.naming.ComponentHolder} will be
 * recursively searched for fields of type {@code Component} or 
 * {@code ComponentHolder}, and all {@code Component} fields will have their
 * names set (if {@code null}).</li>
 * </ul>
 * The default way {@code Component} names are built is defined in 
 * {@link net.guts.gui.naming.DefaultComponentNamePolicy}.
 * <p/>
 * You can, if needed, customize this automatic component naming feature in 
 * several ways:
 * <ul>
 * <li>Override the binding of {@link net.guts.gui.naming.ComponentNamePolicy}: 
 * this is the easiest solution and is generally sufficient</li>
 * <li>Override the binding of {@link net.guts.gui.naming.ComponentNamingService}:
 * this way is much more complex because it puts you in charge of the whole
 * work of traversing components hierarchy and building the names on your own.</li>
 * </ul>
 */
package net.guts.gui.naming;
