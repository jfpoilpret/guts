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

package net.guts.gui.application.support;

import javax.swing.JFrame;

import com.google.inject.Inject;

// TODO need a new Service MDIFrameController or something (something WindowController/DialogFactory)?
abstract public class MDIFrameLifecycle extends SingleFrameLifecycle
{
	@Inject void init(MDIFrameController controller)
	{
		_controller = controller;
	}

	abstract protected void initMDIFrame(JFrame frame);
	
	/* (non-Javadoc)
	 * @see net.guts.gui.application.support.SingleFrameLifecycle#initFrame(javax.swing.JFrame)
	 */
	@Override protected void initFrame(JFrame mainFrame)
	{
		_controller.initFrame(mainFrame);
		initMDIFrame(mainFrame);
	}

	private MDIFrameController _controller = null;
}
