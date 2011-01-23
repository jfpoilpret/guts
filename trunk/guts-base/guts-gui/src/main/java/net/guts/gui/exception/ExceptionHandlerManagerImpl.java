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
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.cleaner.Cleanable;
import net.guts.common.cleaner.Cleaner;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class ExceptionHandlerManagerImpl implements ExceptionHandlerManager
{
	static final private Logger _logger = 
		LoggerFactory.getLogger(ExceptionHandlerManagerImpl.class);
	
	@Inject
	public ExceptionHandlerManagerImpl(AnnotationProcessor processor, Cleaner cleaner)
	{
		_processor = processor;
		cleaner.addCleanable(new Cleanable()
		{
			@Override public void cleanup()
			{
				ExceptionHandlerManagerImpl.this.cleanup();
			}
		});
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.exception.ExceptionHandlerManager#handleException(java.lang.Throwable)
	 */
	@Override public void handleException(Throwable e)
	{
		dispatch(e);
	}
	
	private void dispatch(Throwable e)
	{
		Class<? extends Throwable> clazz = e.getClass();
		synchronized (_handlers)
		{
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
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.exception.ExceptionHandlerManager#registerExceptionHandlers(java.lang.Object)
	 */
	@Override public void registerExceptionHandlers(Object instance)
	{
		List<ExceptionHandler> handlers = _processor.process(instance.getClass());
		synchronized (_handlers)
		{
			for (ExceptionHandler handler: handlers)
			{
				_handlers.add(new Handler(instance, handler));
			}
		}
	}

	//FIXME this currently doesn't work with private methods...
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
	
	private void cleanup()
	{
		synchronized (_handlers)
		{
			Iterator<Handler> i = _handlers.iterator();
			while (i.hasNext())
			{
				if (i.next()._instance.get() == null)
				{
					i.remove();
				}
			}
		}
	}

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
