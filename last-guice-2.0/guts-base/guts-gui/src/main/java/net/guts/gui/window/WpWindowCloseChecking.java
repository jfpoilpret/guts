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

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

class WpWindowCloseChecking<V extends RootPaneContainer> 
extends AbstractWindowProcessor<Window, V>
{
	WpWindowCloseChecking()
	{
		super(Window.class);
	}
	
	@Override protected void processRoot(Window root, RootPaneConfig<V> config)
	{
		CloseChecker checker = config.get(CloseChecker.class);
		if (root instanceof JFrame)
		{
			((JFrame) root).setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}
		else if (root instanceof JDialog)
		{
			((JDialog) root).setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		}
		root.addWindowListener(new WindowCloser(root, checker));
	}
	
	static private class WindowCloser extends WindowAdapter
	{
		public WindowCloser(Window root, CloseChecker checker)
		{
			_root = root;
			_checker = checker;
		}
		
		@Override public void windowClosing(WindowEvent e)
		{
			if (_checker == null || _checker.canClose())
			{
				_root.dispose();
			}
		}

		final private Window _root;
		final private CloseChecker _checker;
	}
}
