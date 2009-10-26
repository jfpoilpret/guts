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

package net.guts.gui.resource.bundle1;

import net.guts.gui.resource.AbstractPanel;
import net.guts.gui.resource.UsesBundles;
import net.guts.gui.resource.bundle2.Panel21;

@UsesBundles({Panel14.class, Panel21.class})
public class Panel14 extends AbstractPanel
{
	private static final String NAME = "bundle1-panel14";

	public Panel14()
	{
		setName(NAME);
	}
}
