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
import java.lang.reflect.Field;
import java.util.LinkedList;

import net.guts.common.ref.ReflectHelper;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class ComponentNamingServiceImpl implements ComponentNamingService
{
	//TODO add more flexibility (through injection of configuration such as:
	// - Field name transformer
	// - Separator to use
	@Inject ComponentNamingServiceImpl(ComponentNamePolicy namingPolicy)
	{
		_namingPolicy = namingPolicy; 
	}
	
	@Override public void setComponentName(final Component parent)
	{
		// First set parent name if necessary
		if (parent.getName() == null)
		{
			parent.setName(_namingPolicy.parentName(parent));
		}
		// Then set component names in all fields in parent class
		// This is done recursively through fields that implement ComponentHolder
		// marker interface
		setChildrenNames(parent, parent, new LinkedList<String>());
	}
	
	private void setChildrenNames(
		final Component root, final Object parent, final LinkedList<String> holders)
	{
		// First set name of every Component field in parent class
		ReflectHelper.processFieldsValues(parent, Component.class, 
			new ReflectHelper.FieldValueProcessor<Component>()
		{
			@Override public boolean process(Field field, Component child)
			{
				if (child.getName() == null)
				{
					child.setName(
						_namingPolicy.childName(root, holders, child, field.getName()));
				}
				return true;
			}
		});
		// Then recurse through all ComponentHolder instances
		ReflectHelper.processFieldsValues(parent, ComponentHolder.class, 
			new ReflectHelper.FieldValueProcessor<ComponentHolder>()
		{
			@Override public boolean process(Field field, ComponentHolder child)
			{
				holders.add(field.getName());
				setChildrenNames(root, child, holders);
				holders.removeLast();
				return true;
			}
		});
	}
	
	final private ComponentNamePolicy _namingPolicy;
}
