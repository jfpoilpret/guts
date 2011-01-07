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

package net.guts.gui.dialog.layout;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import net.java.dev.designgridlayout.DesignGridLayoutManager;

/**
 * Factory of {@link ButtonsPanelAdder}s. Holds different implementations
 * depending on supported {@link LayoutManager}s. Currently supported 
 * {@link LayoutManager}s are:
 * <ul>
 * <li>{@link java.awt.BorderLayout}</li>
 * <li>{@link java.awt.GridBagLayout}</li>
 * <li>{@link net.java.dev.designgridlayout.DesignGridLayout}</li>
 * </ul>
 * But it is possible to add support for others by calling 
 * {@link #registerAdder(Class, ButtonsPanelAdder)} with your own implementation 
 * instance.
 * 
 * @author Jean-Francois Poilpret
 */
final public class ButtonsPanelAdderFactory
{
	private ButtonsPanelAdderFactory()
	{
	}

	static
	{
		_adders = new HashMap<Class<? extends LayoutManager>, ButtonsPanelAdder>();
		registerAdder(BorderLayout.class, new BorderButtonsPanelAdder());
		registerAdder(GridBagLayout.class, new GridBagButtonsPanelAdder());
		registerAdder(DesignGridLayoutManager.class, new DesignGridButtonsPanelAdder());
	}
	
	/**
	 * Registers a new {@link ButtonsPanelAdder} for a given {@link LayoutManager}.
	 * You don't need to use this method unless your {@link LayoutManager} is not
	 * supported yet.
	 * 
	 * @param adder the new adder to add to the factory
	 */
	static public void	registerAdder(
		Class<? extends LayoutManager> layout, ButtonsPanelAdder adder)
	{
		_adders.put(layout, adder);
	}

	/**
	 * Finds the right {@link ButtonsPanelAdder} implementation for the given
	 * {@code container}.
	 * 
	 * @param container the panel for which you need a {@link ButtonsPanelAdder}
	 * @return the right ButtonsPanelAdder for {@code container}
	 */
	static public ButtonsPanelAdder	getAdder(JComponent container)
	{
		return _adders.get(container.getLayout().getClass());
	}
	
	static final private Map<Class<? extends LayoutManager>, ButtonsPanelAdder> _adders;
}
