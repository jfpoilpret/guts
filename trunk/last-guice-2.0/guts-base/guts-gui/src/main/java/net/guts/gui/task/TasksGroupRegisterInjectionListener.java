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

package net.guts.gui.task;

import net.guts.common.injection.AbstractInjectionListener;

import com.google.inject.Inject;

class TasksGroupRegisterInjectionListener extends AbstractInjectionListener<TasksGroup>
{
	@Inject void setTasksGroupRegistry(TasksGroupRegistry registry)
	{
		_registry = registry;
	}

	@Override protected void registerInjectee(TasksGroup injectee)
	{
		_registry.registerTasksGroup(injectee);
	}

	private TasksGroupRegistry _registry;
}
