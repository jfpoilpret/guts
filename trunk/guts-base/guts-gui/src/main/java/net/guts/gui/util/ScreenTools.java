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

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;

/**
 * Various utilitity methods to get information about screen real estate.
 * 
 * @author Jean-Francois Poilpret
 */
public final class ScreenTools
{
	private ScreenTools()
	{
	}

	static public Rectangle[] getScreenEstate()
	{
		GraphicsDevice[] devices = 
			GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		int size = devices.length;
		Rectangle[] estate = new Rectangle[size];
		for (int i = 0; i < size; i++)
		{
			estate[i] = devices[i].getDefaultConfiguration().getBounds();
		}
		return estate;
	}
	
	/**
	 * Calculates the usable real estate of the main monitor. Usable estate
	 * excludes special space consumed by the OS (e.g. for a Task bar).
	 * 
	 * @return the usable estate of the main screen
	 */
	static public Rectangle getMainScreenUsableBounds()
	{
		GraphicsConfiguration config = getDefaultConfig();
		Rectangle bounds = config.getBounds();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);
		return new Rectangle(	bounds.x + insets.left,
								bounds.y + insets.top,
								bounds.width - insets.left - insets.right,
								bounds.height - insets.top - insets.bottom);
	}

	/**
	 * Calculates the origin of the usable real estate of the main monitor.
	 * Usable estate excludes special space consumed by the OS (e.g. for a Task 
	 * bar).
	 * 
	 * @return the origin of usable estate of the main screen
	 */
	static public Point getMainScreenUsableOrigin()
	{
		Rectangle bounds = getMainScreenUsableBounds();
		return new Point(bounds.x, bounds.y);
	}

	/**
	 * Calculates the size of the usable real estate of the main monitor.
	 * Usable estate excludes special space consumed by the OS (e.g. for a Task 
	 * bar).
	 * 
	 * @return the size of usable estate of the main screen
	 */
	static public Dimension getMainScreenUsableSize()
	{
		Rectangle bounds = getMainScreenUsableBounds();
		return new Dimension(bounds.width, bounds.height);
	}

	/**
	 * Calculates the real estate of the main monitor, including areas that may
	 * be used by the OS (e.g. a Task bar).
	 * 
	 * @return the real estate of the main screen
	 */
	static public Rectangle getMainScreenBounds()
	{
		return new Rectangle(getDefaultConfig().getBounds());
	}

	/**
	 * Calculates the size of the real estate of the main monitor, including 
	 * areas that may be used by the OS (e.g. a Task bar).
	 * 
	 * @return the size of real estate of the main screen
	 */
	static public Dimension getMainScreenSize()
	{
		return getMainScreenBounds().getSize();
	}

	/**
	 * Calculates the origin of the real estate of the main monitor, including 
	 * areas that may be used by the OS (e.g. a Task bar).
	 * 
	 * @return the origin of real estate of the main screen
	 */
	static public Point getMainScreenOrigin()
	{
		return getMainScreenBounds().getLocation();
	}
	
	static private GraphicsConfiguration getDefaultConfig()
	{
		return GraphicsEnvironment
			.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice()
			.getDefaultConfiguration();
	}
}
