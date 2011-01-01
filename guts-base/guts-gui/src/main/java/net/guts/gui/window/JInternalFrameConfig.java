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

import javax.swing.JInternalFrame;


/**
 * Builder class for {@code RootPaneConfig<JInternalFrame>}. You must use this builder
 * when you want to show an {@link javax.swing.JInternalFrame} with 
 * {@link WindowController#show}.
 * The following snippet shows typical usage:
 * <pre>
 * windowController.show(myInternalFrame, 
 *     JInternalFrameConfig.create().bounds(BoundsPolicy.PACK_ONLY).config());
 * </pre>
 * 
 * @author Jean-Francois Poilpret
 */
final public class JInternalFrameConfig 
extends AbstractConfig<JInternalFrame, JInternalFrameConfig>
{
	//TODO add properties:
	// for minimize, maximize...
	private JInternalFrameConfig()
	{
		set(BoundsPolicy.class, BoundsPolicy.PACK_AND_CENTER);
		set(StatePolicy.class, StatePolicy.RESTORE_IF_EXISTS);
	}
	
	/**
	 * Create a new builder of {@code RootPaneConfig<JInternalFrame>}. By default, the 
	 * resulting {@code RootPaneConfig<JInternalFrame>} will use 
	 * {@link StatePolicy#RESTORE_IF_EXISTS} and {@link BoundsPolicy#PACK_AND_CENTER}, 
	 * but this can be changed through {@link #state} and {@link #bounds} methods.
	 * 
	 * @return a new {@code JInternalFrameConfig} that can be used to build a 
	 * {@code RootPaneConfig<JInternalFrame>}
	 */
	static public JInternalFrameConfig create()
	{
		return new JInternalFrameConfig();
	}
	
	/**
	 * Change the bounds policy to be used when creating the new 
	 * {@code RootPaneConfig<JInternalFrame>}. This policy may be used by one of 
	 * {@link WindowController#show}'s {@link WindowProcessor}s, in order to know 
	 * how to initialize the size and location of the {@link javax.swing.JInternalFrame}
	 * to display.
	 * 
	 * @param bounds the bounds policy to set on {@code RootPaneConfig<JInternalFrame>}
	 * @return {@code this} builder so that calls can be chained until {@link #config()}
	 */
	public JInternalFrameConfig bounds(BoundsPolicy bounds)
	{
		return set(BoundsPolicy.class, bounds);
	}
	
	/**
	 * Change the state to be used when creating the new 
	 * {@code RootPaneConfig<JInternalFrame>}.
	 * This state may be used by one of {@link WindowController#show}'s 
	 * {@link WindowProcessor}s, in order to know whether Session state must be 
	 * restored (by {@link net.guts.gui.session.SessionManager}). 
	 * 
	 * @param state the state policy to set on {@code RootPaneConfig<JInternalFrame>}
	 * @return {@code this} builder so that calls can be chained until {@link #config()}
	 */
	public JInternalFrameConfig state(StatePolicy state)
	{
		return set(StatePolicy.class, state);
	}
}
