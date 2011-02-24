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

package net.guts.demo.addressbook.singleframe.util;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import net.guts.gui.action.GutsAction;
import net.guts.gui.action.TaskAction;
import net.guts.gui.task.FeedbackController;
import net.guts.gui.task.Task;
import net.guts.gui.task.TaskInfo.State;
import net.guts.gui.task.TasksGroup;
import net.guts.gui.task.blocker.BlockerDialogPane;
import net.guts.gui.task.blocker.InputBlockers;
import net.guts.gui.util.EnumIconRenderer;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TasksGroupProgressPanel extends JPanel implements BlockerDialogPane
{
	static final private long serialVersionUID = 1L;

	@Inject TasksGroupProgressPanel(TasksTableModel model)
	{
		setLayout(new BorderLayout());
		_model = model;
		_tasks = new JTable(_model);
		_tasks.getColumnModel().getColumn(1).setCellRenderer(new ProgressCellRenderer());
		
		int width = _tasks.getPreferredSize().width;
		int height = NUM_VISIBLE_TASKS * _tasks.getRowHeight();
		_tasks.setPreferredScrollableViewportSize(new Dimension(width, height));

		JScrollPane scroller = new JScrollPane(_tasks);
		add(scroller, BorderLayout.CENTER);
		add(_cancelBtn, BorderLayout.SOUTH);
	}

	@Override public void setTasksGroup(TasksGroup group)
	{
		_group = group;
		_model.setTasksGroup(group);
	}
	
	@Override public JComponent getPane()
	{
		return this;
	}

	// Used for resource injection
	void setTitle(String title)
	{
		JDialog dialog = (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, this);
		dialog.setTitle(title);
	}
	
	// Used for resource injection
	void setStateRenderer(EnumIconRenderer<State> stateRenderer)
	{
		_tasks.setDefaultRenderer(State.class, stateRenderer);
	}

	final private GutsAction _cancel = new TaskAction()
	{
		@Override protected void perform()
		{
			Task<Void> task = new Task<Void>()
			{
				@Override public Void execute(FeedbackController controller) throws Exception
				{
					_group.cancel();
					return null;
				}
			};
			submit(task, InputBlockers.actionBlocker(this));
		}
	};

	static final private int NUM_VISIBLE_TASKS = 6;
	
	final private TasksTableModel _model;
	final private JTable _tasks;
	final private JButton _cancelBtn = new JButton(_cancel);
	private TasksGroup _group;
}
