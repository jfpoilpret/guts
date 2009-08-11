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
 * {@link DialogFactory} to be notified when the user is about to close the 
 * dialog through the close box, allowing it to possibly ask user's confirmation 
 * (in case of modifications).
 * <p/>
 * Your panel should implement this interface if you need this level of control.
 * 
 * @author Jean-Francois Poilpret
 */
public interface Closable
{
	/**
	 * Called when the user is about to close the dialog by clicking its close 
	 * box. An implementation should check that closing the dialog is allowed,
	 * or if user should be warned and confirmation required...
	 * 
	 * @return {@code true} if the dialog can be closed, {@code false}
	 * if the dialog should not be closed
	 */
	public boolean canClose();
}
