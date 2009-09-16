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

package net.guts.gui.resource;


import javax.swing.JComponent;


//TODO find a better name like SingleResourceInjector? InstanceResourcesInjector?...
//TODO create impl that simply injects through bean properties
//TODO there must be some recursivity here (because components can contain other components)
// Maybe need to pass a map of ComponentInjectors?
/**
 * TODO
 *
 * @author Jean-Francois Poilpret
 */
public interface ComponentInjector<T>
{
	// resources is the list of all resources (strongly typed) for this
	// component
	// Implementations of this method should iterate through resources and
	// inject each individual resource into component
	public void inject(T component, ResourceMap resources);
}

// Default component injector
class BeanPropertiesInjector implements ComponentInjector<JComponent>
{
	@Override public void inject(JComponent component, ResourceMap resources)
	{
		String prefix = component.getName();
		if (prefix == null)
		{
			return;
		}
		// For each injectable resource
		for (String key: resources.keys(prefix))
		{
			//TODO
			// Check that this property exists
			// Check the expected type of that property
			// Get the value in the correct type
			// Set the property with the resource value
		}
	}
}
