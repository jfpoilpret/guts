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

package net.guts.gui.examples.addressbook;

import javax.swing.JApplet;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import com.google.inject.Inject;

public class AppletUIStarter implements AddressBookUIStarter
{
	@Inject public AppletUIStarter(JApplet applet)
	{
		_applet = applet;
	}
	
	@Override public void showUI(JMenuBar menuBar, JPanel mainView)
	{
		_applet.setName("mainFrame");
		_applet.setJMenuBar(menuBar);
		_applet.setContentPane(mainView);
	}
	
	final private JApplet _applet;
}
