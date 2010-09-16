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
import javax.swing.JInternalFrame;

import net.guts.gui.application.WindowController.BoundsPolicy;
import net.guts.gui.application.WindowController.StatePolicy;

import com.google.inject.ImplementedBy;

@ImplementedBy(MDIFrameControllerImpl.class)
public interface MDIFrameController
{
	public void initFrame(JFrame frame);
	public void show(JInternalFrame frame, BoundsPolicy bounds, StatePolicy state);
	public JInternalFrame getActiveFrame();
}
