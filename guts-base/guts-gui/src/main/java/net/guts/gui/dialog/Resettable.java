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

package net.guts.gui.dialog;

/**
 * Interface that enables a panel contained in a dialog created by 
 * {@link DialogFactory} to be notified when the panel is about to be reused in 
 * a new dialog, allowing it to reset all fields to their default values before 
 * the dialog is displayed; this is useful when using the same panel for both 
 * creation and modification.
 * <p/>
 * Your panel should implement this interface if you need this level of control.
 * 
 * @author Jean-Francois Poilpret
 */
public interface Resettable
{
	/**
	 * Called every time before the panel is about to be displayed in a dialog.
	 * Typically, an implementation would reset all panel fields to their
	 * default values.
	 */
	public void reset();
}
