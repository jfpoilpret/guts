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

import javax.swing.JApplet;

/**
 * Builder class for {@code RootPaneConfig<JApplet>}. You must use this builder
 * when you want to show an {@link javax.swing.JApplet} with {@link WindowController#show}.
 * The following snippet shows typical usage:
 * <pre>
 * windowController.show(myApplet, 
 *     JAppletConfig.create().state(StatePolicy.DONT_RESTORE).config());
 * </pre>
 * 
 * @author Jean-Francois Poilpret
 */
final public class JAppletConfig extends AbstractConfig<JApplet, JAppletConfig>
{
	private JAppletConfig()
	{
		set(StatePolicy.class, StatePolicy.RESTORE_IF_EXISTS);
	}
	
	/**
	 * Create a new builder of {@code RootPaneConfig<JApplet>}. By default, the 
	 * resulting {@code RootPaneConfig<JApplet>} will use 
	 * {@link StatePolicy#RESTORE_IF_EXISTS}, but this can be changed through 
	 * {@link #state} method.
	 * 
	 * @return a new {@code JAppletConfig} that can be used to build a 
	 * {@code RootPaneConfig<JApplet>}
	 */
	static public JAppletConfig create()
	{
		return new JAppletConfig();
	}

	/**
	 * Change the state to be used when creating the new {@code RootPaneConfig<JApplet>}.
	 * This state may be used by one of {@link WindowController#show}'s 
	 * {@link WindowProcessor}s, in order to know whether Session state must be 
	 * restored (by {@link net.guts.gui.session.SessionManager}). 
	 * 
	 * @param state the state policy to set on {@code RootPaneConfig<JApplet>}
	 * @return {@code this} builder so that calls can be chained until {@link #config()}
	 */
	public JAppletConfig state(StatePolicy state)
	{
		return set(StatePolicy.class, state);
	}
}
