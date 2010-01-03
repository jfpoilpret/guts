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

package net.guts.gui.action.blocker;

import com.google.inject.AbstractModule;

public final class BlockerModule extends AbstractModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		// Bind all InputBlockers provided by guts-gui
		Blockers.bindInputBlocker(
			binder(), ComponentInputBlocker.class, ComponentBlocker.class);
		Blockers.bindInputBlocker(
			binder(), ActionInputBlocker.class, ActionBlocker.class);
		Blockers.bindInputBlocker(
			binder(), GlassPaneInputBlocker.class, GlassPaneBlocker.class);
		Blockers.bindInputBlocker(
			binder(), ModalDialogInputBlocker.class, ModalDialogBlocker.class);
	}

	@Override public boolean equals(Object other)
	{
		return other instanceof BlockerModule;
	}

	@Override public int hashCode()
	{
		return BlockerModule.class.hashCode();
	}
}
