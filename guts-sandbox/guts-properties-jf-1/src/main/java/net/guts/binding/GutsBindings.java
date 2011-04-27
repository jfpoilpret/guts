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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import net.guts.properties.Bean;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.ValueModel;

public class GutsBindings extends Bindings
{
	// Prevent direct instantiation
	protected GutsBindings()
	{
	}

	public static void bindDoubleClick(final JTable table, final Action action)
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
	
	public static void bindEnter(final JTable table, final Action action)
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
}
