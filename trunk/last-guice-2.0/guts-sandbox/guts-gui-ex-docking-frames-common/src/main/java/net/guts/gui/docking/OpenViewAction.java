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

import net.guts.gui.action.GutsAction;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.SingleCDockable;

import com.google.inject.Inject;

public class OpenViewAction extends GutsAction
{
	public OpenViewAction(String view)
	{
		super("OpenView-" + view);
		_view = view;
	}

	@Inject void init(CControl controller)
	{
		_controller = controller;
	}
	
	@Override protected void perform()
	{
		SingleCDockable view = _controller.getSingleDockable(_view);
		view.setVisible(true);
	}
	
	final private String _view;
	private CControl _controller;
}
