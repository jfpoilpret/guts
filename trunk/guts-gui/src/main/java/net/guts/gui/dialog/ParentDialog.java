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
 * Interface that is passed to {@link ParentDialogAware} panels before their 
 * containing dialog is shown. Panels must use this interface to close their 
 * embedding dialog upon user's action (e.g. click on "OK" or "Cancel" button).
 * 
 * @see ParentDialogAware#setParent(ParentDialog)
 * @author Jean-Francois Poilpret
 */
public interface ParentDialog
{
	/**
	 * Closes the containing dialog of a panel.
	 * 
	 * @param cancelled {@code true} if the dialog must be closed because
	 * the user has cancelled it (typically by clicking the "Cancel" button).
	 */
	public void close(boolean cancelled);
}
