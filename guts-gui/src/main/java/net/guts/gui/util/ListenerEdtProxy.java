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

package net.guts.gui.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.TypeLiteral;

/**
 * This class proxifies any listener of any class (whatever its type {@code T}) 
 * so that any call to any of the proxy (as returned by {@link #notifier()} will
 * call the matching method of {@code listener} from the EDT.
 * <p/>
 * Calls are performed through {@link SwingUtilities#invokeAndWait(Runnable)}.
 * 
 * @param <T> the type to proxify
 *
 * @author Jean-Francois Poilpret
 */
public final class ListenerEdtProxy<T>
{
	static final private Logger _logger = LoggerFactory.getLogger(ListenerEdtProxy.class);

	/**
	 * Creates a new listener proxy. In order to use {@code this}, call 
	 * {@link #notifier()} and call any of its methods.
	 * 
	 * @param <T> the type to proxify
	 * @param type the type to proxify
	 * @param listener the instance to proxify
	 * @return a new proxy holder
	 */
	static public <T> ListenerEdtProxy<T> createProxy(Class<T> type, T listener)
	{
		return new ListenerEdtProxy<T>(TypeLiteral.get(type), listener);
	}
	
	/**
	 * Creates a new listener proxy. In order to use {@code this}, call 
	 * {@link #notifier()} and call any of its methods.
	 * 
	 * @param <T> the type to proxify
	 * @param type the type to proxify
	 * @param listener the instance to proxify
	 * @return a new proxy holder
	 */
	static public <T> ListenerEdtProxy<T> createProxy(TypeLiteral<T> type, T listener)
	{
		return new ListenerEdtProxy<T>(type, listener);
	}
	
	@SuppressWarnings("unchecked") 
	private ListenerEdtProxy(TypeLiteral<T> type, T listener)
	{
		Class<?> clazz = type.getRawType();
		_proxy = (T) Proxy.newProxyInstance(
			clazz.getClassLoader(), new Class[]{clazz}, new Handler());
		_listener = listener;
	}

	/**
	 * Returns the actual proxy that will wrap {@code listener} (as passed to
	 * {@link #createProxy(Class, Object)} or 
	 * {@link #createProxy(TypeLiteral, Object)}); any call to the returned
	 * proxy will be mapped to the same call to {@code listener} but will make
	 * sure this call is performed inside Swing EDT (Event Dispatch Thread).
	 * 
	 * @return a new proxy to {@code listener}
	 */
	public T notifier()
	{
		return _proxy;
	}

	//CSOFF: IllegalThrowsCheck
	private void invoke(final Method method, final Object[] args) throws Throwable
	{
		if (SwingUtilities.isEventDispatchThread())
		{
			method.invoke(_listener, args);
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override public void run()
				{
					try
					{
						method.invoke(_listener, args);
					}
					catch (IllegalAccessException e)
					{
						_logger.error("Unexpected exception in EDT", e);
					}
					catch (InvocationTargetException e)
					{
						_logger.error("Listener has thrown exception in EDT", e.getCause());
					}
				}
			});
		}
	}
	//CSON: IllegalThrowsCheck

	private class Handler implements InvocationHandler
	{
		//CSOFF: IllegalThrowsCheck
		@Override public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable
		{
			// Check that method belongs to T interface
			ListenerEdtProxy.this.invoke(method, args);
			return null;
		}
		//CSON: IllegalThrowsCheck
	}

	final private T _proxy;
	final private T _listener;
}
