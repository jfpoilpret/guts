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
 * This interface defines a policy for naming {@link GutsAction}s fields in any
 * class instance when injected by Guice, as used by {@link ActionRegistrationManager}
 * default implementation. Only {@code GustAction} fields which have not been named 
 * have their name automatically set.
 * <p/>
 * <b>Important!</b> Please note that only fields declared to be of {@code GutsAction} 
 * type (or any subclass) will be automatically named when needed.
 * <p/>
 * {@link DefaultActionNamePolicy} is the default implementation of
 * {@code ActionNamePolicy} but can be overridden easily in one of your
 * Guice {@link com.google.inject.Module}s:
 * <pre>
 * bind(ActionNamePolicy.class).to(MyActionNamePolicy.class);
 * </pre> 
 *
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(DefaultActionNamePolicy.class)
public interface ActionNamePolicy
{
	/**
	 * Called to build the name of {@code action GutsAction} field of the 
	 * {@code parent} class instance, passed to 
	 * {@link ActionRegistrationManager#registerActions(Object)}.
	 * <p/>
	 * 
	 * 
	 * @param parent the object that holds {@code action} in {@code field} field
	 * @param action the action for which to build a name
	 * @param field the name of the field that holds {@code action} in its 
	 * declaring class
	 * @return the name to set for {@code action}
	 */
	public String actionName(Object parent, GutsAction action, String field);
}
