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

package net.guts.gui.resource;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.guts.gui.util.CursorHelper;
import net.guts.gui.util.CursorInfo;
import net.guts.gui.util.CursorType;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

public interface ResourceConverter<T>
{
	public T convert(ResourceEntry entry);
}

// All default Resource Converters are there:

class StringConverter implements ResourceConverter<String>
{
	@Override public String convert(ResourceEntry entry)
	{
		return entry.value();
	}
}

class BooleanConverter implements ResourceConverter<Boolean>
{
	@Override public Boolean convert(ResourceEntry entry)
	{
		return Boolean.valueOf(entry.value());
	}
}

class IntConverter implements ResourceConverter<Integer>
{
	@Override public Integer convert(ResourceEntry entry)
	{
		// Trick to support "unsigned" Hex values like 0xFF0000FF
		return (int) (long) Long.decode(entry.value());
	}
}

class ColorConverter implements ResourceConverter<Color>
{
	@Override public Color convert(ResourceEntry entry)
	{
		// Trick to support Hex RGBA values like 0xFF0000FF
		int color = (int) (long) Long.decode(entry.value());
		return new Color(color, true);
	}
}

class FontConverter implements ResourceConverter<Font>
{
	@Override public Font convert(ResourceEntry entry)
	{
		return Font.decode(entry.value());
	}
}

class IconConverter implements ResourceConverter<Icon>
{
	@Override public Icon convert(ResourceEntry entry)
	{
		URL url = entry.valueAsUrl();
		if (url != null)
		{
			return new ImageIcon(url);
		}
		else
		{
			//TODO log?
			return null;
		}
	}
}

abstract class AbstractResourceConverterFinderHolder
{
	@Inject void setFinder(ResourceConverterFinder finder)
	{
		_finder = finder;
	}
	
	final protected <T> ResourceConverter<T> converter(Class<T> type)
	{
		return _finder.getConverter(TypeLiteral.get(type));
	}
	
	final protected <T> ResourceConverter<T> converter(TypeLiteral<T> type)
	{
		return _finder.getConverter(type);
	}
	
	private ResourceConverterFinder _finder;
}

class ImageConverter extends AbstractResourceConverterFinderHolder
	implements ResourceConverter<Image>
{
	@Override public Image convert(ResourceEntry entry)
	{
		Icon icon = converter(Icon.class).convert(entry);
		if (icon != null && icon instanceof ImageIcon)
		{
			return ((ImageIcon) icon).getImage();
		}
		else
		{
			//TODO log?
			return null;
		}
	}
}

class CursorInfoConverter extends AbstractResourceConverterFinderHolder
	implements ResourceConverter<CursorInfo>
{
	@Override public CursorInfo convert(ResourceEntry entry)
	{
		CursorInfo info = _cursors.get(entry.value());
		if (info == null)
		{
			// First check if this is a predefined cursor
			CursorType type = CursorType.valueOf(entry.value());
			if (type != null)
			{
				info = type.getCursorInfo();
			}
			else
			{
				// Split s into: iconfile, hotspotx, hotspoty
				StringTokenizer tokenize = new StringTokenizer(entry.value(), ",");
				if (tokenize.countTokens() != 0 && tokenize.countTokens() != MAX_TOKENS)
				{
					//TODO log?
//					throw new ResourceConverterException(
//						"Bad format, expected: \"icon[,hotspotx[,hotspoty]]", s);
					return null;
				}
				ImageIcon icon = (ImageIcon) converter(Icon.class).convert(
					entry.derive(tokenize.nextToken()));
				double x = getHotspotRate(tokenize);
				double y = getHotspotRate(tokenize);
				info = CursorHelper.buildCursor(icon, x, y);
			}
			_cursors.put(entry.value(), info);
		}
		return info;
	}

	static private double getHotspotRate(StringTokenizer token)
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
	
	private final Map<String, CursorInfo> _cursors = new HashMap<String, CursorInfo>();
}

class CursorConverter extends AbstractResourceConverterFinderHolder
	implements ResourceConverter<Cursor>
{
	@Override public Cursor convert(ResourceEntry entry)
	{
		return converter(CursorInfo.class).convert(entry).getCursor();
	}
}
