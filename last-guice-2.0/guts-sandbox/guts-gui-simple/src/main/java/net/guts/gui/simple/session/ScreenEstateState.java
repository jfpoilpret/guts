package net.guts.gui.simple.session;

import java.awt.Component;
import java.awt.Rectangle;

import net.guts.gui.session.SessionState;

public class ScreenEstateState<T extends Component> implements SessionState<T> {

	@Override
	public void reset() {
		_estate = null;
	}

	@Override
	public void extractState(Component component) {
		_estate = new ScreenEstate(component.getBounds());
	}

	@Override
	public void injectState(T component) {
		Rectangle bounds = (_estate != null ? _estate.bounds() : null);
		if (bounds != null) {
			// Don't use setBounds() because it doesn't seem to work with
			// JApplet!
			component.setLocation(bounds.getLocation());
			component.setSize(bounds.getSize());
		}
	}

	private ScreenEstate _estate = null;
}
