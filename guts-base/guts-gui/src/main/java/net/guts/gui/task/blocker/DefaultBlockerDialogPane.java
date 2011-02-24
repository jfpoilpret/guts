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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import net.guts.gui.action.GutsAction;
import net.guts.gui.action.TaskAction;
import net.guts.gui.task.FeedbackController;
import net.guts.gui.task.Task;
import net.guts.gui.task.TaskInfo;
import net.guts.gui.task.TasksGroup;
import net.guts.gui.task.TasksGroupAdapter;
import static net.guts.gui.util.LayoutHelper.bottomGap;
import static net.guts.gui.util.LayoutHelper.maxLeftGap;
import static net.guts.gui.util.LayoutHelper.maxRightGap;
import static net.guts.gui.util.LayoutHelper.relatedVerticalGap;
import static net.guts.gui.util.LayoutHelper.topGap;
import static net.guts.gui.util.LayoutHelper.unrelatedVerticalGap;

import com.google.inject.Inject;

@SuppressWarnings("serial") 
class DefaultBlockerDialogPane extends JPanel implements BlockerDialogPane
{
	static final private String NAME = "BlockerDialog";
	
	@Inject DefaultBlockerDialogPane()
	{
		setName(NAME);
		// Set names for resource injection
		_progress.setName(NAME + "-progress");
		_note.setName(NAME + "-note");
		_cancelBtn.setName(NAME + "-cancel");
		_progress.setMinimum(MIN_RATE);
		_progress.setMaximum(MAX_RATE);
		initLayout();
	}
	
	@Override public JComponent getPane()
	{
		return this;
	}
	
	@Override public void setTasksGroup(TasksGroup tasks)
	{
		_tasks = tasks;
		_note.setText("");
		_progress.setValue(MIN_RATE);
		_cancel.setEnabled(_tasks.isCancellable());
		_tasks.addGroupListener(new TasksGroupAdapter()
		{
			@Override public void feedback(TasksGroup group, TaskInfo source, String note)
			{
				_note.setText(note);
			}

			@Override public void progress(TasksGroup group, TaskInfo source, int rate)
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
	}
	
	// Used for resource injection
	void setTitle(String title)
	{
		JDialog dialog = (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, this);
		dialog.setTitle(title);
	}

	final private GutsAction _cancel = new TaskAction(NAME + "-action-cancel")
	{
		@Override protected void perform()
		{
			Task<Void> task = new Task<Void>()
			{
				@Override public Void execute(FeedbackController controller) throws Exception
				{
					_tasks.cancel();
					return null;
				}
			};
			submit(task, InputBlockers.actionBlocker(this));
		}
	};

	static final private int MIN_RATE = 0;
	static final private int MAX_RATE = 100;
	
	final private JLabel _note = new JLabel("");
	final private JProgressBar _progress = new JProgressBar();
	final private JButton _cancelBtn = new JButton(_cancel);
	private TasksGroup _tasks;
}
