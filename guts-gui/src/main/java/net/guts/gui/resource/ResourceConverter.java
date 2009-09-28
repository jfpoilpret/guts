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

import javax.swing.Icon;

/**
 * TODO
 *
 * @author Jean-Francois Poilpret
 */
public interface ResourceConverter<T>
{
	public T convert(String value);
}

// All default Resource Converters are there:

class StringConverter implements ResourceConverter<String>
{
	@Override public String convert(String value)
	{
		return value;
	}
}

class BooleanConverter implements ResourceConverter<Boolean>
{
	@Override public Boolean convert(String value)
	{
		return Boolean.valueOf(value);
	}
}

class IntConverter implements ResourceConverter<Integer>
{
	@Override public Integer convert(String value)
	{
		return Integer.decode(value);
	}
}

class ColorConverter implements ResourceConverter<Color>
{
	@Override public Color convert(String value)
	{
		//TODO Check if this works also with Alpha channel (I think not)
		return Color.decode(value);
	}
}

class FontConverter implements ResourceConverter<Font>
{
	@Override public Font convert(String value)
	{
		return Font.decode(value);
	}
}

class IconConverter implements ResourceConverter<Icon>
{
	@Override public Icon convert(String value)
	{
		// TODO Auto-generated method stub
		return null;
	}
}

//TODO missing resource types: ImageIcon, Cursor...
