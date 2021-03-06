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

package net.guts.gui.template.wizard;

/**
 * Listener for step changes in a {@link Wizard}-created container.
 * <p/>
 * {@code WizardListener}s can be added to any {@link WizardController}.
 * 
 * @author jfpoilpret
 */
public interface WizardListener
{	
	/**
	 * Called when the wizard managed by {@code controller} has changed from
	 * {@code oldStep} to {@code newStep}.
	 * 
	 * @param controller
	 * @param oldStep
	 * @param newStep
	 */
	public void stepChanged(WizardController controller, String oldStep, String newStep);
}
