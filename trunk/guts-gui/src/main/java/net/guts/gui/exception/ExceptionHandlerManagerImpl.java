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

package net.guts.gui.exception;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

//TODO add locks!
//TODO add cleaning!
@Singleton
class ExceptionHandlerManagerImpl implements ExceptionHandlerManager
{
	static final private Logger _logger = 
		LoggerFactory.getLogger(ExceptionHandlerManagerImpl.class);
	
	@Inject
	public ExceptionHandlerManagerImpl(AnnotationProcessor processor)
	{
		_processor = processor;
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.exception.ExceptionHandlerManager#handleException(java.lang.Throwable)
	 */
	@Override public void handleException(Throwable e)
	{
		//TODO handle special cases of SAF (until it is completely removed from dependencies)

		Class<? extends Throwable> clazz = e.getClass();
		for (Handler handler: _handlers)
		{
			// Check if reference is still valid
			Object instance = handler._instance.get();
			if (instance != null)
			{
				// Check method arg type is compatible with e
				if (handler._info.getType().isAssignableFrom(clazz))
				{
					// Call the handler and check if it has handled this exception
					if (handle(instance, handler._info.getHandler(), e))
					{
						break;
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.exception.ExceptionHandlerManager#registerExceptionHandlers(java.lang.Object)
	 */
	@Override public void registerExceptionHandlers(Object instance)
	{
		List<ExceptionHandler> handlers = _processor.process(instance.getClass());
		for (ExceptionHandler handler: handlers)
		{
			_handlers.add(new Handler(instance, handler));
		}
	}
	
	// CSOFF: IllegalCatchCheck
	private boolean handle(Object instance, Method method, Throwable e)
	{
		try
		{
			return (Boolean) method.invoke(instance, e);
		}
		catch (Exception exc)
		{
			String msg = String.format("Failed to invoke `%1$s` on `%2$s` instance.", 
				method.getName(), instance);
			_logger.info(msg, e);
			return false;
		}
	}
	// CSON: IllegalCatchCheck
	
	static private class Handler implements Comparable<Handler>
	{
		Handler(Object instance, ExceptionHandler info)
		{
			_instance = new WeakReference<Object>(instance);
			_info = info;
		}
		
		public int compareTo(Handler that)
		{
			int diff = this._info.getPriority() - that._info.getPriority();
			return (diff != 0 ? diff : this._order - that._order);
		}
		
		@Override public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + _order;
			result = prime * result + _info.getPriority();
			return result;
		}

		@Override public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			Handler other = (Handler) obj;
			return compareTo(other) == 0;
		}
		
		final private WeakReference<Object> _instance;
		final private ExceptionHandler _info;
		final private int _order = _total++;
		static private int _total = 0; 
	}

	final private AnnotationProcessor _processor;
	final private SortedSet<Handler> _handlers = new TreeSet<Handler>();
}
