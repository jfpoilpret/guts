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
 * This package contains classes to manage {@code @Action}s used in GUI 
 * application based on the framework.
 * <p/>
 * The package just defines one single {@link net.guts.gui.action.ActionManager} 
 * service. You can inject this service through Guice wherever you need by simply 
 * using Guice {@link com.google.inject.Inject} annotation:
 * <pre>
 * &#64;Inject private ActionManager _actionManager;
 * </pre>
 * <p/>
 * Default implementation details can be found in package 
 * {@link net.guts.gui.action.impl}.
 */
package net.guts.gui.action;
