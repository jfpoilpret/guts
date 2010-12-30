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

package net.guts.gui.session;

import javax.swing.JApplet;

import net.guts.event.Consumes;
import net.guts.gui.exit.ExitController;
import net.guts.gui.window.AbstractWindowProcessor;
import net.guts.gui.window.RootPaneConfig;
import net.guts.gui.window.StatePolicy;

import com.google.inject.Inject;

class WpAppletSessionStorage extends AbstractWindowProcessor<JApplet, JApplet>
{
	@Inject WpAppletSessionStorage(SessionManager sessions)
	{
		super(JApplet.class);
		_sessions = sessions;
	}

	@Override protected void processRoot(JApplet root, RootPaneConfig<JApplet> config)
	{
		StatePolicy state = config.get(StatePolicy.class);
		if (state == StatePolicy.RESTORE_IF_EXISTS)
		{
			// Restore size from session storage if any
			_sessions.restore(root);
		}
		_applet = root;
	}

	@Consumes(topic = ExitController.SHUTDOWN_EVENT, priority = Integer.MIN_VALUE + 1)
	public void shutdown(Void nothing)
	{
		if (_applet != null)
		{
			_sessions.save(_applet);
		}
	}

	final private SessionManager _sessions;
	private JApplet _applet = null;
}
