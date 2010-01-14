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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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
import static net.guts.gui.util.LayoutHelper.*;

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
		// Set names for resource injection
		_progress.setName(NAME + "-progress");
		_note.setName(NAME + "-note");
		_cancelBtn.setName(NAME + "-cancel");
		_progress.setMinimum(MIN_RATE);
		_progress.setMaximum(MAX_RATE);
		//FIXME externalize text in some FW resource bundle!
		_cancelBtn.setText("Cancel Task");
		initLayout();
	}
	
	void setTasksGroup(TasksGroup tasks)
	{
		_tasks = tasks;
		_cancel.action().setEnabled(_tasks.isCancellable());
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
	
	private void initLayout()
	{
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// Calculate border gaps
		int top = topGap(this, _note);
		int bottom = bottomGap(this, _cancelBtn);
		int left = maxLeftGap(this, _note, _progress, _cancelBtn);
		int right = maxRightGap(this, _note, _progress, _cancelBtn);

		constraints.anchor = GridBagConstraints.BASELINE_LEADING;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridwidth = 1;
		constraints.weightx = 1.0;
		constraints.gridy = 0;
		constraints.gridheight = 1;
		constraints.weighty = 0.0;
		
		constraints.insets = 
			new Insets(top, left, relatedVerticalGap(this, _note, _progress), right);
		add(_note, constraints);
		
		constraints.gridy++;
		constraints.insets = 
			new Insets(0, left, unrelatedVerticalGap(this, _progress, _cancelBtn), right);
		add(_progress, constraints);
		
		constraints.gridy++;
		constraints.insets = new Insets(0, left, bottom, right);
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.NONE;
		add(_cancelBtn, constraints);
		// I wish I had used DesignGridLayout for that method...
//		DesignGridLayout layout = new DesignGridLayout(this);
//		layout.row().left().fill().add(_note);
//		layout.row().left().fill().add(_progress);
//		layout.emptyRow();
//		layout.row().center().add(_cancelBtn);
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

	final private GutsAction _cancel = new GutsAction(NAME + "-action-cancel")
	{
		@Override protected void perform()
		{
			//TODO normally the cancellation should also be a blocking task!
			_tasks.getExecutor().cancel();
		}
	};

	static final private int MIN_RATE = 0;
	static final private int MAX_RATE = 100;
	
	final private JLabel _note = new JLabel("");
	final private JProgressBar _progress = new JProgressBar();
	final private JButton _cancelBtn = new JButton(_cancel.action());
	private ParentDialog _parent;
	private TasksGroup _tasks;
}
