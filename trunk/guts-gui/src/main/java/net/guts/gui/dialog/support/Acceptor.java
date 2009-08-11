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

import org.jdesktop.application.Task;

import net.guts.gui.dialog.ParentDialog;

/**
 * Interface that enables a panel contained in a dialog to be notified when the
 * user accepts the dialog (ie clicks the "OK" button).
 * 
 * @author Jean-Francois Poilpret
 */
public interface Acceptor
{
	/**
	 * Called when the user clicks "OK" on the embedding dialog.
	 * <p/>
	 * The implementation must call {@link ParentDialog#close(boolean)} if it 
	 * wants to close the dialog after performing the action required by the 
	 * user.
	 * 
	 * @param parent the parent dialog on which to call 
	 * {@link ParentDialog#close(boolean)} if the dialog should be called after
	 * the action is performed
	 * @return either a {@code Task} if action performance has to be asynchronous,
	 * or {@code null} if it is completely performed inside the method.
	 */
	public Task<?, ?> accept(ParentDialog parent);
}
