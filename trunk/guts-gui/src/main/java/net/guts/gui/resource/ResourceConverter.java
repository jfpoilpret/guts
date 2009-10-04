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
import java.awt.Font;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * TODO
 *
 * @author Jean-Francois Poilpret
 */
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
		return Integer.decode(entry.value());
	}
}

class ColorConverter implements ResourceConverter<Color>
{
	@Override public Color convert(ResourceEntry entry)
	{
		int color = Integer.decode(entry.value());
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

//TODO missing resource types: Image, Cursor...
