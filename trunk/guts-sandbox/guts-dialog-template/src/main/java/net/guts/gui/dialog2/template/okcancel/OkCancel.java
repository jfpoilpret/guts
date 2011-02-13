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

package net.guts.gui.dialog2.template.okcancel;

import javax.swing.Action;
import javax.swing.RootPaneContainer;

import net.guts.gui.dialog2.template.TemplateDecorator;
import net.guts.gui.window.AbstractConfig;

public final class OkCancel extends AbstractConfig<RootPaneContainer, OkCancel>
{
	private OkCancel()
	{
		set(TemplateDecorator.TEMPLATE_TYPE_KEY, OkCancelDecorator.class);
		set(OkCancelConfig.class, _config);
	}
	
	static public OkCancel create()
	{
		return new OkCancel();
	}
	
	public OkCancel withOK(Action apply)
	{
		_config._apply = apply;
		_config._hasOK = true;
		return this;
	}
	
	public OkCancel withCancel(Action cancel)
	{
		_config._cancel =  cancel;
		_config._hasCancel = true;
		return this;
	}

	public OkCancel withCancel()
	{
		return withCancel(null);
	}

	public OkCancel withApply()
	{
		_config._hasApply = true;
		return this;
	}
	
	public OkCancel dontChangeView()
	{
		_config._dontChangeView = true;
		return this;
	}

	static public enum Result
	{
		OK,
		CANCEL
	}

	public Result result()
	{
		return _config._result;
	}
	
	final private OkCancelConfig _config = new OkCancelConfig();
}
