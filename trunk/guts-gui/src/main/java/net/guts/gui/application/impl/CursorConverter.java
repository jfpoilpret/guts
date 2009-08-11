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

import java.awt.Cursor;

import org.jdesktop.application.ResourceConverter;
import org.jdesktop.application.ResourceMap;

import net.guts.gui.util.CursorInfo;

/**
 * Special {@link ResourceConverter} that can convert a string into a 
 * {@link Cursor} so that it can be injected into any component by the Swing
 * Application Framework.
 * <p/>
 * Inside properties files, cursors are defined in one of two ways:
 * <pre>
 * eraser.toolcursor=icons/tools/eraser-cursor.png,0.5,0.5
 * </pre>
 * or
 * <pre>
 * text.toolcursor=TEXT
 * </pre>
 * In the first example, the value is expressed as "{@code image,x,y}" where
 * {@code image} is a path (in the classpath) to an image file (jpeg, gif or png),
 * {@code x} and {@code y} are the relative coordinates of the cursor "hotspot",
 * expressed as a percentage (0.0 - 1.0, 0.0 being leftmost/topmost, 1.0 being
 * rightmost/bottommost)) of the actual image size. {@code x} and {@code y} are
 * optional and default to 0.5 (hotspot is in the center of the image).
 * <p/>
 * In the second example, the value is one constant string that defines a 
 * predefined cursor. The possible values can be found in 
 * {@link net.guts.gui.util.CursorType}.
 * <p/>
 * This converter is automatically registered by 
 * {@link net.guts.gui.application.AbstractGuiceApplication}.
 * <p/>
 * Note: this converter depends on {@link CursorInfoConverter} being registered
 * first (this is what {@code AbstractGuiceApplication} does).
 * 
 * @author Jean-Francois Poilpret
 */
public class CursorConverter extends ResourceConverter
{
	public CursorConverter()
	{
		super(Cursor.class);
		_infoConverter = forType(CursorInfo.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jdesktop.application.ResourceConverter#parseString(java.lang.String, org.jdesktop.application.ResourceMap)
	 */
	@Override public Object parseString(String s, ResourceMap r)
		throws ResourceConverterException
	{
		return ((CursorInfo) _infoConverter.parseString(s, r)).getCursor();
	}
	
	private ResourceConverter _infoConverter;
}
