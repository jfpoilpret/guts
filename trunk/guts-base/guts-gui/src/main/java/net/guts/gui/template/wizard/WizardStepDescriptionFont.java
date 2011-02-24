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

package net.guts.gui.template.wizard;

import java.awt.Font;

import net.guts.gui.resource.ResourceInjector;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class WizardStepDescriptionFont
{
	@Inject WizardStepDescriptionFont(ResourceInjector injector)
	{
		injector.injectInstance(this);
	}
	
	Font getFont()
	{
		return _font;
	}
	
	void setFont(Font font)
	{
		_font = font;
	}
	
	private Font _font;
}
