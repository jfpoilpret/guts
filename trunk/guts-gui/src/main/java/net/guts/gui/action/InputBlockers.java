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

package net.guts.gui.action;

import java.awt.Component;

public final class InputBlockers
{
	private InputBlockers()
	{
	}

	static public InputBlocker createNoBlocker()
	{
		return _noBlocker;
	}
	
	static public InputBlocker createActionBlocker(final GutsAction source)
	{
		return new AbstractInputBlocker()
		{
			@Override protected void setBlocking(boolean block)
			{
				source.action().setEnabled(!block);
			}
		};
	}

	static public InputBlocker createComponentBlocker(final GutsAction source)
	{
		final Component component = (Component) source.event().getSource();
		return new AbstractInputBlocker()
		{
			@Override protected void setBlocking(boolean block)
			{
				component.setEnabled(!block);
			}
		};
	}
	
	//TODO other blockers here: Window, Application...
	
	static final private InputBlocker _noBlocker = new AbstractInputBlocker()
	{
		@Override protected void setBlocking(boolean block)
		{
		}
	};
}
