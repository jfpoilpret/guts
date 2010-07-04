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

package net.guts.gui.application.docking;

import javax.swing.JComponent;

import org.flexdock.view.View;

import net.guts.gui.resource.ResourceInjector;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultViewFactory implements ViewFactory
{
	@Inject void init(ResourceInjector injector)
	{
		_injector = injector;
	}
	
	@Override public View createView(String id, JComponent content)
	{
		if (content == null)
		{
			return null;
		}
		View view = createView(id);
		view.setName(id);
		view.setContentPane(content);
		initView(view, id);
		_injector.injectHierarchy(view);
		return view;
	}
	
	final protected View createView(String id)
	{
		return new View(id);
	}

	//TODO find some better way to configure which actions should be added to the view,
	// rather than requiring subclassing?
	protected void initView(View view, String id)
	{
		if (EmptyableViewport.EMPTY_VIEW_ID.equals(id))
		{
			view.setTitlebar(null);
		}
		else
		{
			view.addAction(View.CLOSE_ACTION);
		}
	}

	private ResourceInjector _injector;
}
