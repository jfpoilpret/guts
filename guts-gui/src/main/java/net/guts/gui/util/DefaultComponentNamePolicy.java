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

package net.guts.gui.util;

import java.awt.Component;
import java.util.List;

import com.google.inject.Singleton;

@Singleton
public class DefaultComponentNamePolicy implements ComponentNamePolicy
{
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

	@Override public String parentName(Component parent)
	{
		 return parent.getClass().getSimpleName();
	}

	protected void concatenateHolders(List<String> holders, StringBuilder builder)
	{
		for (String holder: holders)
		{
			builder.append(holder);
			builder.append(separator());
		}
	}
	
	protected String separator()
	{
		return "-";
	}
}
