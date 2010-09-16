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

import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.fest.assertions.Assertions;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "itest")
public class SessionManagerTest
{
	public void checkWindowStateStoreAndRestore()
	{
		SessionManager manager = manager();
		Window window = window("checkWindowStateStoreAndRestore");
		window.setBounds(20, 10, 400, 300);
		
		manager.save(window);
		window = window("checkWindowStateStoreAndRestore");
		manager.restore(window);
		
		Assertions.assertThat(window.getX()).as("window.x").isEqualTo(20);
		Assertions.assertThat(window.getY()).as("window.y").isEqualTo(10);
		Assertions.assertThat(window.getWidth()).as("window.width").isEqualTo(400);
		Assertions.assertThat(window.getHeight()).as("window.height").isEqualTo(300);
	}
	
	public void checkWindowStateNotRestoredIfNotSavedBefore()
	{
		SessionManager manager = manager();
		Window window = window("checkWindowStateNotRestoredIfNotSavedBefore");
		window.setBounds(20, 10, 400, 300);
		manager.restore(window);
		
		Assertions.assertThat(window.getX()).as("window.x").isEqualTo(20);
		Assertions.assertThat(window.getY()).as("window.y").isEqualTo(10);
		Assertions.assertThat(window.getWidth()).as("window.width").isEqualTo(400);
		Assertions.assertThat(window.getHeight()).as("window.height").isEqualTo(300);
	}
	
	public void checkSplitPane()
	{
		SessionManager manager = manager();
		Window window = window("checkSplitPane");
		JSplitPane split = initSplitPaneWindow(window);
		
		int divider = window.getWidth() / 4;
		split.setDividerLocation(divider);
		manager.save(window);

		window = window("checkSplitPane");
		split = initSplitPaneWindow(window);
		manager.restore(window);

		Assertions.assertThat(split.getDividerLocation()).as("split.divider").isEqualTo(divider);
	}
	
	private JSplitPane initSplitPaneWindow(Window window)
	{
		JSplitPane split = new JSplitPane();
		split.setName("split");
		split.setLeftComponent(new JLabel("LEFT"));
		split.setRightComponent(new JLabel("RIGHT"));
		window.add(split);
		window.pack();
		return split;
	}
	
	public void checkTable()
	{
		SessionManager manager = manager();
		Window window = window("checkTable");
		JTable table = initTableWindow(window);

		TableColumn column = table.getColumnModel().getColumn(0);
		table.removeColumn(column);
		int width = table.getColumnModel().getColumn(0).getWidth();
		manager.save(window);

		window = window("checkTable");
		table = initTableWindow(window);
		manager.restore(window);

		Assertions.assertThat(table.getColumnModel().getColumn(0).getWidth()).as("table.column[0].width").isEqualTo(width);
	}
	
	private JTable initTableWindow(Window window)
	{
		Object[] columns = {"First column", "C2"};
		Object[][] rows = {{23, 32}, {14, 41}, {67, 76}};
		JTable table = new JTable(rows, columns);
		table.setName("table");
		window.add(table);
		window.pack();
		return table;
	}

	public void checkTabbedPane()
	{
		SessionManager manager = manager();
		Window window = window("checkTabbedPane");
		JTabbedPane tabs = initTabWindow(window);

		Assertions.assertThat(tabs.getSelectedIndex()).as("tabs.selected").isEqualTo(0);
		tabs.setSelectedIndex(1);
		manager.save(window);
		
		window = window("checkTabbedPane");
		tabs = initTabWindow(window);
		manager.restore(window);
		
		Assertions.assertThat(tabs.getSelectedIndex()).as("tabs.selected").isEqualTo(1);
	}

	private JTabbedPane initTabWindow(Window window)
	{
		JTabbedPane tabs = new JTabbedPane();
		tabs.setName("tabs");
		tabs.addTab("First Tab", new JLabel("ONE"));
		tabs.addTab("Tab 2", new JLabel("TWO"));
		window.add(tabs);
		window.pack();
		return tabs;
	}
	
	private SessionManager manager()
	{
		Injector injector = Guice.createInjector(new SessionModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				Sessions.bindApplicationClass(binder(), SessionManagerTest.class);
			}
		});
		return injector.getInstance(SessionManager.class);
	}
	
	private Window window(String id)
	{
		JFrame frame = new JFrame();
		frame.setName(id);
		return frame;
	}
}
