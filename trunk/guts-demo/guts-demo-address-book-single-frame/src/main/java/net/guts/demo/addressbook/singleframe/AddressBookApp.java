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

package net.guts.demo.addressbook.singleframe;

import java.util.List;

import net.guts.gui.application.AbstractApplication;
import net.guts.gui.application.AppLifecycleStarter;
import net.guts.gui.message.MessageModule;
import net.guts.gui.naming.ComponentNamingModule;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

public class AddressBookApp extends AbstractApplication
{
	public static void main(String[] args)
	{
		new AddressBookApp().launch(args);
	}
	
	@Override protected void initModules(String[] args, List<Module> modules)
	{
		// Make sure frames & dialogs are decorated by the current PLAF (substance)
//		JFrame.setDefaultLookAndFeelDecorated(true);
//		JDialog.setDefaultLookAndFeelDecorated(true);
		// Workaround Sun bug #5079688 with JFrame/JDialog resize on Windows
		System.setProperty("sun.awt.noerasebackground", "true");
		// Enable automatic content selection on focus gained for all text fields
//		UIManager.put(LafWidget.TEXT_SELECT_ON_FOCUS, Boolean.TRUE);
		// Finally, add modules we use from guts-gui
		modules.add(new MessageModule());
		modules.add(new ComponentNamingModule());
		// Finally, add our specific module
		modules.add(new AddressBookModule());
		// And the module to start the UI as a JFrame (not an JApplet)
		modules.add(new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(AppLifecycleStarter.class)
					.to(AddressBookAppLifecycleStarter.class).asEagerSingleton();
			}
		});
	}
}
