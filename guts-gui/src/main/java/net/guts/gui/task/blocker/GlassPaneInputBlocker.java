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

import java.awt.Component;

import javax.swing.JRootPane;

import net.guts.gui.task.TasksGroup;

import com.google.inject.Inject;

public class GlassPaneInputBlocker implements InputBlocker
{
	public GlassPaneInputBlocker(JRootPane root)
	{
		_root = root;
	}

	@Inject void init(DisabledGlassPane glassPane)
	{
		_blockingGlassPane = glassPane;
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.action2.blocker.InputBlocker#block(net.guts.gui.action2.task.TasksSubmission)
	 */
	@Override public void block(TasksGroup tasks)
	{
		_savedGlassPane = _root.getGlassPane();
		_root.setGlassPane(_blockingGlassPane);
		_blockingGlassPane.activate();
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.action2.blocker.InputBlocker#unblock(net.guts.gui.action2.task.TasksSubmission)
	 */
	@Override public void unblock(TasksGroup  tasks)
	{
		_blockingGlassPane.deactivate();
		_root.setGlassPane(_savedGlassPane);
	}

	final private JRootPane _root;
	private DisabledGlassPane _blockingGlassPane;
	private Component _savedGlassPane;
}
