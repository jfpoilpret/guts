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
