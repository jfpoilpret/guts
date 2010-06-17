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

import net.guts.gui.application.WindowController.BoundsPolicy;
import net.guts.gui.application.WindowController.StatePolicy;

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
	 * The passed {@code panel} must have a unique name (set by 
	 * {@link javax.swing.JComponent#setName(String)}.
	 * <p/>
	 * Optionally,  {@code panel} may implement one or more of:
	 * <ul>
	 * <li>{@link Closable}</li>
	 * <li>{@link ParentDialogAware}</li>
	 * <li>{@link Resettable}</li>
	 * </ul>
	 * A better -and easier- solution is to have {@code panel} class derive
	 * from {@link net.guts.gui.dialog.support.AbstractPanel}.
	 * 
	 * @param panel panel to be contained in the shown dialog
	 * @param bounds the policy to use for determining the size and location of
	 * the new dialog
	 * @param state the policy to use for determining how state (bounds) should
	 * be restored from previously saved state
	 * @return {@code true} if user has validated the dialog (i.e. clicked
	 * the "OK" button), {@code false} if the user has cancelled the dialog
	 * (i.e. clicked the "Cancel" button or the close box on the title bar)
	 */
	public boolean showDialog(JComponent panel, BoundsPolicy bounds, StatePolicy state);

	/**
	 * Shows a modal dialog. The contained panel is directly instantiated by the
	 * method through Guice (automatically injected), based on the passed Class.
	 * <p/>
	 * The rules and comments for instances of {@code clazz} are the same as
	 * for {@link #showDialog(JComponent, BoundsPolicy, StatePolicy)}.
	 * 
	 * @param clazz class of the panel to be contained in the shown dialog
	 * @param bounds the policy to use for determining the size and location of
	 * the new dialog
	 * @param state the policy to use for determining how state (bounds) should
	 * be restored from previously saved state
	 * @return {@code true} if user has validated the dialog (i.e. clicked
	 * the "OK" button), {@code false} if the user has cancelled the dialog
	 * (i.e. clicked the "Cancel" button or the close box on the title bar)
	 */
	public <T extends JComponent> boolean showDialog(
		Class<T> clazz, BoundsPolicy bounds, StatePolicy state);

	/**
	 * Shows a modal dialog. The contained panel is directly instantiated by the
	 * method through Guice (automatically injected), based on the passed Class.
	 * <p/>
	 * The Guice rules for instantiation apply: if the given class is annotated
	 * (or defined through a Module) as Singleton, then it will be reused once
	 * created, even on several consecutive calls.
	 * <p/>
	 * After creation of the contained panel through Guice, the instance is
	 * passed to the supplied {@code initializer} for further processing
	 * before the dialog is shown. This is useful for instance to pass data
	 * that is displayed by the panel but can change for every display of the
	 * dialog. The call to {@link ComponentInitializer#init(JComponent)} is
	 * always performed, even if the contained panel was already created before.
	 * <p/>
	 * The rules and comments for instances of {@code clazz} are the same as
	 * for {@link #showDialog(JComponent, BoundsPolicy, StatePolicy)}.
	 * 
	 * @param clazz class of the panel to be contained in the shown dialog
	 * @param bounds the policy to use for determining the size and location of
	 * the new dialog
	 * @param state the policy to use for determining how state (bounds) should
	 * be restored from previously saved state
	 * @param initializer an initializer object
	 * @return {@code true} if user has validated the dialog (i.e. clicked
	 * the "OK" button), {@code false} if the user has cancelled the dialog
	 * (i.e. clicked the "Cancel" button or the close box on the title bar)
	 */
	public <T extends JComponent> boolean showDialog(Class<T> clazz, 
		BoundsPolicy bounds, StatePolicy state, ComponentInitializer<T> initializer);
}
