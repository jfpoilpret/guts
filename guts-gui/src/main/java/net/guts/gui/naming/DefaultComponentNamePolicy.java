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

import com.google.inject.Singleton;

/**
 * Default implementation of {@link ComponentNamePolicy}.
 *
 * @author Jean-Francois Poilpret
 */
@Singleton
public class DefaultComponentNamePolicy implements ComponentNamePolicy
{
	/**
	 * Builds the name for {@code parent} as its class simple name.
	 */
	@Override public String parentName(Component parent)
	{
		 return parent.getClass().getSimpleName();
	}

	/**
	 * Builds the name for {@code child} as follows:
	 * <br/>
	 * {@code parentName[sep]holder1[sep]holder2...[sep]holderN[sep]field}
	 * <br/>
	 * where:
	 * <ul>
	 * <li>{@code [sep]} is a separator as returned by {@link #separator()}</li>
	 * <li>{@code holderX} is a holder string from {@code holders}</li>
	 * </ul>
	 * Note that the part "{@code holder1[sep]holder2...[sep]holderN[sep]}" is
	 * assembled by {@link #concatenateHolders} and can be overridden in a subclass.
	 */
	@Override public String childName(
		Component parent, List<String> holders, Component child, String field)
	{
		StringBuilder builder = new StringBuilder(parent.getName());
		builder.append(separator());
		if (!holders.isEmpty())
		{
			concatenateHolders(holders, builder);
		}
		builder.append(field);
		return builder.toString();
	}

	/**
	 * Called by {@link #childName}, this method assembled the list of {@code holders}
	 * into a name, that will be then inserted by {@link #childName} into the complete
	 * name of the component.
	 * <p/>
	 * By default, this method just concatenates all {@code holders} values with a 
	 * separator (returned by {@link #separator()}) in between each.
	 * <p/>
	 * This can be overridden in a subclass in order to, for example, use only the 
	 * last {@code holder} name, or if no {@code holder} at all.
	 * <p/>
	 * This method should add a {@link #separator()} at the end of {@code builder}.
	 * 
	 * @param holders list of holders field names (down the hierarchy from parent 
	 * to child)
	 * @param builder the {@code StringBuilder} to use to build the compound name
	 * out of {@code holders}
	 */
	protected void concatenateHolders(List<String> holders, StringBuilder builder)
	{
		for (String holder: holders)
		{
			builder.append(holder);
			builder.append(separator());
		}
	}

	/**
	 * Defines the string to use to separate field names when assembling the complete
	 * name of a component.
	 * 
	 * @return a separator string
	 */
	protected String separator()
	{
		return "-";
	}
}
