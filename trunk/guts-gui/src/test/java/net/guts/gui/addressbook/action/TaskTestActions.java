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

package net.guts.gui.addressbook.action;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.guts.gui.action.GutsAction;
import net.guts.gui.action.TaskAction;
import net.guts.gui.action.TasksGroupAction;
import net.guts.gui.task.AbstractTask;
import net.guts.gui.task.FeedbackController;
import net.guts.gui.task.Task;
import net.guts.gui.task.TasksGroup;
import net.guts.gui.task.blocker.InputBlockers;

import com.google.inject.Singleton;

//CSOFF: LineLengthCheck
//CSOFF: VisibilityModifierCheck
//CSOFF: MagicNumberCheck
@Singleton
public class TaskTestActions
{
	public final GutsAction _oneTaskNoBlocker = new TaskAction("oneTaskNoBlocker")
	{
		@Override protected void perform()
		{
			submit(new LongTask("oneTaskNoBlocker"));
		}
	};
	
	public final GutsAction _oneTaskActionBlocker = new TaskAction("oneTaskActionBlocker")
	{
		@Override protected void perform()
		{
			submit(new LongTask("oneTaskActionBlocker"), InputBlockers.actionBlocker(this));
		}
	};
	
	public final GutsAction _oneTaskComponentBlocker = new TaskAction("oneTaskComponentBlocker")
	{
		@Override protected void perform()
		{
			submit(new LongTask("oneTaskComponentBlocker"), InputBlockers.componentBlocker(this));
		}
	};
	
	public final GutsAction _oneTaskWindowBlocker = new TaskAction("oneTaskWindowBlocker")
	{
		@Override protected void perform()
		{
			submit(new LongTask("oneTaskWindowBlocker"), InputBlockers.windowBlocker(this));
		}
	};
	
	public final GutsAction _oneTaskDialogBlocker = new TaskAction("oneTaskDialogBlocker")
	{
		@Override protected void perform()
		{
			submit(new LongTask("oneTaskDialogBlocker"), InputBlockers.dialogBlocker());
		}
	};
	
	public final GutsAction _oneTaskProgressDialogBlocker = new TaskAction("oneTaskProgressDialogBlocker")
	{
		@Override protected void perform()
		{
			submit(new LongProgressTask("oneTaskProgressDialogBlocker"), InputBlockers.dialogBlocker());
		}
	};
	
	public final GutsAction _oneTaskSerialExecutor = new TaskAction("oneTaskNoBlockerSerialExecutor")
	{
		@Override protected void perform()
		{
			submit(new LongTask("oneTaskDialogBlocker"), InputBlockers.noBlocker(), _serialExecutor);
		}
	};
	
	public final GutsAction _fiveTasksDialogBlocker = new TasksGroupAction("fiveTasksDialogBlocker")
	{
		@Override protected void perform()
		{
			TasksGroup group = newTasksGroup(
				"fiveTasksDialogBlocker", true, InputBlockers.dialogBlocker());
			for (int i = 0; i < 5; i++)
			{
				group.add(new LongTask("fiveTasksDialogBlocker #" + i));
			}
			group.getExecutor().execute();
		}
	};
	
	public final GutsAction _twoSerialTaskDialogBlocker = new TasksGroupAction("twoSerialTaskDialogBlocker")
	{
		@Override protected void perform()
		{
			final TasksGroup theGroup = newTasksGroup(
				"twoSerialTaskDialogBlocker", true, InputBlockers.dialogBlocker());
			Task<?> task1 = new LongProgressTask("twoSerialTaskDialogBlocker #1")
			{
				@Override public Void execute(FeedbackController controller) throws Exception
				{
					super.execute(controller);
					theGroup.add(new LongProgressTask("twoSerialTaskDialogBlocker #2"));
					return null;
				}
			};
			theGroup.add(task1);
			theGroup.getExecutor().execute();
		}
	};
	
	public final GutsAction _twoSerialGroupsDialogBlocker = new TasksGroupAction("twoSerialGroupsDialogBlocker")
	{
		@Override protected void perform()
		{
			TasksGroup group1 = newTasksGroup(
				"twoSerialGroupsDialogBlocker #1", true, InputBlockers.dialogBlocker());
			final TasksGroup group2 = newTasksGroup(
				"twoSerialGroupsDialogBlocker #2", true, InputBlockers.dialogBlocker());
			final Task<?> task2 = new LongTask("twoSerialGroupsDialogBlocker #2");
			Task<?> task1 = new LongTask("twoSerialGroupsDialogBlocker #1")
			{
				@Override public Void execute(FeedbackController controller) throws Exception
				{
					super.execute(controller);
					group2.add(task2).getExecutor().execute();
					return null;
				}
			};
			group1.add(task1).getExecutor().execute();
		}
	};
	
	static private class LongTask extends AbstractTask<Void>
	{
		LongTask(String name)
		{
			_name = name;
		}

		@Override public Void execute(FeedbackController controller) throws Exception
		{
			Long sleep = 5000L + (_randomizer.nextInt(10) * 500L);
			System.out.printf("Task `%s` starting execute() for %d ms\n", _name, sleep);
			Thread.sleep(sleep);
			System.out.printf("Task `%s` ending execute() after %d ms\n", _name, sleep);
			return null;
		}

		@Override public void finished(TasksGroup group, Task<? extends Void> source)
		{
			System.out.printf("Task `%s` finished\n", _name);
		}
		
		final private String _name;
	}

	static private class LongProgressTask extends AbstractTask<Void>
	{
		LongProgressTask(String name)
		{
			_name = name;
		}

		@Override public Void execute(FeedbackController controller) throws Exception
		{
			int totalSleepChunks = 10 + _randomizer.nextInt(10);
			Long sleep = 500L * totalSleepChunks;
			System.out.printf("Task `%s` starting execute() for %d ms\n", _name, sleep);
			for (int i = 0; i < totalSleepChunks; i++)
			{
				controller.setFeedback("Sleep chunk #" + (i + 1) + "...");
				Thread.sleep(500L);
				controller.setProgress((i + 1) * 100 / totalSleepChunks);
			}
			controller.setFeedback("Task done!");
			System.out.printf("Task `%s` ending execute() after %d ms\n", _name, sleep);
			return null;
		}

		@Override public void finished(TasksGroup group, Task<? extends Void> source)
		{
			System.out.printf("Task `%s` finished\n", _name);
		}
		
		final private String _name;
	}

	static final private Random _randomizer = new Random();
	final private ExecutorService _serialExecutor = Executors.newFixedThreadPool(1);
}
//CSON: MagicNumberCheck
//CSON: VisibilityModifierCheck
//CSON: LineLengthCheck
