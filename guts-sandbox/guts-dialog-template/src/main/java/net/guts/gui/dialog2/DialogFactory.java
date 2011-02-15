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

package net.guts.gui.dialog2;

import javax.swing.JComponent;
import javax.swing.JDialog;

import net.guts.gui.window.RootPaneConfig;

import com.google.inject.ImplementedBy;

/**
 * Interface defining the factory to create and show modal dialogs in the
 * application.
 * <p/>
 * This service is directly injectable through Guice facilities.
 * 
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(DialogFactoryImpl.class)
public interface DialogFactory
{
	/**
	 * Shows a modal dialog.
	 * <p/>
	 * The passed {@code view} must have a unique name (set by 
	 * {@link javax.swing.JComponent#setName(String)}.
	 * <p/>
	 * The passed {@code config} must be obtained by 
	 * {@link net.guts.gui.window.JDialogConfig} and can possibly be 
	 * {@link net.guts.gui.window.JDialogConfig#merge merged} with other configuration
	 * parameters provided by more specialized configuration builders, such as:
	 * <ul>
	 * <li>{@link net.guts.gui.dialog2.template.okcancel.OkCancel}</li>
	 * <li>{@link net.guts.gui.dialog2.template.wizard.Wizard}</li>
	 * </ul>
	 * This additional configuration sets up decoration of {@code view} before adding
	 * it to a new {@link JDialog}.
	 * 
	 * @param view panel to be contained in the shown dialog
	 * @param config the configuration for displaying {@code view} in a new {@link JDialog};
	 * this must be created with {@link net.guts.gui.window.JDialogConfig} builder class.
	 */
	public void showDialog(JComponent view, RootPaneConfig<JDialog> config);

	/**
	 * Shows a modal dialog. The contained panel is directly instantiated by the
	 * method through Guice (automatically injected), based on the passed Class.
	 * <p/>
	 * The rules and comments for instances of {@code clazz} are the same as for
	 * {@link #showDialog(JComponent, RootPaneConfig)}.
	 * 
	 * @param view class of the panel to be contained in the shown dialog
	 * @param config the configuration for displaying {@code view} in a new {@link JDialog};
	 * this must be created with {@link net.guts.gui.window.JDialogConfig} builder class.
	 */
	public void showDialog(Class<? extends JComponent> view, RootPaneConfig<JDialog> config);
}
