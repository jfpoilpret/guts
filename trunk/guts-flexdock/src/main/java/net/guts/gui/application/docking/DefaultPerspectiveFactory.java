//  Copyright 2004-2007 Jean-Francois Poilpret
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

package net.guts.gui.application.docking;

import java.util.Map;

import org.flexdock.perspective.LayoutSequence;
import org.flexdock.perspective.Perspective;
import org.flexdock.perspective.PerspectiveFactory;

import com.google.inject.Inject;

class DefaultPerspectiveFactory implements PerspectiveFactory
{
	@Inject DefaultPerspectiveFactory(Map<String, PerspectiveInitializer> perspectives)
	{
		_perspectives = perspectives;
	}
	
	public Perspective getPerspective(String persistentId)
	{
		PerspectiveInitializer initializer = _perspectives.get(persistentId);
		if (initializer == null)
		{
			return null;
		}
		Perspective perspective = new Perspective(persistentId, initializer.getDescription());
		LayoutSequence seq = perspective.getInitialSequence(true);
		initializer.initLayout(persistentId, seq);
		return perspective;
	}

	private final Map<String, PerspectiveInitializer> _perspectives;
}
