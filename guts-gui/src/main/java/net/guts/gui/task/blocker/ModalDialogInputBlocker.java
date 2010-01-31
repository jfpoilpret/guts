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

package net.guts.gui.task.blocker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import net.guts.gui.application.WindowController.BoundsPolicy;
import net.guts.gui.dialog.DialogFactory;
import net.guts.gui.task.TasksGroup;

import com.google.inject.Inject;

/**
 * This {@link InputBlocker} implementation displays a modal dialog after a given
 * amount of time following start of tasks execution, until the execution end.
 * The dialog displays tasks progress as reported by individual 
 * {@link net.guts.gui.task.Task}s. It also includes a "cancel" button that is
 * enabled for a cancellable {@link TasksGroup}.
 * <p/>
 * The time to wait (in ms) until the dialog is displayed is configurable as a 
 * resource and can be set in the application resource bundle; resources for
 * i18n of the modal dialog are also settable as follows:
 * <pre>
 * # Default delay before showing progress dialog (default 250ms)
 * ModalDialogInputBlocker.waitBeforeDialog=500
 * # Default delay before closing progress dialog after all tasks finished (default 100ms)
 * ModalDialogInputBlocker.waitBeforeClose=200
 * 
 * BlockerDialog.title=Task in progress...
 * BlockerDialog-action-cancel.text=Cancel Task
 * </pre>
 * By default, the waiting time is set to 250ms. 
 * <p/>
 * You can provide your own dialog to {@code ModalDialogInputBlocker} if the
 * default dialog doesn't suit your needs. For this, you need to provide a
 * {@link BlockerDialogPane} implementation and explicitly bind it with Guice:
 * <pre>
 * class MyUserInputModalDialog extends JPanel 
 *     implements BlockerDialogPane, ParentDialogAware
 * {
 *     // Constructor handles UI setup...
 *     
 *     &#64;Override public JComponent getPane()
 *     {
 *         return this;
 *     }
 *     
 *     &#64;Override public void setTasksGroup(TasksGroup tasks)
 *     {
 *         // Add listener to tasks in order to update the UI
 *         tasks.addListener(new TaskAdapter&lt;Object&gt;()
 *         {
 *             // Override the interesting methods for UI update...
 *         });
 *     }
 *     
 *     &#64;Override public void close()
 *     {
 *         // Close the embedding JDialog
 *     }
 * }
 * ...
 * 
 *     // Then, inside the configure() method of your Modules
 *     bind(BlockerDialogPane.class).to(MyUserInputModalDialog.class);
 * </pre>
 *
 * @author Jean-Francois Poilpret
 */
//TODO make sure resource injection accounts for source GutsAction name!
public class ModalDialogInputBlocker implements InputBlocker
{
	public ModalDialogInputBlocker()
	{
		_startDelay = new Timer(0, new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				showDialog();
			}
		});
		_startDelay.setRepeats(false);
		_startDelay.setInitialDelay(DEFAULT_WAIT_BEFORE_DIALOG);
		
		_closeDelay = new Timer(0, new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent arg0)
			{
				_pane.close();
			}
		});
		_closeDelay.setRepeats(false);
		_closeDelay.setInitialDelay(DEFAULT_WAIT_BEFORE_CLOSE);
	}
	
	@Inject void init(DialogFactory dialogFactory, BlockerDialogPane pane)
	{
		_dialogFactory = dialogFactory;
		_pane = pane;
	}
	
	private void showDialog()
	{
		_openDialog = true;
		_dialogFactory.showDialog(_pane.getPane(), BoundsPolicy.PACK_AND_CENTER, false);
	}

	@Override public void block(TasksGroup tasks)
	{
		_openDialog = false;
		_pane.setTasksGroup(tasks);
		_startDelay.start();
	}

	@Override public void unblock(TasksGroup tasks)
	{
		_startDelay.stop();
		if (_openDialog)
		{
			_closeDelay.start();
		}
	}
	
	void setWaitBeforeDialog(int wait)
	{
		_startDelay.setInitialDelay(Math.max(0, wait));
	}

	void setWaitBeforeClose(int wait)
	{
		_closeDelay.setInitialDelay(Math.max(0, wait));
	}

	static final private int DEFAULT_WAIT_BEFORE_DIALOG = 250;
	static final private int DEFAULT_WAIT_BEFORE_CLOSE = 100;
	
	final private Timer _startDelay;
	final private Timer _closeDelay;
	private DialogFactory _dialogFactory;
	private BlockerDialogPane _pane;
	private boolean _openDialog = false;
}
