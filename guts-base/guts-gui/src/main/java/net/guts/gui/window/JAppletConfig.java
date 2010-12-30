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

final public class JAppletConfig extends AbstractConfig<JApplet, JAppletConfig>
{
	private JAppletConfig()
	{
		set(StatePolicy.class, StatePolicy.RESTORE_IF_EXISTS);
	}
	
	static public JAppletConfig create()
	{
		return new JAppletConfig();
	}
	
	public JAppletConfig state(StatePolicy state)
	{
		return set(StatePolicy.class, state);
	}
}
