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

package net.guts.gui.dialog2.wizard;

import javax.swing.JComponent;

//TODO maybe this factory is unnecessary and could be replaced by a
// WindowController.createMainView() method, but that would require systematic
// check that this method isn't called twice and has already been called once...
// Then the best option would be to create a Provider<WindowController> as a factory
public interface WizardControllerFactory
{
	public WizardController create(JComponent wizardMainView);
}
