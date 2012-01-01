package net.guts.gui.simple.module;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.inject.Module;

public class SingletonModuleTest {

	class TestModule extends SingletonModule {

		@Override
		protected void configure() {

		}

	}

	@Test
	public void testEqualsObject() {

		Module mod1 = new SingletonModule() {
			@Override
			protected void configure() {
			}
		};

		Module mod2 = new SingletonModule() {
			@Override
			protected void configure() {
			}
		};

		assertFalse(mod1.equals(mod2));

		//

		TestModule mod3 = new TestModule();

		TestModule mod4 = new TestModule();

		assertTrue(mod3.equals(mod4));

		//

		Module mod5 = new TestModule() {

		};

		assertFalse(mod3.equals(mod5));
		assertFalse(mod4.equals(mod5));

	}

}
