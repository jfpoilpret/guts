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

package net.guts.gui.action;

import com.google.inject.ImplementedBy;

/**
 * Service in charge of registering {@code GutsAction} instances, so that they can
 * have their resources immediately injected (for i18n purposed) and automatically 
 * updated (when {@link java.util.Locale} changes).
 * <p/>
 * The actual implementation of {@code ActionRegistrationManager} service is bound 
 * with Guice by using {@link ActionModule}, and is hence injectable in your classes
 * if you need it.
 * <p/>
 * In general, you won't need to directly use {@code ActionRegistrationManager} if you
 * follow general conventions about {@code GutsAction} usage:
 * <ul>
 * <li>Declare {@code GutsAction} instances as fields (of type {@code GutsAction} or a 
 * subclass) in your classes</li>
 * <li>Make sure they are instantiated at construction time (best way is to declare
 * them {@code final} and initialize them where they are declared</li>
 * <li>Make your classes with {@code GutsAction} fields are injected by Guice</li>
 * </ul>
 * If you follow these conventions, then all your {@code GutsAction} will be 
 * automatically registered by {@code ActionRegistrationManager}.
 *
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(ActionRegistrationManagerImpl.class)
public interface ActionRegistrationManager
{
	/**
	 * Register all non-null {@code GutsAction} fields of {@code instance}.
	 * 
	 * @param instance the object containing {@code GutsAction} fields to register
	 */
	public void registerActions(Object instance);
	
	/**
	 * Register a {@code GutsAction}. This method is useful if you don't want to
	 * store a {@code GutsAction} into a field but only have it as a local variable;
	 * you can create the {@code GutsAction}, register it, then set it to Swing
	 * components.
	 * 
	 * @param action the action to register
	 */
	public void registerAction(GutsAction action);
}
