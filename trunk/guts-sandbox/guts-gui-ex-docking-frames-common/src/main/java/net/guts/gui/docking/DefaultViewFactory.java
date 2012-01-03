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

package net.guts.gui.docking;

import javax.swing.JComponent;

import net.guts.gui.resource.ResourceInjector;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.intern.CDockable;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
class DefaultViewFactory implements ViewFactory
{
	@Inject DefaultViewFactory(
		DockableFactoryHolder dockableFactory, ViewContentFactory contentFactory, 
		ResourceInjector injector, CControl controller)
	{
		_dockableFactory = dockableFactory.get();
		_contentFactory = contentFactory;
		_injector = injector;
		_controller = controller;
	}

	@Override public SingleCDockable createSingle(String id)
	{
		return createSingle(id, _contentFactory.create(id));
	}
	
	@Override public SingleCDockable createSingle(String id, JComponent content)
	{
		if (content == null)
		{
			return null;
		}
		SingleCDockable view = _dockableFactory.createSingle(id, content);
		initView(view, content, id);
		return view;
	}
	
	@Override public MultipleCDockable createMulti(
		String idFactory, String id, JComponent content)
	{
		MultipleCDockableFactory<?, ?> factory = 
			_controller.getMultipleDockableFactory(idFactory);
		if (factory != null && content != null)
		{
			MultipleCDockable view = 
				_dockableFactory.createMulti(idFactory, factory, id, content);
			_controller.addDockable(id, view);
			initView(view, content, id);
			return view;
		}
		else
		{
			return null;
		}
	}
	
	@Override public MultipleCDockable createMulti(String idFactory, String id)
	{
		return createMulti(idFactory, id, _contentFactory.create(id));
	}

	private void initView(CDockable view, JComponent content, String id)
	{
		// Inject the content itself
		_injector.injectHierarchy(content);
		// Inject the dockable now (titleText, titleIcon...)
		_injector.injectInstance(view, id);
	}

	static class DockableFactoryHolder
	{
		@Inject DockableFactoryHolder(Provider<DefaultDockableFactory> defaultFactory)
		{
			_defaultFactory = defaultFactory;
		}
		
		DockableFactory get()
		{
			return (_factory != null ? _factory : _defaultFactory.get());
		}
		
		@Inject(optional = true) void set(@BindDockableFactory DockableFactory factory)
		{
			_factory = factory;
		}

		final private Provider<DefaultDockableFactory> _defaultFactory;
		private DockableFactory _factory;
	}

	final private DockableFactory _dockableFactory;
	final private ViewContentFactory _contentFactory;
	final private ResourceInjector _injector;
	final private CControl _controller;
}
