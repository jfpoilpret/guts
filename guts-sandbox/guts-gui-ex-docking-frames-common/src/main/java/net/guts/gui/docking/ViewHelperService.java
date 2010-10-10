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

package net.guts.gui.docking;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.event.CFocusListener;
import bibliothek.gui.dock.common.intern.AbstractCDockable;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.MutableCControlRegister;

@Singleton
public class ViewHelperService
{
	@Inject ViewHelperService(CControl controller)
	{
		_controller = controller;
		_controller.addFocusListener(new CFocusListener()
		{
			@Override public void focusLost(CDockable dockable)
			{
				_focused = null;
			}
			
			@Override public void focusGained(CDockable dockable)
			{
				_focused = dockable;
			}
		});
	}

	public String getIdFromView(CDockable view)
	{
		if (view != null)
		{
			MutableCControlRegister register = view.getControl().getRegister();
			String id = view.getControl().access(view).getUniqueId();
			if (register.isMultiId(id))
			{
				return register.multiToNormalId(id);
			}
		}
		return null;
	}
	
	public boolean selectView(String id)
	{
		if (id != null)
		{
			MultipleCDockable view = _controller.getMultipleDockable(id);
			if (view != null)
			{
				CDockable focused = _focused;
				if (!view.isVisible())
				{
					view.setVisible(true);
				}
				((AbstractCDockable) view).toFront();
				if (focused != null)
				{
					((AbstractCDockable) focused).toFront();
				}
				return true;
			}
		}
		return false;
	}
	
	final private CControl _controller;
	private CDockable _focused = null;
}
