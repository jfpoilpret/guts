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

import java.awt.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.guts.common.bean.UntypedProperty;

class JTabbedPaneInjector extends AbstractComponentInjector<JTabbedPane>
{
	@Override public void inject(JTabbedPane tabs, ResourceMap resources)
	{
		String prefix = prefix(tabs);
		if (prefix == null)
		{
			return;
		}
		Class<? extends Component> componentType = tabs.getClass();
		// For each injectable resource
		for (ResourceMap.Key key: resources.keys(prefix))
		{
			String name = key.key();
			// Check if key is a special JTabbedPane property
			if (!handleTabProperty(tabs, key, resources))
			{
				// Check that this property exists
				UntypedProperty property = writableProperty(name, componentType);
				if (property != null)
				{
					Class<?> type = property.type();
					// Get the value in the correct type
					Object value = resources.getValue(key, type);
					// Set the property with the resource value
					property.set(tabs, value);
				}
			}
		}
	}
	
	private boolean handleTabProperty(
		JTabbedPane tabs, ResourceMap.Key key, ResourceMap resources)
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
				//TODO log something
				return true;
			}
			TabProperty property = TabProperty.fromName(matcher.group(2));
			if (property == null)
			{
				//TODO log something
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
			//TODO log something
			return true;
		}
	}
	
	static private enum TabProperty
	{
		TITLE("title", String.class)
		{
			@Override public void setValue(JTabbedPane tabs, int tab, Object value)
			{
				tabs.setTitleAt(tab, (String) value);
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
