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

import com.google.inject.Singleton;

/**
 * Default implementation of {@link ActionNamePolicy}.
 * 
 * @author Jean-Francois Poilpret
 */
@Singleton
public class DefaultActionNamePolicy implements ActionNamePolicy
{
	/**
	 * Builds the name for {@code action} as follows:
	 * <br/>
	 * {@code parentName[sep]field}
	 * <br/>
	 * where:
	 * <ul>
	 * <li>{@code [sep]} is a separator as returned by {@link #separator()}</li>
	 * <li>{@code parentName} is a simple name of the class of {@code parent}, as returned
	 * by {@link #extractClassName(Class)}</li>
	 * </ul>
	 */
	@Override public String actionName(Object parent, GutsAction action, String field)
	{
		return extractClassName(parent.getClass()) + separator() + field;
	}
	
	/**
	 * Extracts the class name of the passed argument, to be used as the first part of
	 * the action name.
	 * Default implementation {@link Class#getSimpleName() takes the simple name} of 
	 * {@code clazz} and removes any characters that might have been added by CGLIB-based
	 * interceptors (ie anything following "{@literal $$}").
	 * 
	 * @param clazz the class of which to extract the name
	 * @return the extracted name for {@code clazz}
	 */
	protected String extractClassName(Class<?> clazz)
	{
		String name = clazz.getSimpleName();
		int index = name.indexOf("$$");
		if (index > -1)
		{
			name = name.substring(0, index);
		}
		return name;
	}

	/**
	 * Defines the string to use to separate the parent class name from the field name
	 * when assembling the complete name of an action.
	 * 
	 * @return a separator string ({@code "-"} by default)
	 */
	protected String separator()
	{
		return "-";
	}
}
