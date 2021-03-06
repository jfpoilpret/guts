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

package net.guts.binding;

import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import net.guts.properties.Bean;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;

public class GutsBindings extends Bindings
{
	// Prevent direct instantiation
	protected GutsBindings()
	{
	}

	public static void connectTitle(final JComponent view, final ValueModel<String> model)
	{
		if (view.getRootPane() != null)
		{
			PropertyConnector.connectAndUpdate(model, view.getRootPane().getParent(), "title");
		}
		view.addHierarchyListener(new HierarchyListener()
		{
			@Override public void hierarchyChanged(HierarchyEvent e)
			{
				if (view.getRootPane() != null)
				{
					PropertyConnector.connectAndUpdate(
						model, view.getRootPane().getParent(), "title");
				}
			}
		});
	}

	public static void connectActionsEnableToSelection(
		final SelectionInList<?> selection, final Action... actions)
	{
		selection.addPropertyChangeListener(
			SelectionInList.PROPERTYNAME_SELECTION_EMPTY, new PropertyChangeListener()
		{
			@Override public void propertyChange(PropertyChangeEvent evt)
			{
				enableIfSelection(selection, actions);
			}
		});
		enableIfSelection(selection, actions);
	}
	
	private static void enableIfSelection(SelectionInList<?> selection, Action... actions)
	{
		for (Action action: actions)
		{
			action.setEnabled(!selection.isSelectionEmpty());
		}
	}

	public static void connectDoubleClick(final JTable table, final Action action)
	{
		table.addMouseListener(new MouseAdapter()
		{
			@Override public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2 && action.isEnabled())
				{
					action.actionPerformed(
						new ActionEvent(table, ActionEvent.ACTION_PERFORMED, ""));
				}
			}
		});
	}
	
	public static void connectEnter(final JTable table, final Action action)
	{
		table.getActionMap().put("ENTER", action);
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ENTER");
	}
	
	// Usage: bind(field, Models.of(JTextField.class).getText(), textValueModel);
	@SuppressWarnings("unchecked") 
	public static <C extends JComponent, T> void bind(
		C component, T getter, ValueModel<T> valueModel)
	{
		Bean<C> helper = Bean.create((Class<C>) component.getClass());
		bind(component, helper.property(getter).name(), valueModel);
	}
	
	@SuppressWarnings("unchecked") 
	public static <C extends JComponent, T> void connect(
		C component, T getter, ValueModel<T> valueModel)
	{
		Bean<C> helper = Bean.create((Class<C>) component.getClass());
		PropertyConnector connector = PropertyConnector.connect(
			component, helper.property(getter).name(), valueModel, "value");
		connector.updateProperty1();
	}
	
	public static void connectText(JLabel label, ValueModel<String> text)
	{
		connect(label, Models.of(JLabel.class).getText(), text);
	}
	
	public static void connectIcon(JLabel label, ValueModel<Icon> icon)
	{
		connect(label, Models.of(JLabel.class).getIcon(), icon);
	}
}
