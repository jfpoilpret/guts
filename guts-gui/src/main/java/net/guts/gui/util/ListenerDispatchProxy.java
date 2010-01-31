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

public final class ListenerDispatchProxy<T>
{
	static public <T> ListenerDispatchProxy<T> createProxy(Class<T> type)
	{
		return new ListenerDispatchProxy<T>(TypeLiteral.get(type));
	}
	
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
	
	public T notifier()
	{
		return _proxy;
	}

	public void addListener(T listener)
	{
		_delegates.add(listener);
	}
	
	public void removeListener(T listener)
	{
		_delegates.remove(listener);
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
