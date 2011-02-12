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

package net.guts.gui.dialog2.template.wizard;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class WizardFactory
{	
	@Inject WizardFactory(Injector injector)
	{
		_injector = injector;
	}
	
	public Builder builder()
	{
		return new Builder();
	}
	
	final public class Builder
	{
		Builder()
		{
			_mainView = new JPanel();
			_controller = new WizardController(_mainView);
		}
		
		public Builder mapNextStep(JComponent view)
		{
			_controller.addStep(view.getName(), view, true);
			return this;
		}
		
		public Builder mapOneStep(JComponent view)
		{
			_controller.addStep(view.getName(), view, false);
			return this;
		}
		
		public Builder mapNextStep(Class<? extends JComponent> view)
		{
			return mapNextStep(_injector.getInstance(view));
		}
		
		public Builder mapOneStep(Class<? extends JComponent> view)
		{
			return mapOneStep(_injector.getInstance(view));
		}
		
		public JComponent mainView()
		{
			return _mainView;
		}
		
		public WizardController controller()
		{
			return _controller;
		}
		
		final private JComponent _mainView;
		final private WizardController _controller;
	}
	
	final private Injector _injector;
}
