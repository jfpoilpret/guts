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

package net.guts.demo.dialog;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import net.guts.gui.action.GutsAction;
import net.guts.gui.application.support.SingleFrameLifecycle;
import net.guts.gui.dialog2.DialogFactory;
import net.guts.gui.dialog2.template.OkCancel;
import net.guts.gui.menu.MenuFactory;
import net.guts.gui.window.BoundsPolicy;
import net.guts.gui.window.JDialogConfig;
import net.guts.gui.window.RootPaneConfig;
import net.guts.gui.window.StatePolicy;

import com.google.inject.Inject;

public class DialogDemoLifecycle extends SingleFrameLifecycle
{
	@Inject public DialogDemoLifecycle(DialogFactory dialogFactory)
	{
		_dialogFactory = dialogFactory;
	}
	
	@Override protected void initFrame(JFrame mainFrame)
	{
		mainFrame.setJMenuBar(createMenuBar());
		mainFrame.getContentPane().setPreferredSize(new Dimension(800, 600));
	}

	private JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu file = menuFactory().createMenu("file",
			_openDialog,
			MenuFactory.ACTION_SEPARATOR,
			appActions().quit());
		menuBar.add(file);
		return menuBar;
	}
	
	final private GutsAction _openDialog = new GutsAction()
	{
		@Override protected void perform()
		{
			OkCancel config1 = OkCancel.create().withCancel();
			RootPaneConfig<JDialog> config2 = JDialogConfig.create()
				.bounds(BoundsPolicy.PACK_AND_CENTER)
				.state(StatePolicy.RESTORE_IF_EXISTS)
				.merge(config1).config();
			_dialogFactory.showDialog(DemoView.class, config2);
			System.out.println("Result = " + config1.result());
		}
	}; 
	
	final private DialogFactory _dialogFactory;
}