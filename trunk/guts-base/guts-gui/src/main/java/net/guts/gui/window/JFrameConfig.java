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

import javax.swing.JFrame;

final public class JFrameConfig extends AbstractConfig<JFrame, JFrameConfig>
{
	private JFrameConfig()
	{
		set(BoundsPolicy.class, BoundsPolicy.PACK_AND_CENTER);
		set(StatePolicy.class, StatePolicy.RESTORE_IF_EXISTS);
	}
	
	static public JFrameConfig create()
	{
		return new JFrameConfig();
	}
	
	public JFrameConfig bounds(BoundsPolicy bounds)
	{
		return set(BoundsPolicy.class, bounds);
	}
	
	public JFrameConfig state(StatePolicy state)
	{
		return set(StatePolicy.class, state);
	}
}
