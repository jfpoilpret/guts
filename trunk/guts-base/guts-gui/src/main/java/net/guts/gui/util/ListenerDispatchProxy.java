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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.inject.TypeLiteral;

/**
 * This class proxifies any listener of any class (whatever its type {@code T}) 
 * so that any call to any of the proxy (as returned by {@link #notifier()} will
 * be dispatched to all listeners of the same type, registered with 
 * {@link #addListener(Object)}.
 * <p/>
 * 
 * @param <T> the type to proxify
 *
 * @author Jean-Francois Poilpret
 */
public final class ListenerDispatchProxy<T>
{
	/**
	 * Creates a new listener proxy. In order to use {@code this}, call 
	 * {@link #notifier()} and call any of its methods.
	 * 
	 * @param <T> the type to proxify
	 * @param type the type to proxify
	 * @return a new proxy holder
	 */
	static public <T> ListenerDispatchProxy<T> createProxy(Class<T> type)
	{
		return new ListenerDispatchProxy<T>(TypeLiteral.get(type));
	}
	
	/**
	 * Creates a new listener proxy. In order to use {@code this}, call 
	 * {@link #notifier()} and call any of its methods.
	 * 
	 * @param <T> the type to proxify
	 * @param type the type to proxify
	 * @return a new proxy holder
	 */
	static public <T> ListenerDispatchProxy<T> createProxy(TypeLiteral<T> type)
	{
		return new ListenerDispatchProxy<T>(type);
	}
	
	@SuppressWarnings("unchecked") 
	private ListenerDispatchProxy(TypeLiteral<T> type)
	{
		Class<?> clazz = type.getRawType();
		_proxy = (T) Proxy.newProxyInstance(
			clazz.getClassLoader(), new Class[]{clazz}, new Handler());
	}
	
	/**
	 * Returns the actual proxy that will dispatch all method calls to all listeners 
	 * registered with {@code this} proxy through {@link #addListener(Object)}.
	 * 
	 * @return the proxy that will dispatch all calls to registered listeners
	 */
	public T notifier()
	{
		return _proxy;
	}

	/**
	 * Adds {@code listener} to the list of listeners to which calls to 
	 * {@link #notifier()} will have to be dispatched.
	 * 
	 * @param listener the listener to be added to the list of dispatched listeners
	 */
	public void addListener(T listener)
	{
		if (listener != null)
		{
			_delegates.add(listener);
		}
	}
	
	/**
	 * Removes {@code listener} from the list of listeners to which calls to 
	 * {@link #notifier()} have to be dispatched.
	 * Does nothing if {@code listener} was not previously in the list.
	 * 
	 * @param listener the listener to be removed to the list of dispatched listeners
	 */
	public void removeListener(T listener)
	{
		if (listener != null)
		{
			_delegates.remove(listener);
		}
	}

	//CSOFF: IllegalThrowsCheck
	private void invoke(Method method, Object[] args) throws Throwable
	{
		try
		{
			for (T listener: _delegates)
			{
				method.invoke(listener, args);
			}
		}
		catch (InvocationTargetException e)
		{
			throw e.getCause();
		}
	}
	//CSON: IllegalThrowsCheck

	private class Handler implements InvocationHandler
	{
		//CSOFF: IllegalThrowsCheck
		@Override public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable
		{
			ListenerDispatchProxy.this.invoke(method, args);
			return null;
		}
		//CSON: IllegalThrowsCheck
	}

	final private T _proxy;
	final private List<T> _delegates = new CopyOnWriteArrayList<T>();
}
