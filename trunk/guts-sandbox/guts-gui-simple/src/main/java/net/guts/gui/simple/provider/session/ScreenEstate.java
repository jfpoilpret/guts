package net.guts.gui.simple.provider.session;

import java.awt.Rectangle;
import java.util.Arrays;

import net.guts.gui.util.ScreenTools;

public class ScreenEstate {

	// Default constructor for deserialization
	public ScreenEstate() {
	}

	ScreenEstate(Rectangle bounds) {
		_bounds = bounds;
		_screensEstate = ScreenTools.getScreenEstate();
	}

	Rectangle bounds() {

		// Check if screen real estate has changed since previous session
		if (_bounds != null
				&& Arrays.equals(_screensEstate, ScreenTools.getScreenEstate())) {
			return _bounds;
		} else {
			return null;
		}

	}

	private Rectangle[] _screensEstate;
	private Rectangle _bounds;

}
