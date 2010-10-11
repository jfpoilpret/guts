package net.guts.gui.simple.module;

/**
 * http://www.mattinsler.com/google-guice-module-de-duplication/
 *
 * */

import com.google.inject.AbstractModule;

public abstract class SingletonModule extends AbstractModule {

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		return this.getClass() == other.getClass();
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}

}