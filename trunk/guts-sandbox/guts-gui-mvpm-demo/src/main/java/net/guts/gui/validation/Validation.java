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

import javax.swing.RootPaneContainer;

import net.guts.gui.window.AbstractConfig;

import com.jgoodies.validation.Validatable;
import com.jgoodies.validation.ValidationResultModel;

/**
 * Configuration object to decorate a view ({@link javax.swing.JComponent}) with
 * a transparent pane that can show indicator icons and tooltips based on validation
 * results of the pane. This object must then be 
 * {@link AbstractConfig#merge(AbstractConfig) merged} to one of
 * {@link net.guts.gui.window.JDialogConfig}, {@link net.guts.gui.window.JFrameConfig},
 * {@link net.guts.gui.window.JInternalFrameConfig} or 
 * {@link net.guts.gui.window.JAppletConfig} before passing the latter to
 * {@link net.guts.gui.window.WindowController#show}.
 * <p/>
 * {@code Validation} uses a fluent API to set configuration on how exactly to 
 * decorate a view.
 * <p/>
 * Check <a href="net/guts/gui/validation/package-summary.html">package
 * documentation</a> for a code example.
 * 
 * @author Jean-Francois Poilpret
 */
public final class Validation extends AbstractConfig<RootPaneContainer, Validation>
{
	private Validation()
	{
		set(ValidationConfig.class, _config);
	}
	
	/**
	 * Create a new {@code Validation} default configuration object. Default configuration
	 * won't perform any automatic validation and will provide a default
	 * {@link com.jgoodies.validation.ValidationResultModel}.
	 * 
	 * @return a new {@code Validation} configuration object
	 */
	static public Validation create()
	{
		return new Validation();
	}

	//TODO new methods to indicate when/how validation should occur?
	
	/**
	 * Specify the {@link ValidationResultModel} to be used by the Validation
	 * {@link net.guts.gui.window.WindowProcessor}. If not explicitly provided by
	 * call this method, a default one is automatically created and can be obtained
	 * by calling {@link #getModel()}.
	 * 
	 * @param model the {@link com.jgoodies.validation.ValidationResultModel} to be used
	 * by the transparent panel to find out which components of the original view
	 * should display warning or error icons; if {@code null}, the previous value is
	 * kept.
	 * @return {@code this} builder so that calls can be chained until {@link #config()}
	 */
	public Validation withModel(ValidationResultModel model)
	{
		if (model != null)
		{
			_config._model = model;
		}
		return this;
	}
	
	public Validation withValidator(Validatable validator)
	{
		_config._validator = validator;
		return this;
	}
	
	public Validation withValidationWhenDisplayed()
	{
		_config._validateAtFirstDiplay = true;
		return this;
	}

	public Validation withAutoFocus()
	{
		_config._autoFocus = true;
		return this;
	}
	
	public ValidationResultModel getModel()
	{
		return _config._model;
	}

	final private ValidationConfig _config = new ValidationConfig();
}
