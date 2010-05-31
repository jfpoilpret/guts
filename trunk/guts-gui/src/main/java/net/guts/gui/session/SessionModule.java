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

package net.guts.gui.session;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import com.google.inject.AbstractModule;

/**
 * Guice {@link com.google.inject.Module} for Guts-GUI Session State 
 * persistence system. This module must be added to the list of modules 
 * passed to {@link com.google.inject.Guice#createInjector}:
 * <pre>
 * Injector injector = Guice.createInjector(new SessionModule(), ...);
 * </pre>
 * If you use Guts-GUI {@link net.guts.gui.application.AbstractApplication}, then
 * {@code SessionModule} is automatically added to the list of {@code Module}s used
 * by Guts-GUI to create Guice {@code Injector}.
 * <p/>
 * Hence you would care about {@code SessionModule} only if you intend to use 
 * Guts-GUI Session State persistence system but don't want to use the whole 
 * Guts-GUI framework.
 * <p/>
 * Note that in this latter situation, you must also bind your main application 
 * class with {@link Sessions#bindApplicationClass}.
 *
 * @author Jean-Francois Poilpret
 */
public final class SessionModule extends AbstractModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		// Bind default SessionConverters for known types
		// Window, TabbedPane, Table, Tree...
		bindConverter(Window.class, WindowState.class);
		bindConverter(JApplet.class, AppletState.class);
		bindConverter(JFrame.class, FrameState.class);
		bindConverter(JSplitPane.class, SplitPaneState.class);
		bindConverter(JTable.class, TableState.class);
		bindConverter(JTabbedPane.class, TabbedPaneState.class);
	}
	
	private <T extends Component> void bindConverter(
		Class<T> type, Class<? extends SessionState<T>> state)
	{
		Sessions.bindSessionConverter(binder(), type).to(state);
	}

	@Override public boolean equals(Object other)
	{
		return other instanceof SessionModule;
	}

	@Override public int hashCode()
	{
		return SessionModule.class.hashCode();
	}
}
