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

package net.guts.gui.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

/**
 * Simple utilities for {@link java.awt.Cursor}s.
 * 
 * @author Jean-Francois Poilpret
 */
public final class CursorHelper
{
	/**
	 * Creates a {@link CursorInfo} from an icon and relative positions for the
	 * cursor hotspot.
	 * <p/>
	 * The cursor size may differ from the original {@code icon} size according
	 * to the current Operating System.
	 * 
	 * @param icon the icon to use as the cursor image
	 * @param x the relative x position expressed as a percentage of the
	 * actual cursor width (0.0 - 1.0, 0.0 is leftmost, 1.0 is rightmost)
	 * @param y the relative y position expressed as a percentage of the
	 * actual cursor height (0.0 - 1.0, 0.0 is topmost, 1.0 is bottommost)
	 * @return a new {@link CursorInfo}
	 */
	static public CursorInfo buildCursor(ImageIcon icon, double x, double y)
	{
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension dim = kit.getBestCursorSize(icon.getIconWidth(), icon.getIconHeight());
		Point hotspot = new Point((int) (x * dim.width), (int) (y * dim.height));
		return new CursorInfo(icon, hotspot, dim);
	}
	
	private CursorHelper()
	{
	}
}
