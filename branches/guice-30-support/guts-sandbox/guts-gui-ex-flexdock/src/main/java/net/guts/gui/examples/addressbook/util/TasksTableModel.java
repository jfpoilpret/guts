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

package net.guts.gui.examples.addressbook.util;

import javax.swing.table.AbstractTableModel;

import net.guts.gui.task.TaskInfo;
import net.guts.gui.task.TaskInfo.State;
import net.guts.gui.task.TasksGroup;
import net.guts.gui.task.TasksGroupListener;

import com.google.inject.Singleton;

@Singleton
public class TasksTableModel extends AbstractTableModel implements TasksGroupListener
{
	public void setTasksGroup(TasksGroup group)
	{
		_group = group;
		_endedTasks = 0;
		_groupState = State.NOT_STARTED;
		_group.addGroupListener(this);
		fireTableDataChanged();
	}

	@Override public Class<?> getColumnClass(int columnIndex)
	{
		switch (columnIndex)
		{
			case COLUMN_STATE:
			return State.class;
			
			case COLUMN_PROGRESS:
			return Integer.class;
			
			case COLUMN_FEEDBACK:
			return String.class;
			
			default:
			return Object.class;
		}
	}

	@Override public int getColumnCount()
	{
		return NUM_COLUMNS;
	}

	@Override public int getRowCount()
	{
		if (_group != null)
		{
			return _group.tasks().size() + 1;
		}
		else
		{
			return 0;
		}
	}

	@Override public Object getValueAt(int row, int column)
	{
		if (row == 0)
		{
			switch (column)
			{
				case COLUMN_STATE:
				return groupState();
				
				case COLUMN_PROGRESS:
				return groupProgress();
				
				case COLUMN_FEEDBACK:
				default:
				return "";
			}
		}
		else
		{
			TaskInfo info = _group.tasks().get(row - 1);
			switch (column)
			{
				case COLUMN_STATE:
				return info.state();
				
				case COLUMN_PROGRESS:
				return info.progress();
				
				case COLUMN_FEEDBACK:
				return info.feedback();
				
				default:
				return "";
			}
		}
	}
	
	private int groupProgress()
	{
		return _endedTasks * 100 / _group.tasks().size();
	}
	
	private State groupState()
	{
		return _groupState;
	}
	
	@Override public void started(TasksGroup group, TaskInfo task)
	{
		_groupState = State.RUNNING;
		updateTask(task);
	}

	@Override public void cancelled(TasksGroup group, TaskInfo source)
	{
		_groupState = State.CANCELLED;
		updateTask(source);
	}

	@Override public void failed(TasksGroup group, TaskInfo source, Throwable cause)
	{
		updateTask(source);
	}

	@Override public void feedback(TasksGroup group, TaskInfo source, String note)
	{
		updateTask(source);
	}

	@Override public void finished(TasksGroup group, TaskInfo source)
	{
		_endedTasks++;
		updateTask(source);
	}

	@Override public void interrupted(TasksGroup group, TaskInfo source,
		InterruptedException cause)
	{
		updateTask(source);
	}

	@Override public void progress(TasksGroup group, TaskInfo source, int rate)
	{
		updateTask(source);
	}

	@Override public void succeeded(TasksGroup group, TaskInfo source, Object result)
	{
		updateTask(source);
	}

	@Override public void allTasksEnded(TasksGroup group)
	{
		_groupState = State.FINISHED;
		fireTableDataChanged();
	}

	@Override public void taskAdded(TasksGroup group, TaskInfo task)
	{
		int index = _group.tasks().size();
		fireTableRowsInserted(index, index);
	}

	private void updateTask(TaskInfo task)
	{
		int index = findTask(task);
		if (index > 0)
		{
			fireTableRowsUpdated(index, index);
			fireTableRowsUpdated(0, 0);
		}
	}

	private int findTask(TaskInfo task)
	{
		int index = 0;
		for (TaskInfo info: _group.tasks())
		{
			if (info == task)
			{
				return index + 1;
			}
			index++;
		}
		return -1;
	}

	static final private int NUM_COLUMNS = 3;
	static final private int COLUMN_STATE = 0;
	static final private int COLUMN_PROGRESS = 1;
	static final private int COLUMN_FEEDBACK = 2;
	
	private TasksGroup _group = null;
	private int _endedTasks = 0;
	private State _groupState = State.NOT_STARTED;
}
