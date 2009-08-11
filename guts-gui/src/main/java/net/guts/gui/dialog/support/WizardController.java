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

import javax.swing.JComponent;

// Can be passed to any Wizard subpane if it implements WizardStepPanel
// or better system: injectability? => provider, maybe more complex
public interface WizardController
{
	// All potential wizard panes must be added early if you want to have
	// correct calculation of dialog dimensions.
	// This method must be called for any wizard pane before it is used in
	// a wizard sequence.
	// If appendToSequence is true, then a default sequence is
	// built from the added pane in the adding order.
	public void addWizardPane(JComponent pane, boolean appendToSequence);

	// throws IllegalArgExc
	public void setNextStepsSequence(String... steps);

	public void setNextEnabled(boolean enabled);
	public void setAcceptEnabled(boolean enabled);
	
	public <T> void setContext(T context);
	public <T> T getContext(Class<T> clazz);
}
