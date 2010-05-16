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

package net.guts.gui.session;

import java.awt.Component;
import java.awt.Container;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.type.TypeHelper;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class SessionManagerImpl implements SessionManager
{
	static final private Logger _logger = LoggerFactory.getLogger(SessionManagerImpl.class);

	@Inject
	SessionManagerImpl(SerializationManager serializer, StorageMedium medium, 
		Map<Class<?>, SessionState<?>> converters)
	{
		_serializer = serializer;
		_medium = medium;
		_converters = converters;
	}

	@Override public <T extends Component> void restore(T component)
	{
		restoreRecursive(component, component);
	}

	private <T extends Component> void restoreRecursive(Component parent, T component)
	{
		inject(parent, component);
		if (component instanceof Container)
		{
			Container container = (Container) component;
			for (Component child: container.getComponents())
			{
				restoreRecursive(parent, child);
			}
		}
	}

	@Override public <T extends Component> void save(T component)
	{
		saveRecursive(component, component);
	}

	private <T extends Component> void saveRecursive(Component parent, T component)
	{
		extract(parent, component);
		if (component instanceof Container)
		{
			Container container = (Container) component;
			for (Component child: container.getComponents())
			{
				saveRecursive(parent, child);
			}
		}
	}

	public void save(String id, Object content)
	{
		// Serialize object
		byte[] data = _serializer.serialize(content);
		// Save it somewhere
		_medium.save(id, data);
	}
	
	public void restore(String id, Object content)
	{
		// Restore saved content
		byte[] data = _medium.load(id);
		// Deserialize content into given object
		_serializer.deserialize(data, content);
	}

	@SuppressWarnings("unchecked") 
	private <T extends Component> SessionState<T> findState(T component)
	{
		return (SessionState<T>) TypeHelper.findBestMatchInTypeHierarchy(
			_converters, component.getClass());
	}
	
	private <T extends Component> void inject(Component parent, T component)
	{
		SessionState<T> state = findState(component);
		if (state != null)
		{
			state.reset();
			String id = name(parent, component, "restore");
			if (id != null)
			{
				restore(id, state);
				state.injectState(component);
			}
		}
	}
	
	private <T extends Component> void extract(Component parent, T component)
	{
		SessionState<T> state = findState(component);
		if (state != null)
		{
			state.extractState(component);
			String id = name(parent, component, "save");
			if (id != null)
			{
				save(id, state);
			}
		}
	}
	
	// GUTS-26: make names short so they can fit as a key (length < 80) in java.util.prefs
	private String name(Component parent, Component component, String action)
	{
		String name = component.getName();
		String parentName = parent.getName();
		if (name == null)
		{
			_logger.warn("cannot {} component state: missing name() for {}",
				new Object[]{action, component.getClass().getSimpleName()});
			return null;
		}
		if (parentName == null)
		{
			_logger.warn("cannot {} component state: missing name() for {}'s parent {}",
				new Object[]{action,
							 component.getClass().getSimpleName(),
							 parent.getClass().getSimpleName()});
			return null;
		}
		// FIXME what if returned name is still too long? truncate? log and return null?
		if (parent == component)
		{
			// This is the toplevel container, return only its name
			return name;
		}
		else
		{
			// Not the topelevel container, return (toplevel, component) names only
			return parentName + '.' + name;
		}
	}

	final private SerializationManager _serializer;
	final private StorageMedium _medium;
	final private Map<Class<?>, SessionState<?>> _converters;
}
