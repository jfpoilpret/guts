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

import net.guts.gui.task.Task;

/**
 * Interface that wizard panes (added to an {@link AbstractWizardPanel} through
 * {@link WizardController#addWizardPane(javax.swing.JComponent, boolean)}) can 
 * choose to implement, in order to be notified of when they are displayed in the 
 * wizard dialog and when they are left for another wizard pane.
 *
 * @author Jean-Francois Poilpret
 */
public interface WizardStepPanel
{
	/**
	 * Called by {@link AbstractWizardPanel} just before {@code this} wizard pane
	 * is going to be made visible in the current wizard dialog, this method 
	 * allows the current wizard pane to use {@code controller} to get context
	 * information from the previous wizard panes. 
	 * 
	 * @param controller the wizard controller for the wizard dialog {@code this}
	 * wizard pane belongs to
	 */
	public void enter(WizardController controller);

	/**
	 * Called by {@link AbstractWizardPanel} just before {@code this} wizard pane
	 * is going to be made invisible in the current wizard dialog, because another
	 * wizard pane is to be made visible.
	 * <p/>
	 * This can be used to {@linkplain WizardController#setContext(Object) save new context} 
	 * for the next wizard pane.
	 * 
	 * @param controller the wizard controller for the wizard dialog {@code this}
	 * wizard pane belongs to
	 * @return a {@code Task} that will be executed in background before the next 
	 * wizard pane is made visible; this is advised if the work to be performed is 
	 * too long and might block Swing EDT; otherwise, {@code null} is just fine.
	 */
	public Task<?> leave(WizardController controller);
}
