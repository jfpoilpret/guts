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

package net.guts.gui.window;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.RootPaneContainer;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class WindowControllerImpl implements WindowController
{
	@Inject WindowControllerImpl(Map<Integer, WindowProcessor> processors)
	{
		// Sort all processors according to order index key
		TreeMap<Integer, WindowProcessor> sortedProcessors = 
			new TreeMap<Integer, WindowProcessor>(processors);
		// Then copy to a sorted list of WPs (no need to store index)
		_processors = new ArrayList<WindowProcessor>(sortedProcessors.values());
	}

	@Override public <T extends RootPaneContainer> void show(T root, RootPaneConfig<T> config)
	{
		if (!EventQueue.isDispatchThread())
		{
			throw new IllegalStateException(
				"WindowController.show() must be called from the EDT!");
		}
		// Delegate all work to all registered WindowProcessors
		for (WindowProcessor processor: _processors)
		{
			processor.process(root, config);
		}
	}

	final private List<WindowProcessor> _processors;
}
