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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.gui.util.CursorHelper;
import net.guts.gui.util.CursorInfo;
import net.guts.gui.util.CursorType;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Defines a converter of resources ({@link java.lang.String} values read from
 * a properties file) into a given type.
 * <p/>
 * You would need to implement this interface if you need to add special support
 * of a given property type to inject by {@link ResourceInjector}.
 * <p/>
 * Your own converter may require special conventions to the {@code String} values
 * it can possibly convert.
 * <p/>
 * Custom converters can possibly use other registered {@code ResourceConverter}s, 
 * and chain calls to them; for this they can require to be injected a
 * {@code Provider<ResourceConverter<T>>} and then use this provider to get the
 * needed {@code ResourceConverter<T>}, as in the following snippet (excerpted
 * from Guts-GUI itself):
 * <pre>
 * class CursorConverter implements ResourceConverter&lt;Cursor&gt;
 * {
 *     &#64;Inject CursorConverter(
 *         Provider&lt;ResourceConverter&lt;CursorInfo&gt;&gt; cursorInfoConverter)
 *     {
 *         _cursorInfoConverter = cursorInfoConverter;
 *     }
 *     
 *     &#64;Override public Cursor convert(ResourceEntry entry)
 *     {
 *         return _cursorInfoConverter.get().convert(entry).getCursor();
 *     }
 *     
 *     private final Provider&lt;ResourceConverter&lt;CursorInfo&gt;&gt; _cursorInfoConverter;
 * }
 * </pre>
 * Note that you should not (FIXME: cannot) directly inject a {@code ResourceConverter<T>}
 * in your own {@code ResourceConverter}, that would trigger a circular dependency.
 * <p/>
 * Note that {@link ResourceInjector} already comes with {@code ResourceConverter}s
 * for many types (as described in {@link ResourceModule}) and it's unlikely you
 * would often need your own {@code ResourceConverter}.
 * <p/>
 * Adding your own implementation of {@code ResourceConverter} to Guts-GUI 
 * {@link ResourceInjector} is quite straightforward, you just have to add code
 * similar to the snippet below in one of your {@link com.google.inject.Module}s:
 * <pre>
 * Resources.bindConverter(binder(), SomeType.class).to(SomeTypeConverter.class);
 * </pre>
 * 
 * @param <T> the type to which {@code this} converter can convert a {@code String}
 * value
 *
 * @author Jean-Francois Poilpret
 */
public interface ResourceConverter<T>
{
	/**
	 * Converts a given value (read from a resource bundle file) into type {@code T}.
	 * 
	 * @param entry an entry wrapping the {@code String} value to convert, with a few
	 * utility methods that can be useful for some {@code ResourceConverter}s
	 * @return an instance of {@code T} if the value wrapped by {@code entry} could
	 * be successfully converted, or {@code null} otherwise
	 */
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
	static private final Logger _logger = LoggerFactory.getLogger(IconConverter.class);
	
	@Override public Icon convert(ResourceEntry entry)
	{
		URL url = entry.valueAsUrl();
		if (url != null)
		{
			return new ImageIcon(url);
		}
		else
		{
			_logger.debug("Could not convert {} to a valid Icon URL!", entry.value());
			return null;
		}
	}
}

class ImageConverter implements ResourceConverter<Image>
{
	static private final Logger _logger = LoggerFactory.getLogger(ImageConverter.class);

	@Inject ImageConverter(Provider<ResourceConverter<Icon>> iconConverter)
	{
		_iconConverter = iconConverter;
	}
	
	@Override public Image convert(ResourceEntry entry)
	{
		Icon icon = _iconConverter.get().convert(entry);
		if (icon != null && icon instanceof ImageIcon)
		{
			return ((ImageIcon) icon).getImage();
		}
		else
		{
			_logger.debug("Could not convert {} to a valid Image URL!", entry.value());
			return null;
		}
	}
	
	final private Provider<ResourceConverter<Icon>> _iconConverter;
}

class CursorInfoConverter implements ResourceConverter<CursorInfo>
{
	static private final Logger _logger = LoggerFactory.getLogger(CursorInfoConverter.class);
	
	@Inject CursorInfoConverter(Provider<ResourceConverter<Icon>> iconConverter)
	{
		_iconConverter = iconConverter;
	}
	
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
				// Split s into: iconfile[,hotspotx[,hotspoty]]
				StringTokenizer tokenize = new StringTokenizer(entry.value(), ",");
				if (tokenize.countTokens() < 1 || tokenize.countTokens() > MAX_TOKENS)
				{
					_logger.debug(
						"Bad cursor format: {}; expected: \"icon[,hotspotx[,hotspoty]]\".",
						entry.value());
					return null;
				}
				ImageIcon icon = (ImageIcon) _iconConverter.get().convert(
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
	private final Provider<ResourceConverter<Icon>> _iconConverter;
}

class CursorConverter implements ResourceConverter<Cursor>
{
	@Inject CursorConverter(Provider<ResourceConverter<CursorInfo>> cursorInfoConverter)
	{
		_cursorInfoConverter = cursorInfoConverter;
	}
	
	@Override public Cursor convert(ResourceEntry entry)
	{
		return _cursorInfoConverter.get().convert(entry).getCursor();
	}
	
	private final Provider<ResourceConverter<CursorInfo>> _cursorInfoConverter;
}
