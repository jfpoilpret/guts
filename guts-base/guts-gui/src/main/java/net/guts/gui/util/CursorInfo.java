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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

/**
 * Class wrapping a {@link Cursor} and various information about it. This type
 * is directly injectable by the framework (from properties files).
 * 
 * @author Jean-Francois Poilpret
 */
public class CursorInfo
{
	public CursorInfo(Cursor cursor)
	{
		_icon = null;
		_hotspot = null;
		_size = null;
		_cursor = cursor;
	}
	
	public CursorInfo(ImageIcon icon, Point hotspot, Dimension size)
	{
		_icon = icon;
		_hotspot = hotspot;
		_size = size;
	}
	
	public ImageIcon getIcon()
	{
		return _icon;
	}

	public Point getHotspot()
	{
		return _hotspot;
	}

	public Dimension getSize()
	{
		return _size;
	}
	
	public Cursor getCursor()
	{
		if (_cursor == null)
		{
			_cursor = Toolkit.getDefaultToolkit()
				.createCustomCursor(_icon.getImage(), _hotspot, "");
		}
		return _cursor;
	}

	final private ImageIcon _icon;
	final private Point _hotspot;
	final private Dimension _size;
	private Cursor _cursor;
}
