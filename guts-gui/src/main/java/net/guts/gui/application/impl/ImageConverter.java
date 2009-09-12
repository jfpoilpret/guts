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

import java.awt.Image;

import javax.swing.ImageIcon;

import org.jdesktop.application.ResourceConverter;
import org.jdesktop.application.ResourceMap;

/**
 * Special {@link ResourceConverter} that can convert a path to an image file
 * into an {@link Image}. This is particularly useful for injecting the minimized
 * icon for a frame: {@link javax.swing.JFrame#setIconImage(Image)}.
 * <p/>
 * This converter is automatically registered by 
 * {@link net.guts.gui.application.AbstractGuiceApplication}.
 * 
 * @author Jean-Francois Poilpret
 */
public class ImageConverter extends ResourceConverter
{
	public ImageConverter()
	{
		super(Image.class);
//		_iconConverter = forType(ImageIcon.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jdesktop.application.ResourceConverter#parseString(java.lang.String, org.jdesktop.application.ResourceMap)
	 */
	@Override public Object parseString(String s, ResourceMap r)
		throws ResourceConverterException
	{
//		ImageIcon icon = (ImageIcon) _iconConverter.parseString(s, r);
		ImageIcon icon = (ImageIcon) forType(ImageIcon.class).parseString(s, r);
		return icon.getImage();
	}

//	private final ResourceConverter _iconConverter;
}
