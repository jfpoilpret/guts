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

package net.guts.gui.resource;

import java.awt.Component;
import java.awt.Container;
import java.util.Locale;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.type.TypeHelper;
import net.guts.event.Channel;
import net.guts.gui.resource.InjectionDecisionStrategy.InjectionDecision;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class ResourceInjectorImpl implements ResourceInjector
{
	static final private Logger _logger = LoggerFactory.getLogger(ResourceInjectorImpl.class);

	@Inject	
	public ResourceInjectorImpl(Channel<Locale> localeChanges, InjectionDecisionStrategy hooks,
		ResourceMapFactory registry, Map<Class<?>, InstanceInjector<?>> injectors)
	{
		_localeChanges = localeChanges;
		_hooks = hooks;
		_registry = registry;
		_injectors = injectors;
	}

	@Override public void injectComponent(Component component)
	{
		if (component != null)
		{
			InjectionDecision decision = _hooks.needsInjection(component, _locale);
			switch (decision)
			{
				case INJECT_COMPONENT_ONLY:
				case INJECT_HIERARCHY:
				injectComponent(component, false);
				_hooks.injectionPerformed(component, _locale);
				break;

				case DONT_INJECT:
				// Do nothing
				break;
				
				default:
				_logger.warn("injectComponent() unsupported InjectionDecision enum value: {}",
					decision);
				break;
			}
		}
	}

	@Override public void injectHierarchy(Component component)
	{
		if (component != null)
		{
			long time = System.nanoTime();
			InjectionDecision decision = _hooks.needsInjection(component, _locale);
			switch (decision)
			{
				case INJECT_COMPONENT_ONLY:
				injectComponent(component, false);
				_hooks.injectionPerformed(component, _locale);
				break;
					
				case INJECT_HIERARCHY:
				injectComponent(component, true);
				_hooks.injectionPerformed(component, _locale);
				break;
				
				case DONT_INJECT:
				// Do nothing
				break;

				default:
				_logger.warn("injectHierarchy() unsupported InjectionDecision enum value: {}",
					decision);
				break;
			}
			time = System.nanoTime() - time;
			_logger.debug("injectHierarchy {} in {} ms", 
				component.getName(), time / ONE_MS_IN_NS);
		}
	}
	
	private void injectComponent(Component component, boolean recursive)
	{
		// First inject component itself
		String prefix = prefix(component);
		if (prefix != null)
		{
			// Find the most specific ComponentInjector for component
			Class<? extends Component> type = component.getClass();
			InstanceInjector<Component> injector = findComponentInjector(type);
			ResourceMap resources = _registry.createResourceMap(type);
			injector.inject(component, prefix, resources);
		}
		if (recursive && component instanceof Container)
		{
			// Then recursively inject all its children
			for (Component child: ((Container) component).getComponents())
			{
				injectComponent(child, true);
			}
		}
		// Fix for GUTS-58 issue (with JMenu children which are NOT in getComponents())
		if (recursive && component instanceof JMenu)
		{
			JMenu menu = (JMenu) component;
			for (int i = 0; i < menu.getItemCount(); i++)
			{
				injectComponent(menu.getItem(i), true);
			}
		}
	}

	@Override public <T> void injectInstance(T instance)
	{
		if (instance != null)
		{
			injectInstance(instance, instance.getClass().getSimpleName());
		}
	}
	
	@Override public <T> void injectInstance(T instance, String name)
	{
		if (instance != null)
		{
			InjectionDecision decision = _hooks.needsInjection(instance, _locale);
			switch (decision)
			{
				case INJECT_COMPONENT_ONLY:
				case INJECT_HIERARCHY:
				// Find the most specific ComponentInjector for component
				Class<? extends T> type = getClass(instance);
				InstanceInjector<T> injector = findComponentInjector(type);
				ResourceMap resources = _registry.createResourceMap(type);
				injector.inject(instance, name, resources);
				_hooks.injectionPerformed(instance, _locale);
				break;

				case DONT_INJECT:
				// Do nothing
				break;

				default:
				_logger.warn("injectInstance() unsupported InjectionDecision enum value: {}",
					decision);
				break;
			}
		}
	}

	@Override public void setLocale(Locale locale)
	{
		if (locale != null && !locale.equals(_locale))
		{
			_locale = locale;
			Locale.setDefault(_locale);
			// Trigger Locale event, consumed by:
			// - ResourceMapFactoryImpl to update Bundles
			// - WindowControllerImpl to force resource injections on all visible windows
			_localeChanges.publish(_locale);
		}
	}

	@SuppressWarnings("unchecked") 
	private <T> Class<? extends T> getClass(T instance)
	{
		return (Class<? extends T>) instance.getClass();
	}
	
	@SuppressWarnings("unchecked") 
	private <T> InstanceInjector<T> findComponentInjector(Class<? extends T> type)
	{
		InstanceInjector<?> injector = 
			TypeHelper.findBestMatchInTypeHierarchy(_injectors, type);
		return (InstanceInjector<T>) injector;
	}
	
	private String prefix(Component component)
	{
		String prefix = component.getName();
		if (prefix == null)
		{
			_logger.debug("Component has no name: {}", component.getClass().getSimpleName());
		}
		return prefix;
	}

	static final private long ONE_MS_IN_NS = 1000000;
		
	final private Channel<Locale> _localeChanges;
	final private InjectionDecisionStrategy _hooks;
	final private ResourceMapFactory _registry;
	final private Map<Class<?>, InstanceInjector<?>> _injectors;
	private Locale _locale = Locale.getDefault();
}
