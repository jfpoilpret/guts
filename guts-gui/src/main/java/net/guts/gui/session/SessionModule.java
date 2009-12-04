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

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import com.google.inject.AbstractModule;

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
}
