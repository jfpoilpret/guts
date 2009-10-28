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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

import net.guts.gui.resource.ResourceMap.Key;

class JTabbedPaneInjector extends BeanPropertiesInjector<JTabbedPane>
{
	// CSOFF: ReturnCountCheck
	@Override protected boolean handleSpecialProperty(
		JTabbedPane tabs, Key key, ResourceMap resources)
	{
		String name = key.key();
		Matcher matcher = _tabsTagPattern.matcher(name);
		if (!matcher.matches())
		{
			return false;
		}
		try
		{
			int tab = Integer.parseInt(matcher.group(1));
			if (tab < 0 || tab >= tabs.getTabCount())
			{
				_logger.debug(
					"JTabbedPane {} has only {} tabs, so property {} can't be matched.",
					new Object[]{tabs.getName(), tabs.getTabCount(), name});
				return true;
			}
			TabProperty property = TabProperty.fromName(matcher.group(2));
			if (property == null)
			{
				_logger.debug("Property {} isn't valid for a JTabbedPane.", name);
				return true;
			}
			// Convert the value to the expected type for that tab property
			Object value = resources.getValue(key, property.type());
			// Set the value for the right tab
			property.setValue(tabs, tab, value);
			return true;
		}
		catch (NumberFormatException e)
		{
			_logger.warn("Normally impossible to get this exception with property {}", name);
			return true;
		}
	}
	// CSON: ReturnCountCheck
	
	static private enum TabProperty
	{
		TITLE("title", String.class)
		{
			@Override public void setValue(JTabbedPane tabs, int tab, Object value)
			{
				MnemonicInfo info = MnemonicInfo.extract((String) value);
				tabs.setTitleAt(tab, info.getText());
				tabs.setMnemonicAt(tab, info.getMnemonic());
				tabs.setDisplayedMnemonicIndexAt(tab, info.getMnemonicIndex());
			}
		},
		ICON("icon", Icon.class)
		{
			@Override public void setValue(JTabbedPane tabs, int tab, Object value)
			{
				tabs.setIconAt(tab, (Icon) value);
			}
		},
		DISABLED_ICON("disabledIcon", Icon.class)
		{
			@Override public void setValue(JTabbedPane tabs, int tab, Object value)
			{
				tabs.setDisabledIconAt(tab, (Icon) value);
			}
		},
		TOOLTIP("toolTipText", String.class)
		{
			@Override public void setValue(JTabbedPane tabs, int tab, Object value)
			{
				tabs.setToolTipTextAt(tab, (String) value);
			}
		};
		
		private TabProperty(String name, Class<?> type)
		{
			_name = name;
			_type = type;
		}
		
		static public TabProperty fromName(String name)
		{
			for (TabProperty property: values())
			{
				if (property._name.equals(name))
				{
					return property;
				}
			}
			return null;
		}
		
		public Class<?> type()
		{
			return _type;
		}

		abstract public void setValue(JTabbedPane tabs, int tab, Object value);
		
		final private String _name;
		final private Class<?> _type;
	}
	
	static final private Pattern _tabsTagPattern = Pattern.compile("tab([0-9]+)-([a-zA-Z]+)");
}
