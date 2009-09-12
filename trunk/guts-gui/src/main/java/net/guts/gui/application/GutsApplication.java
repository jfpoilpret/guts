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

package net.guts.gui.application;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import net.guts.gui.action.ActionManager;
import net.guts.gui.exit.ExitController;

import com.google.inject.Inject;

//TODO remove this class ASAP (when SAF is removed)
final public class GutsApplication extends Application
//final public class GutsApplication extends SingleFrameApplication
{
	@Override protected void startup()
	{
	}
	
	@Inject public void init(ExitController controller, ActionManager manager)
	{
		_controller = controller;
		manager.addActionSource(this);
	}
	
	@Action public void quit()
	{
		_controller.shutdown();
	}

	//TODO add other actions: cut, copy,paste
	private ExitController _controller;
}
