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
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

class WpInternalFrameCloseChecking 
extends AbstractWindowProcessor<JInternalFrame, JInternalFrame>
{
	WpInternalFrameCloseChecking()
	{
		super(JInternalFrame.class);
	}
	
	@Override protected void processRoot(
		JInternalFrame root, RootPaneConfig<JInternalFrame> config)
	{
		CloseChecker checker = config.get(CloseChecker.class);
		root.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		root.addInternalFrameListener(new WindowCloser(root, checker));
	}
	
	static private class WindowCloser extends InternalFrameAdapter
	{
		public WindowCloser(JInternalFrame root, CloseChecker checker)
		{
			_root = root;
			_checker = checker;
		}
		
		@Override public void internalFrameClosing(InternalFrameEvent arg0)
		{
			if (_checker == null || _checker.canClose())
			{
				_root.dispose();
			}
		}

		final private JInternalFrame _root;
		final private CloseChecker _checker;
	}
}
