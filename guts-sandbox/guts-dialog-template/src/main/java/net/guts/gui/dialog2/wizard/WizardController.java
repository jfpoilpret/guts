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

package net.guts.gui.dialog2.wizard;

import javax.swing.Action;
import javax.swing.JComponent;

/**
 * Main controller to be used when developing a 
 * {@linkplain AbstractWizardPanel wizard dialog}. It is available from:
 * <ul>
 * <li>the {@linkplain AbstractWizardPanel main wizard panel} through 
 * {@link AbstractWizardPanel#getController()}</li>
 * <li>any {@linkplain WizardStepPanel wizard pane} through 
 * {@link WizardStepPanel#enter(WizardController)}</li>
 * </ul>
 * This is used to:
 * <ul>
 * <li>set up the list of wizard panes that will be used during use of the wizard 
 * dialog</li>
 * <li>dynamically define the sequence of wizard panes until the end of the 
 * wizard dialog</li>
 * <li>change the enabled state of "next" and "finish" buttons</li>
 * <li>pass context information across wizard panes</li>
 * </ul>
 * All methods can be used at any time during the lifecycle of the wizard dialog,
 * with the possibility to dynamically define wizard paths, based on user 
 * selections.
 *
 * @author Jean-Francois Poilpret
 */
public interface WizardController
{
	/**
	 * Add a wizard {@code pane} to the current wizard dialog. Such panes must 
	 * be added early if you want to have correct calculation of wizard dialog 
	 * dimensions; however, you can call it later, in a more dynamic way, if you
	 * need.
	 * <p/>
	 * This method must be called for any wizard pane before it is used in a 
	 * wizard sequence.
	 * <p/>
	 * If {@code appendToSequence} is {@code true}, then a default sequence is
	 * built from the added {@code pane} in the adding order.
	 * <p/>
	 * If {@code pane} implements {@link WizardStepPanel}, then it will be notified
	 * when it becomes the current wizard pane.
	 */
	public void addWizardPane(JComponent pane, boolean appendToSequence);

	public void addWizardPane(Class<? extends JComponent> pane, boolean appendToSequence);

	/**
	 * Set the next steps (wizard pane names) in the current wizard panel
	 * sequence. You would use this method when user's options in the current
	 * wizard pane dynamically define the path for next wizard panes.
	 * <p/>
	 * If your wizard dialog defines a static and linear path of steps, then
	 * you won't need this method.
	 * <p/>
	 * You must make sure all {@code steps} have been first added by calling
	 * {@link #addWizardPane(JComponent, boolean)}.
	 * 
	 * @throws IllegalArgumentException if any step in {@code steps} doesn't
	 * match to the name of a wizard pane previously added with 
	 * {@link #addWizardPane(JComponent, boolean)}
	 */
	public void setNextStepsSequence(String... steps);

	public Action getNextAction();
	public Action getPreviousAction();
}
