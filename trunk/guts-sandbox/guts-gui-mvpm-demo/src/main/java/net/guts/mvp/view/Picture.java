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

package net.guts.mvp.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

public class Picture extends JComponent implements Scrollable
{
	private static final long serialVersionUID = 6020351706377807115L;

	public Picture()
	{
		setMinimumSize(new Dimension(MIN_SIZE, MIN_SIZE));
		setPreferredSize(new Dimension(PREF_SIZE, PREF_SIZE));
		setMaximumSize(new Dimension(MAX_SIZE, MAX_SIZE));
	}
	
	public void setIcon(ImageIcon icon)
	{
		ImageIcon oldIcon = this.icon;
		this.icon = icon;
		firePropertyChange("", oldIcon, icon);
	}
	
	public ImageIcon getIcon()
	{
		return icon;
	}

	@Override protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		int x = 0;
		int y = 0;
		int width = getWidth();
		int height = getHeight();
		if (icon != null)
		{
			// Always preserve x/y ratio
			double imageRatio = (double) icon.getIconHeight() / icon.getIconWidth();
			double availRatio = (double) height / width;
//			if (imageRatio > 1.0)
			if (imageRatio > availRatio)
			{
				// Image is higher than wide
				width = (int) (height / imageRatio);
				x = (getWidth() - width) / 2;
			}
			else
			{
				// Image is wider than high
				height = (int) (imageRatio * width);
				y = (getHeight() - height) / 2;
			}
			// Always center the image
			g.drawImage(icon.getImage(), x, y, width, height, null);
		}
		else
		{
			//TODO
		}
	}
	
	@Override public Dimension getPreferredScrollableViewportSize()
	{
		return new Dimension(PREF_SIZE, PREF_SIZE);
	}
	
	@Override public int getScrollableBlockIncrement(
		Rectangle visibleRect, int orientation, int direction)
	{
		if (orientation == SwingConstants.HORIZONTAL)
		{
			int unit = visibleRect.width;
			if (direction < 0)
			{
				return Math.min(unit, visibleRect.x);
			}
			else
			{
				return unit;
			}
		}
		else
		{
			int unit = visibleRect.height;
			if (direction < 0)
			{
				return Math.min(unit, visibleRect.y);
			}
			else
			{
				return unit;
			}
		}
	}
	
	@Override public boolean getScrollableTracksViewportHeight()
	{
		return true;
	}
	
	@Override public boolean getScrollableTracksViewportWidth()
	{
		return true;
	}
	
	@Override public int getScrollableUnitIncrement(
		Rectangle visibleRect, int orientation, int direction)
	{
		if (orientation == SwingConstants.HORIZONTAL)
		{
			if (direction < 0)
			{
				return Math.min(UNIT, visibleRect.x);
			}
			else
			{
				return UNIT;
			}
		}
		else
		{
			if (direction < 0)
			{
				return Math.min(UNIT, visibleRect.y);
			}
			else
			{
				return UNIT;
			}
		}
	}

	static final private int MIN_SIZE = 80;
	static final private int PREF_SIZE = 120;
	static final private int MAX_SIZE = 500;
	static final private int UNIT = 10;
	
	private ImageIcon icon;
}
