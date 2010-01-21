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

import javax.swing.JComponent;

/**
 * Interface allowing further initialization of dialog panels created by
 * {@link DialogFactory#showDialog(Class, net.guts.gui.application.WindowController.BoundsPolicy, boolean, ComponentInitializer)}.
 * <p/>
 * This is useful when the panel, created by Guice facilities, needs extra
 * arguments that cannot be injected by Guice, such as a Domain Model Object 
 * instance which detail data should be displayed in the dialog.
 * <p/>
 * Typically, implementing classes will be inlined in the code that calls
 * {@link DialogFactory#showDialog(Class, net.guts.gui.application.WindowController.BoundsPolicy, boolean, ComponentInitializer)}.
 * 
 * @author Jean-Francois Poilpret
 * @param <T> the panel class which instances can be initialized by this
 * {@code ComponentInitializer}
 */
public interface ComponentInitializer<T extends JComponent>
{
	/**
	 * Called immediately before the dialog embedding {@code panel} is
	 * shown, giving the last opportunity to set finish initialization of
	 * {@code panel} (eg setting its fields with values of a Domain Object).
	 * 
	 * @param panel dialog panel to be initialized before its dialog is made 
	 * visible
	 */
	public void init(T panel);
}
