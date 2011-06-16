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

package net.guts.gui.validation;

import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.RootPaneContainer;

import net.guts.gui.window.AbstractWindowProcessor;
import net.guts.gui.window.RootPaneConfig;

import com.jgoodies.validation.Validatable;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;

class WpWindowValidation<V extends RootPaneContainer> 
extends AbstractWindowProcessor<Window, V>
{
	WpWindowValidation()
	{
		super(Window.class);
	}

	@Override protected void processRoot(Window root, RootPaneConfig<V> config)
	{
		ValidationConfig validationConfig = config.get(ValidationConfig.class);
		if (validationConfig != null)
		{
			ValidationResultModel validation = validationConfig._model;
			
			// Require validation when possible
			Validatable validator = validationConfig._validator;
			if (validator != null)
			{
				ValidationResult result = validator.validate();
				if (result != null)
				{
					validation.setResult(result);
				}
			}
			RootPaneContainer container = (RootPaneContainer) root;
			JComponent view = (JComponent) container.getContentPane();
			JComponent feedback = new IconFeedbackPanel(validation, view);
			container.setContentPane(feedback);
		}
	}
}
