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

package net.guts.gui.exit;

import net.guts.common.injection.AbstractInjectionListener;

import com.google.inject.Inject;

class ExitCheckerInjectionListener extends AbstractInjectionListener<ExitChecker>
{
	// Guice Injector is injected as a "trick" in order to delay the call to this method
	// as late as possible during Guice.createInjector()
	@Inject void setExitController(ExitController controller)
	{
		_controller = controller;
	}

	@Override protected void registerInjectee(ExitChecker injectee)
	{
		_controller.registerExitChecker(injectee);
	}
	
	private ExitController _controller = null;
}
