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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.gui.action.GutsAction;
import net.guts.gui.application.support.SingleFrameLifecycle;
import net.guts.gui.dialog2.DialogFactory;
import net.guts.gui.dialog2.template.okcancel.OkCancel;
import net.guts.gui.dialog2.template.wizard.Wizard;
import net.guts.gui.dialog2.template.wizard.WizardFactory;
import net.guts.gui.exception.HandlesException;
import net.guts.gui.menu.MenuFactory;
import net.guts.gui.message.MessageFactory;
import net.guts.gui.message.UserChoice;
import net.guts.gui.window.BoundsPolicy;
import net.guts.gui.window.CloseChecker;
import net.guts.gui.window.CloseCheckerConfig;
import net.guts.gui.window.JDialogConfig;
import net.guts.gui.window.RootPaneConfig;
import net.guts.gui.window.StatePolicy;

import com.google.inject.Inject;

public class DialogDemoLifecycle extends SingleFrameLifecycle
{
	static final private Logger _logger = LoggerFactory.getLogger(DialogDemoLifecycle.class);
	
	@Inject 
	public DialogDemoLifecycle(DialogFactory dialogFactory, MessageFactory messageFactory,
		WizardFactory wizardFactory)
	{
		_dialogFactory = dialogFactory;
		_messageFactory = messageFactory;
		_wizardFactory = wizardFactory;
	}
	
	@Override protected void initFrame(JFrame mainFrame)
	{
		mainFrame.setJMenuBar(createMenuBar());
		mainFrame.getContentPane().setPreferredSize(new Dimension(800, 600));
	}
	
	@HandlesException public boolean handle(Throwable e)
	{
		_logger.error("handle", e);
		return true;
	}

	private JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu file = menuFactory().createMenu("file",
			_openDialog1,
			_openDialog2,
			_openDialog3,
			MenuFactory.ACTION_SEPARATOR,
			_openWizard1,
			MenuFactory.ACTION_SEPARATOR,
			appActions().quit());
		menuBar.add(file);
		return menuBar;
	}
	
	final private GutsAction _openDialog1 = new GutsAction()
	{
		@Override protected void perform()
		{
			OkCancel config1 = OkCancel.create().withCancel();
			RootPaneConfig<JDialog> config2 = JDialogConfig.create()
				.bounds(BoundsPolicy.PACK_AND_CENTER)
				.state(StatePolicy.RESTORE_IF_EXISTS)
				.merge(config1).config();
			_dialogFactory.showDialog(DemoView1.class, config2);
			_logger.info("Result = {}", config1.result());
		}
	}; 
	
	final private GutsAction _openDialog2 = new GutsAction()
	{
		@Override protected void perform()
		{
			OkCancel config1 = OkCancel.create().withCancel();
			RootPaneConfig<JDialog> config2 = JDialogConfig.create()
				.bounds(BoundsPolicy.PACK_AND_CENTER)
				.state(StatePolicy.RESTORE_IF_EXISTS)
				.merge(config1)
				.config();
			_dialogFactory.showDialog(DemoView2.class, config2);
			_logger.info("Result = {}", config1.result());
		}
	}; 
	
	final private GutsAction _openDialog3 = new GutsAction()
	{
		@Override protected void perform()
		{
			OkCancel config1 = OkCancel.create()
				.withCancel(_cancel)
				.withOK(_apply)
				.withApply();
			RootPaneConfig<JDialog> config2 = JDialogConfig.create()
				.bounds(BoundsPolicy.PACK_AND_CENTER)
				.state(StatePolicy.RESTORE_IF_EXISTS)
				.merge(config1)
				.merge(CloseCheckerConfig.create(_closer))
				.config();
			_dialogFactory.showDialog(DemoView1.class, config2);
			_logger.info("Result = {}", config1.result());
		}
	}; 
	
	final private GutsAction _openWizard1 = new GutsAction()
	{
		@Override protected void perform()
		{
			WizardFactory.Builder builder = _wizardFactory.builder()
				.mapNextStep(DemoView1.class)
				.mapNextStep(DemoView2.class)
				.mapNextStep(DemoWizardStep3.class);
			Wizard config1 = Wizard.create()
				.withCancel(_cancel)
				.withOK(_apply)
				.withPrevious(builder.controller().getPreviousAction())
				.withNext(builder.controller().getNextAction());
			RootPaneConfig<JDialog> config2 = JDialogConfig.create()
				.bounds(BoundsPolicy.PACK_AND_CENTER)
				.state(StatePolicy.RESTORE_IF_EXISTS)
				.merge(config1)
				.config();
			//TODO Create view with all steps
			_dialogFactory.showDialog(builder.mainView(), config2);
			_logger.info("Result = {}", config1.result());
		}
	};
	
	final private GutsAction _apply = new GutsAction()
	{
		@Override protected void perform()
		{
			_messageFactory.showMessage("sample-message");
		}
	};
	
	final private GutsAction _cancel = new GutsAction()
	{
		@Override protected void perform()
		{
			_messageFactory.showMessage("sample2-message");
		}
	};
	
	final private CloseChecker _closer = new CloseChecker()
	{
		@Override public boolean canClose()
		{
			UserChoice result = _messageFactory.showMessage("close-message");
			return result == UserChoice.YES;
		}
	};
	
	final private DialogFactory _dialogFactory;
	final private MessageFactory _messageFactory;
	final private WizardFactory _wizardFactory;
}
