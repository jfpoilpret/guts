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

package net.guts.gui.addressbook.util;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.guts.gui.action.GutsAction;
import net.guts.gui.action.TaskAction;
import net.guts.gui.dialog.Closable;
import net.guts.gui.dialog.ParentDialog;
import net.guts.gui.dialog.ParentDialogAware;
import net.guts.gui.task.FeedbackController;
import net.guts.gui.task.Task;
import net.guts.gui.task.TasksGroup;
import net.guts.gui.task.TaskInfo.State;
import net.guts.gui.task.blocker.BlockerDialogPane;
import net.guts.gui.task.blocker.InputBlockers;
import net.guts.gui.util.EnumIconRenderer;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TasksGroupProgressPanel extends JPanel 
	implements Closable, BlockerDialogPane, ParentDialogAware
{
	static final private long serialVersionUID = 1L;
	static final private String NAME = "MyBlockerDialog";

	@Inject TasksGroupProgressPanel(TasksTableModel model)
	{
		setLayout(new BorderLayout());
		setName(NAME);
		_model = model;
		_tasks = new JTable(_model);
		// Add special renderers for state & progress
		_stateRenderer.mapIcon(State.CANCELLED, "net/guts/gui/addressbook/icons/cross.png");
		_stateRenderer.mapIcon(State.FAILED, "net/guts/gui/addressbook/icons/cog_error.png");
		_stateRenderer.mapIcon(State.FINISHED, "net/guts/gui/addressbook/icons/tick.png");
		_stateRenderer.mapIcon(State.NOT_STARTED, "net/guts/gui/addressbook/icons/cog.png");
		_stateRenderer.mapIcon(State.RUNNING, "net/guts/gui/addressbook/icons/cog_go.png");
		_tasks.setDefaultRenderer(State.class, _stateRenderer);
		_tasks.getColumnModel().getColumn(1).setCellRenderer(new ProgressCellRenderer());
		
		int width = _tasks.getPreferredSize().width;
		int height = 6 * _tasks.getRowHeight();
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
	
	@Override public boolean canClose()
	{
		// It is not allowed to close the dialog through the close box
		return false;
	}
	
	@Override public void close()
	{
		_parent.close(false);
	}

	@Override public JComponent getPane()
	{
		return this;
	}

	@Override public void setParentDialog(ParentDialog parent)
	{
		_parent = parent;
	}

	// Used for resource injection
	void setTitle(String title)
	{
		_parent.setDialogTitle(title);
	}

	final private GutsAction _cancel = new TaskAction(NAME + "-action-cancel")
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

	final private TasksTableModel _model;
	final private JTable _tasks;
	final private EnumIconRenderer<State> _stateRenderer = 
		new EnumIconRenderer<State>(State.class);
	final private JButton _cancelBtn = new JButton(_cancel.action());
	private TasksGroup _group;
	private ParentDialog _parent;
}
