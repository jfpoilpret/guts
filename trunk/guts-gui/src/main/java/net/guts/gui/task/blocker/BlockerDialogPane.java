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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.guts.gui.action.GutsAction;
import net.guts.gui.dialog.Closable;
import net.guts.gui.dialog.ParentDialog;
import net.guts.gui.dialog.ParentDialogAware;
import net.guts.gui.task.Task;
import net.guts.gui.task.TaskAdapter;
import net.guts.gui.task.TasksGroup;
import net.java.dev.designgridlayout.DesignGridLayout;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial") 
@Singleton
class BlockerDialogPane extends JPanel implements Closable, ParentDialogAware
{
	static final private String NAME = "BlockerDialog";
	
	@Inject BlockerDialogPane()
	{
		setName(NAME);
		//TODO set names for resource injection!
		_progress.setMinimum(0);
		_progress.setMaximum(100);
		_cancelBtn.setText("Cancel Task");
		initLayout();
	}
	
	void setTasksGroup(TasksGroup tasks)
	{
		_tasks = tasks;
		_cancel.action().setEnabled(_tasks.isCancellable());
		//TODO listen to progress!
		_tasks.addListener(new TaskAdapter<Object>()
		{

			@Override public void feedback(TasksGroup group, Task<? extends Object> source,
				String note)
			{
				_note.setText(note);
			}

			@Override public void progress(TasksGroup group, Task<? extends Object> source,
				int rate)
			{
				_progress.setValue(rate);
			}
		});
	}
	
	//FIXME Change to GBL to avoid DGL as mandatory dependency in runtime
	private void initLayout()
	{
		DesignGridLayout layout = new DesignGridLayout(this);
		layout.row().left().fill().add(_note);
		layout.row().left().fill().add(_progress);
		layout.emptyRow();
		layout.row().center().add(_cancelBtn);
	}
	
	@Override public boolean canClose()
	{
		// It is not allowed to close the dialog through the close box
		return false;
	}

	@Override public void setParentDialog(ParentDialog parent)
	{
		_parent = parent;
	}
	
	void close()
	{
		if (_parent != null)
		{
			_parent.close(true);
			_parent = null;
		}
	}

	// Used for resource injection
	void setTitle(String title)
	{
		_parent.setDialogTitle(title);
	}

	final private GutsAction _cancel = new GutsAction(NAME + "-cancel")
	{
		@Override protected void perform()
		{
			//TODO normally the cancellation should also be a blocking task!
			_tasks.getExecutor().cancel();
		}
	};

	final private JLabel _note = new JLabel("");
	final private JProgressBar _progress = new JProgressBar();
	final private JButton _cancelBtn = new JButton(_cancel.action());
	private ParentDialog _parent;
	private TasksGroup _tasks;
}
