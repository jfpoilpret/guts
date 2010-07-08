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

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.flexdock.view.View;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class EmptyViewsRegistry
{
	@Inject EmptyViewsRegistry(EmptyableViewportPolicy viewportPolicy, ViewFactory viewFactory)
	{
		_viewportPolicy = viewportPolicy;
		_viewFactory = viewFactory;
	}
	
	public View getEmptyView(String idView)
	{
		if (!_viewportPolicy.isEmptyView(idView))
		{
			return null;
		}
		View view = _emptyViews.get(idView);
		if (view == null)
		{
			JPanel empty = new JPanel();
			empty.setName(EMPTY_VIEW_PANEL);
			view = _viewFactory.createView(idView, empty);
			_emptyViews.put(idView, view);
		}
		return view;
	}
	
	public View getEmptyView(GutsViewport port)
	{
		return getEmptyView(port.getEmptyViewId());
	}

	public boolean isEmptyView(View view)
	{
		return		view.getContentPane() != null
				&&	EMPTY_VIEW_PANEL.equals(view.getContentPane().getName());
	}
	
	private static final String EMPTY_VIEW_PANEL = "guts-flexdock-empty-view";

	private final EmptyableViewportPolicy _viewportPolicy;
	private final ViewFactory _viewFactory;
	private final Map<String, View> _emptyViews = new HashMap<String, View>();
}
