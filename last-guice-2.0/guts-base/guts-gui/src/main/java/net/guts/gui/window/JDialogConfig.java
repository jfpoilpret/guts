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

package net.guts.gui.window;

import javax.swing.JDialog;

/**
 * Builder class for {@code RootPaneConfig<JDialog>}. You must use this builder
 * when you want to show an {@link javax.swing.JDialog} with {@link WindowController#show}.
 * The following snippet shows typical usage:
 * <pre>
 * windowController.show(myDialog, 
 *     JDialogConfig.create().bounds(BoundsPolicy.PACK_ONLY).config());
 * </pre>
 * 
 * @author Jean-Francois Poilpret
 */
final public class JDialogConfig extends AbstractConfig<JDialog, JDialogConfig>
{
	private JDialogConfig()
	{
		set(BoundsPolicy.class, BoundsPolicy.PACK_AND_CENTER);
		set(StatePolicy.class, StatePolicy.RESTORE_IF_EXISTS);
	}
	
	/**
	 * Create a new builder of {@code RootPaneConfig<JDialog>}. By default, the 
	 * resulting {@code RootPaneConfig<JDialog>} will use 
	 * {@link StatePolicy#RESTORE_IF_EXISTS} and {@link BoundsPolicy#PACK_AND_CENTER}, 
	 * but this can be changed through {@link #state} and {@link #bounds} methods.
	 * 
	 * @return a new {@code JDialogConfig} that can be used to build a 
	 * {@code RootPaneConfig<JDialog>}
	 */
	static public JDialogConfig create()
	{
		return new JDialogConfig();
	}
	
	/**
	 * Change the bounds policy to be used when creating the new 
	 * {@code RootPaneConfig<JDialog>}. This policy may be used by one of 
	 * {@link WindowController#show}'s {@link WindowProcessor}s, in order to know 
	 * how to initialize the size and location of the {@link javax.swing.JDialog}
	 * to display.
	 * 
	 * @param bounds the bounds policy to set on {@code RootPaneConfig<JDialog>}
	 * @return {@code this} builder so that calls can be chained until {@link #config()}
	 */
	public JDialogConfig bounds(BoundsPolicy bounds)
	{
		return set(BoundsPolicy.class, bounds);
	}
	
	/**
	 * Change the state to be used when creating the new {@code RootPaneConfig<JDialog>}.
	 * This state may be used by one of {@link WindowController#show}'s 
	 * {@link WindowProcessor}s, in order to know whether Session state must be 
	 * restored (by {@link net.guts.gui.session.SessionManager}). 
	 * 
	 * @param state the state policy to set on {@code RootPaneConfig<JDialog>}
	 * @return {@code this} builder so that calls can be chained until {@link #config()}
	 */
	public JDialogConfig state(StatePolicy state)
	{
		return set(StatePolicy.class, state);
	}
}
