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

package net.guts.gui.window;

import com.google.inject.AbstractModule;

/**
 * Guice {@link com.google.inject.Module} for Guts-GUI {@link WindowController} 
 * system. This module must be added to the list of modules passed to 
 * {@link com.google.inject.Guice#createInjector}:
 * <pre>
 * Injector injector = Guice.createInjector(new WindowModule(), ...);
 * </pre>
 * If you use Guts-GUI {@link net.guts.gui.application.AbstractApplication}, then
 * {@code WindowModule} is automatically added to the list of {@code Module}s used
 * by Guts-GUI to create Guice {@code Injector}.
 * <p/>
 * Hence you would care about {@code WindowModule} only if you intend to use 
 * Guts-GUI {@link WindowController} system but don't want to use the whole 
 * Guts-GUI framework.
 *
 * @author Jean-Francois Poilpret
 */
public final class WindowModule extends AbstractModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		bind(ActiveWindow.class).asEagerSingleton();
		// Create Map<Integer, WindowProcessor>
		Windows.windowProcessors(binder());
		// Add WP for various aspects
		Windows.bindWindowProcessor(binder(), WindowProcessor.BOUNDS_INIT)
			.to(WpWindowBoundsInit.class);
		Windows.bindWindowProcessor(binder(), WindowProcessor.BOUNDS_INIT + 1)
			.to(WpAppletBoundsInit.class);
		Windows.bindWindowProcessor(binder(), WindowProcessor.BOUNDS_INIT + 2)
			.to(WpInternalFrameBoundsInit.class);
		// Add WP that adds close listeners to windows
		Windows.bindWindowProcessor(binder(), WindowProcessor.CLOSE_CHECKER_SETUP)
			.to(WpWindowCloseChecking.class);
		Windows.bindWindowProcessor(binder(), WindowProcessor.CLOSE_CHECKER_SETUP + 1)
			.to(WpInternalFrameCloseChecking.class);
		// Add WP that eventually displays the window
		Windows.bindWindowProcessor(binder(), WindowProcessor.DISPLAY)
			.to(WpWindowDisplay.class);
		Windows.bindWindowProcessor(binder(), WindowProcessor.DISPLAY + 1)
			.to(WpInternalFrameDisplay.class);
	}
	
	@Override public boolean equals(Object other)
	{
		return other instanceof WindowModule;
	}

	@Override public int hashCode()
	{
		return WindowModule.class.hashCode();
	}
}
