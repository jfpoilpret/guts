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

package net.guts.gui.dialog.support;

import java.awt.Component;

import javax.swing.JComponent;

import net.guts.gui.dialog.Closable;
import net.guts.gui.dialog.Resettable;

/**
 * Abstract panel to be used in dialogs containing multiple panels managed in a
 * single {@link JComponent}. This is used as a base class for 
 * {@link AbstractTabbedPanel} and {@link AbstractWizardPanel}.
 * <p/>
 * This class manages most repetitive stuff for you: creating OK/Cancel buttons
 * and associated Actions, inject resources into components from properties
 * files...
 * <p/>
 * Embedded panels can be instances of any class as long as it is a subclass 
 * (direct or not) of {@link JComponent}. Similarly, embedded panels may or may 
 * not implement {@link Resettable} or {@link net.guts.gui.dialog.Closable}. 
 * If they do, {@code AbstractMultiPanel} will delegate to them the corresponding 
 * calls.
 * 
 * @author Jean-Francois Poilpret
 * @see AbstractPanel
 */
public abstract class AbstractMultiPanel extends AbstractPanel 
	implements Closable, Resettable 
{
	/**
	 * Gets the list of sub panes.
	 */
	abstract protected Iterable<JComponent> getSubComponents();

	//TODO rework to make it final with an empty overridable hook
	/**
	 * Resets all individual tab panes added to this panel (if they implement
	 * {@link Resettable}). You may override this method if you need further 
	 * control; if you do that, however, don't forget to call 
	 * {@code super.reset()}.
	 */
	@Override public void reset()
    {
		for (Component subpane: getSubComponents())
		{
			if (subpane instanceof Resettable)
			{
				((Resettable) subpane).reset();
			}
		}
    }

	//TODO same as above
	@Override public boolean canClose()
	{
		for (Component subpane: getSubComponents())
		{
			if (	subpane instanceof Closable
				&&	!((Closable) subpane).canClose())
			{
				return false;
			}
		}
		return true;
	}

	private static final long serialVersionUID = 8676919627941201180L;
}
