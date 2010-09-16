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

package net.guts.gui.naming;

import java.awt.Component;
import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * This interface defines a policy for naming {@link java.awt.Component}s,
 * used by {@link ComponentNamingService} default implementation.
 * <p/>
 * {@link DefaultComponentNamePolicy} is the default implementation of 
 * {@code ComponentNamePolicy} but can be overridden easily in one of your
 * Guice {@link com.google.inject.Module}s:
 * <pre>
 * bind(ComponentNamePolicy.class).to(MyComponentNamePolicy.class);
 * </pre> 
 *
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(DefaultComponentNamePolicy.class)
public interface ComponentNamePolicy
{
	/**
	 * Called to build the name of the {@code parent Component} passed to
	 * {@link ComponentNamingService#setComponentName(Component)}.  
	 * 
	 * @param parent the root component to build a name for
	 * @return the name to set for {@code parent}
	 */
	public String parentName(Component parent);

	/**
	 * Called to build the name of a {@code child Component} of the
	 * {@code parent Component} passed to 
	 * {@link ComponentNamingService#setComponentName(Component)}.
	 * <p/>
	 * Note that {@code child} comes from fields in {@code parent} or
	 * in {@link ComponentHolder}s fields in {@code parent}.
	 * 
	 * @param parent the root component
	 * @param holders the list of names of the consecutive {@link ComponentHolder}s
	 * fields from {@code parent} to {@code child}, excluded; these are the names 
	 * of the fields implementing {@code ComponentHolder}.
	 * @param child the component for which to build a name
	 * @param field the name of the field that holds {@code child} in its 
	 * declaring class
	 * @return the name to set for {@code child}
	 */
	public String childName(
		Component parent, List<String> holders, Component child, String field);
}
