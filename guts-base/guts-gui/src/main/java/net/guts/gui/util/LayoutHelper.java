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

import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;

/**
 * Utility class providing several methods dealing with gaps between components in
 * their container or between a component and its container borders.
 *
 * @author Jean-Francois Poilpret
 */
public final class LayoutHelper
{
	private LayoutHelper()
	{
	}
	
	static public int maxTopGap(Container parent, JComponent... components)
	{
		int gap = 0;
		for (JComponent component: components)
		{
			gap = Math.max(gap, topGap(parent, component));
		}
		return gap;
	}

	static public int topGap(Container parent, JComponent component)
	{
		return gap(parent, component, SwingConstants.NORTH);
	}

	static public int maxLeftGap(Container parent, JComponent... components)
	{
		int gap = 0;
		for (JComponent component: components)
		{
			gap = Math.max(gap, leftGap(parent, component));
		}
		return gap;
	}

	static public int leftGap(Container parent, JComponent component)
	{
		return gap(parent, component, SwingConstants.WEST);
	}

	static public int maxBottomGap(Container parent, JComponent... components)
	{
		int gap = 0;
		for (JComponent component: components)
		{
			gap = Math.max(gap, bottomGap(parent, component));
		}
		return gap;
	}

	static public int bottomGap(Container parent, JComponent component)
	{
		return gap(parent, component, SwingConstants.SOUTH);
	}

	static public int maxRightGap(Container parent, JComponent... components)
	{
		int gap = 0;
		for (JComponent component: components)
		{
			gap = Math.max(gap, rightGap(parent, component));
		}
		return gap;
	}

	static public int rightGap(Container parent, JComponent component)
	{
		return gap(parent, component, SwingConstants.EAST);
	}
	
	static public int unrelatedHorizontalGap(
		Container parent, JComponent left, JComponent right)
	{
		return unrelatedGap(parent, left, right, SwingConstants.EAST);
	}

	static public int unrelatedVerticalGap(
		Container parent, JComponent top, JComponent bottom)
	{
		return unrelatedGap(parent, top, bottom, SwingConstants.SOUTH);
	}

	static public int relatedHorizontalGap(
		Container parent, JComponent left, JComponent right)
	{
		return relatedGap(parent, left, right, SwingConstants.EAST);
	}

	static public int relatedVerticalGap(
		Container parent, JComponent top, JComponent bottom)
	{
		return relatedGap(parent, top, bottom, SwingConstants.SOUTH);
	}

	static private int relatedGap(
		Container parent, JComponent comp1, JComponent comp2, int position)
	{
		return LayoutStyle.getInstance().getPreferredGap(
			comp1, comp2, LayoutStyle.ComponentPlacement.RELATED, position, parent);
	}

	static private int unrelatedGap(
		Container parent, JComponent comp1, JComponent comp2, int position)
	{
		return LayoutStyle.getInstance().getPreferredGap(
			comp1, comp2, LayoutStyle.ComponentPlacement.UNRELATED, position, parent);
	}
	
	static private int gap(Container parent, JComponent component, int position)
	{
		return LayoutStyle.getInstance().getContainerGap(component, position, parent);
	}
}
