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

package net.guts.gui.application.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;

import org.jdesktop.application.ResourceConverter;
import org.jdesktop.application.ResourceMap;

import net.guts.gui.util.CursorHelper;
import net.guts.gui.util.CursorInfo;
import net.guts.gui.util.CursorType;

/**
 * Special {@link ResourceConverter} that can convert a string into a 
 * {@link CursorInfo}.
 * <p/>
 * The main difference with {@link CursorConverter} is that injecting 
 * {@link CursorInfo} allows to know more information about the cursor (e.g.
 * size and hotspot) that you don't have through {@link java.awt.Cursor} itself.
 * <p/>
 * Cursors definition in properties files is defined in {@link CursorConverter}.
 * <p/>
 * This converter is automatically registered by 
 * {@link net.guts.gui.application.AbstractGuiceApplication}.
 * 
 * @author Jean-Francois Poilpret
 */
public class CursorInfoConverter extends ResourceConverter
{
	public CursorInfoConverter()
	{
		super(CursorInfo.class);
		_iconConverter = forType(ImageIcon.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jdesktop.application.ResourceConverter#parseString(java.lang.String, org.jdesktop.application.ResourceMap)
	 */
	@Override public Object parseString(String s, ResourceMap r)
		throws ResourceConverterException
	{
		CursorInfo info = _cursors.get(s);
		if (info == null)
		{
			// First check if this is a predefined cursor
			CursorType type = CursorType.valueOf(s);
			if (type != null)
			{
				info = type.getCursorInfo();
			}
			else
			{
				// Split s into: iconfile, hotspotx, hotspoty
				StringTokenizer tokenize = new StringTokenizer(s, ",");
				if (tokenize.countTokens() != 0 && tokenize.countTokens() != MAX_TOKENS)
				{
					throw new ResourceConverterException(
						"Bad format, expected: \"icon[,hotspotx[,hotspoty]]", s);
				}
				ImageIcon icon = 
					(ImageIcon) _iconConverter.parseString(tokenize.nextToken(), r);
				double x = getHotspotRate(tokenize);
				double y = getHotspotRate(tokenize);
				info = CursorHelper.buildCursor(icon, x, y);
			}
			_cursors.put(s, info);
		}
		return info;
	}
	
	static private double	getHotspotRate(StringTokenizer token)
	{
		if (!token.hasMoreTokens())
		{
			return MEAN_COORDINATE;
		}
		try
		{
			double coord = Double.parseDouble(token.nextToken());
			if (coord >= 1.0)
			{
				coord = MAX_COORDINATE;
			}
			else if (coord < 0.0)
			{
				coord = MEAN_COORDINATE;
			}
			return coord;
		}
		catch (NumberFormatException e)
		{
			return MEAN_COORDINATE;
		}
	}

	static private final int	MAX_TOKENS		= 3;
	static private final double	MAX_COORDINATE	= 0.99;
	static private final double	MEAN_COORDINATE	= 0.5;
	
	private final ResourceConverter _iconConverter;
	private final Map<String, CursorInfo> _cursors = new HashMap<String, CursorInfo>();
}
