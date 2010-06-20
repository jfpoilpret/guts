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

package net.guts.gui.dialog.support;

/**
 * Interface to be implemented by tab panels (used in {@link AbstractTabbedPanel})
 * if they want to be notified of a click on "OK" button.
 *
 * @author Jean-Francois Poilpret
 */
public interface TabPanelAcceptor
{
	/**
	 * Automatically called when the user clicks "OK" for the {@link AbstractTabbedPanel}
	 * in which {@code this} panel is embedded.
	 */
	public void accept();
}
