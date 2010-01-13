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

import net.guts.gui.dialog.DialogFactory;
import net.guts.gui.task.TasksGroup;

import com.google.inject.Inject;

//TODO get timeout from resources!
//TODO make sure resource injection accounts for source GutsAction name!
public class ModalDialogInputBlocker implements InputBlocker
{
	public ModalDialogInputBlocker()
	{
		_timeout = new Timer(0, new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				showDialog();
			}
		});
		//TODO replace with injected resource!
		_timeout.setInitialDelay(1000);
	}
	
	@Inject void init(DialogFactory dialogFactory, BlockerDialogPane pane)
	{
		_dialogFactory = dialogFactory;
		_pane = pane;
	}
	
	private void showDialog()
	{
		_dialogFactory.showDialog(_pane);
	}

	@Override public void block(TasksGroup tasks)
	{
		_pane.setTasksGroup(tasks);
		_timeout.start();
	}

	@Override public void unblock(TasksGroup tasks)
	{
		_timeout.stop();
		_pane.close();
	}

	final private Timer _timeout;
	private DialogFactory _dialogFactory;
	private BlockerDialogPane _pane;
}
