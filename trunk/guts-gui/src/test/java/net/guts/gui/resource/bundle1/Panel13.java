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

@UsesBundles({"resources", "/net/guts/gui/resource/bundle2/resources"})
public class Panel13 extends AbstractPanel
{
	private static final String NAME = "bundle1-panel13";

	public Panel13()
	{
		setName(NAME);
	}
}
