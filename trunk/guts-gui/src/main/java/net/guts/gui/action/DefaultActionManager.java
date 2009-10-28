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

package net.guts.gui.action;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;

import org.jdesktop.application.ApplicationContext;


import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Default implementation of {@link ActionManager} service.
 * <p/>
 * Normally, there is no need to override it or change it for another
 * implementation.
 * 
 * @author Jean-Francois Poilpret
 */
@Singleton
public class DefaultActionManager implements ActionManager
{
	@Inject public DefaultActionManager(ApplicationContext context)
	{
		_context = context;
//		addActionSource(application);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.action.ActionManager#getAction(java.lang.String)
	 */
	public Action getAction(String name)
	{
		for (Object source: _actionSources)
		{
			Action action = getAction(name, source);
			if (action != null)
			{
				return action;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.action.ActionManager#getAction(java.lang.String, java.lang.Object)
	 */
	public Action getAction(String name, Object source)
	{
		Class<?> clazz = (source instanceof JComponent ? JComponent.class : Object.class);
		return _context.getActionMap(clazz, source).get(name);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.action.ActionManager#addActionSource(java.lang.Object)
	 */
	public void addActionSource(Object source)
	{
		_actionSources.add(source);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.action.ActionManager#removeActionSource(java.lang.Object)
	 */
	public void removeActionSource(Object source)
	{
		_actionSources.remove(source);
	}

	final private List<Object> _actionSources = new ArrayList<Object>();
	final private ApplicationContext _context;
}
