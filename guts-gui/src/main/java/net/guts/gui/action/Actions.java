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

package net.guts.gui.action;

import java.util.concurrent.ExecutorService;

import com.google.inject.Binder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;

final public class Actions
{
	private Actions()
	{
	}
	
	static final String DEFAULT_TASK_SERVICE = "DEFAULT";
	
	static public LinkedBindingBuilder<ExecutorService> bindDefaultTaskExecutorService(
		Binder binder)
	{
		return binder.bind(ExecutorService.class).annotatedWith(BindTaskServiceExecutor.class);
	}
	
	static public LinkedBindingBuilder<TaskService> bindTaskService(Binder binder, String name)
	{
		MapBinder<String, TaskService> services = 
				MapBinder.newMapBinder(binder, String.class, TaskService.class);
		return services.addBinding(name);
	}
}
