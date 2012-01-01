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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

class ViewFactoryProvider implements Provider<ViewFactory>, ViewFactory
{
	//TODO Also inject the hook interface? or Event Channel?
	@Inject ViewFactoryProvider(ViewFactoryHolder viewFactory, ViewFactoryListener listener)
	{
		_viewFactory = viewFactory.get();
		_listener = listener;
	}

	@Override public ViewFactory get()
	{
		return this;
	}
	
	@Override public View createView(String id, JComponent content)
	{
		_logger.debug("createView({})", id);
		View view = _viewFactory.createView(id, content);
		// Call hook
		if (view != null)
		{
			_listener.viewCreated(view);
		}
		return view;
	}

	// Nested class to allow optional injection of custom ViewFactory
	static class ViewFactoryHolder
	{
		@Inject ViewFactoryHolder(Provider<DefaultViewFactory> defaultFactory)
		{
			_defaultFactory = defaultFactory;
		}
		
		ViewFactory get()
		{
			if (_factory != null)
			{
				return _factory;
			}
			else
			{
				return _defaultFactory.get();
			}
		}
		
		@Inject(optional = true) void set(@BindCustomViewFactory ViewFactory factory)
		{
			_factory = factory;
		}
		
		final private Provider<DefaultViewFactory> _defaultFactory;
		private ViewFactory _factory = null;
	}
	
	static final private Logger _logger = LoggerFactory.getLogger(ViewFactoryProvider.class);
	
	final private ViewFactory _viewFactory;
	final private ViewFactoryListener _listener;
}
