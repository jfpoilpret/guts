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

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.google.inject.ImplementedBy;

// Deals with windows display, including managing i18n and session storage
@ImplementedBy(WindowControllerImpl.class)
public interface WindowController
{
	public enum BoundsPolicy
	{
		PACK_AND_CENTER,
		PACK_ONLY,
		CENTER_ONLY,
		KEEP_ORIGINAL_BOUNDS
	};
	
	public void show(JFrame frame, BoundsPolicy policy);
	public void show(JDialog dialog, BoundsPolicy policy);

	/**
	 * Get the current active foreground window in the application.
	 * 
	 * @return the current active foreground window
	 */
	public Window	getActiveWindow();
}
