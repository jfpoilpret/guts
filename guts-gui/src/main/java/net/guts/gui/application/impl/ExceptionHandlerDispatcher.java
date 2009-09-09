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

package net.guts.gui.application.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import net.guts.gui.application.ExceptionHandler;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Default implementation of {@link ExceptionHandlerManager} service.
 * <p/>
 * Generally, there is no need to override it or change it for another
 * implementation.
 * 
 * @author Jean-Francois Poilpret
 */
@Singleton
public class ExceptionHandlerDispatcher implements ExceptionHandler
{
	//TODO Make it package-private?
	@Inject
	public ExceptionHandlerDispatcher(Set<ExceptionHandler> handlers)
	{
		_handlers = handlers;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.application.ExceptionHandler#handle(java.lang.Throwable)
	 */
	public boolean handle(Throwable e)
	{
		// Workaround Swing AppFW Issue #42
		if (e instanceof Error && e.getCause() != null)
		{
			// Work-around for Issue #72
			Throwable cause = e.getCause();
			if (cause instanceof InvocationTargetException)
			{
				return dispatch(((InvocationTargetException) cause).getTargetException());
			}
			else
			{
				return dispatch(cause);
			}
		}
		else
		{
			return dispatch(e);
		}
	}
	
	private boolean dispatch(Throwable e)
	{
		// Search for a handler which can handle the caught exception
		for (ExceptionHandler handler: _handlers)
		{
			if (handler.handle(e))
			{
				return true;
			}
		}
		return false;
	}

	final private Set<ExceptionHandler> _handlers;
}
