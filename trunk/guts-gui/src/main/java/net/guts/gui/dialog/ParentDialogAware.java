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
 * {@link DialogFactory} to be called back by its embedding dialog in order to 
 * receive a {@link ParentDialog} reference that will allow it to close the 
 * embedding dialog later on.
 * <p/>
 * Your panel should normally implement this interface except if it give no way
 * to the user to close it (no "OK" or "Cancel" button).
 * <p/>
 * Note that {@link net.guts.gui.dialog.support.AbstractPanel} implements this interface.
 * 
 * @author Jean-Francois Poilpret
 */
public interface ParentDialogAware
{
	/**
	 * Called every time before the panel is about to be displayed in a new
	 * dialog. Typically, an implementation would just store the passed reference
	 * in one field for later use.
	 * 
	 * @param parent the parent dialog of the panel
	 */
	public void setParentDialog(ParentDialog parent);
}
