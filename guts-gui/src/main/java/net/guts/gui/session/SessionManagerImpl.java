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

import net.guts.common.type.TypeHelper;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class SessionManagerImpl implements SessionManager
{
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
		inject(component);
		if (component instanceof Container)
		{
			Container container = (Container) component;
			for (Component child: container.getComponents())
			{
				restore(child);
			}
		}
	}

	@Override public <T extends Component> void save(T component)
	{
		extract(component);
		if (component instanceof Container)
		{
			Container container = (Container) component;
			for (Component child: container.getComponents())
			{
				save(child);
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
	
	private <T extends Component> void inject(T component)
	{
		SessionState<T> state = findState(component);
		if (state != null)
		{
			state.reset();
			restore(name(component), state);
			state.injectState(component);
		}
	}
	
	private <T extends Component> void extract(T component)
	{
		SessionState<T> state = findState(component);
		if (state != null)
		{
			state.extractState(component);
			save(name(component), state);
		}
	}
	
	private String name(Component component)
	{
		String name = component.getName();
		if (name != null)
		{
			return name;
		}
		else if (component.getParent() != null)
		{
			return name(component.getParent()) + "." + component.getClass().getSimpleName();
		}
		else
		{
			return "noname";
		}
	}

	final private SerializationManager _serializer;
	final private StorageMedium _medium;
	final private Map<Class<?>, SessionState<?>> _converters;
}
