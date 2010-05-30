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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import net.guts.gui.application.WindowController;
import net.guts.gui.application.WindowController.BoundsPolicy;
import net.guts.gui.exit.ExitController;

import com.google.inject.Inject;

public class ApplicationUIStarter implements AddressBookUIStarter
{
	@Inject public ApplicationUIStarter(
		WindowController windowController, ExitController exitController)
	{
		_windowController = windowController;
		_exitController = exitController;
	}
	
	@Override public void showUI(JMenuBar menuBar, JPanel mainView)
	{
		JFrame mainFrame = new JFrame();
		mainFrame.setName("mainFrame");
		//TODO Guts-GUI should always provide this somehow
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter()
		{
			@Override public void windowClosing(WindowEvent arg0)
			{
				_exitController.shutdown();
			}
		});

		// Initialize the main frame content: 3 panels are there, separated by JSplitPanes
		mainFrame.setJMenuBar(menuBar);
		mainFrame.setContentPane(mainView);
		_windowController.show(mainFrame, BoundsPolicy.PACK_AND_CENTER, true);
	}

	final private WindowController _windowController;
	final private ExitController _exitController;
}
