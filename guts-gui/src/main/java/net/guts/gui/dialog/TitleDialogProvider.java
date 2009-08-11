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
 * {@link DialogFactory} to define the title to be displayed in the embedding 
 * dialog.
 * <p/>
 * Your panel should implement this interface if you need this level of control,
 * e.g. when the title is dynamically calculated (else for static titles,
 * resources in properties files are enough).
 * <p/>
 * Note that {@link net.guts.gui.dialog.support.AbstractPanel} implements 
 * this interface.
 * 
 * @author Jean-Francois Poilpret
 */
public interface TitleDialogProvider
{
	/**
	 * Called every time before the panel is about to be displayed in a new
	 * dialog in order to know what title should be used for that dialog.
	 * 
	 * @return the title to set for the embedding dialog
	 */
	public String getDialogTitle();
}
