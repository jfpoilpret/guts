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

package net.guts.gui.template.okcancel;

import java.awt.Container;

import javax.swing.JButton;

/**
 * Implementations of this interface are responsible to add a special area to
 * a dialog panel, dedicated to dialog buttons (such as "OK", "Cancel", 
 * "Apply") This area must appear at the bottom of the panel.
 * <p/>
 * There is one implementation per supported {@link java.awt.LayoutManager} so 
 * that buttons can be added directly to the panel without having to create an
 * embedded panel for holding the buttons.
 * <p/>
 * You can define your own {@code OkCancelLayoutAdder} implementation and bind it
 * with {@link OkCancelLayouts#bindLayout}.
 * 
 * @author jfpoilpret
 */
public interface OkCancelLayoutAdder
{
	public Container layout(Container view, JButton ok, JButton cancel, JButton apply);
}
