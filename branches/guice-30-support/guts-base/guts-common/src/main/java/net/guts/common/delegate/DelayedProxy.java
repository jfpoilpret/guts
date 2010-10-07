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

package net.guts.common.delegate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.internal.cglib.proxy.Callback;
import com.google.inject.internal.cglib.proxy.Enhancer;
import com.google.inject.internal.cglib.proxy.Factory;
import com.google.inject.internal.cglib.proxy.MethodInterceptor;
import com.google.inject.internal.cglib.proxy.MethodProxy;

/**
 * Utility class that can create a "delayed proxy" that delegates all calls
 * to a given class instance, and forwards these calls to a delegate that can
 * be set after the actual calls, in which case past calls will be replayed
 * against the actual delegate.
 *
 * @author Jean-Francois Poilpret
 */
public class DelayedProxy
{
	static final private Logger _logger = LoggerFactory.getLogger(DelayedProxy.class);
	
	private DelayedProxy()
	{
	}

	/**
	 * Create a delayed proxy for the given type. The actual delegate can be set, 
	 * at any time, with {@link #setDelegate(Object, Object)}.
	 *  
	 * @param <T> type of the delayed proxy to create 
	 * @param target type of the delayed proxy to create
	 * @return a new delayed proxy of the required type
	 */
	@SuppressWarnings("unchecked") 
	static public <T> T create(Class<T> target)
	{
		return (T) Enhancer.create(target, new Delegate());
	}

	/**
	 * Set the actual delegate of a delayed proxy created by {@link #create(Class)}.
	 * If method calls have been performed on {@code source} before, then they will
	 * be immediately replayed, in the same order, on {@code target}. Later calls to
	 * {@code source} will be directly forwarded to {@code target}.
	 * 
	 * @param <T> type of the delayed proxy to create 
	 * @param source the delayed proxy for which to set the delegate
	 * @param target the delegate to set to {@code source} delayed proxy
	 */
	static public <T> void setDelegate(T source, T target)
	{
		if (target == null)
		{
			_logger.error("setDelegate() target must not be null");
		}
		if (source instanceof Factory)
		{
			boolean isDelegate = false;
			for (Callback callback: ((Factory) source).getCallbacks())
			{
				if (callback instanceof Delegate)
				{
					((Delegate) callback).setDelegate(target);
					isDelegate = true;
				}
			}
			if (!isDelegate)
			{
				_logger.error(
					"setDelegate() source `{}` was not obtained by create()", source);
			}
		}
		else
		{
			_logger.error("setDelegate() source `{}` was not obtained by create()", source);
		}
	}
	
	static private class Delegate implements MethodInterceptor
	{
		public void setDelegate(Object target)
		{
			_target = target;
			if (_calls != null)
			{
				for (Call call: _calls)
				{
					call.invoke(_target);
				}
				_calls = null;
			}
		}

		@Override public Object intercept(
			Object source, Method method, Object[] args, MethodProxy proxy)
			throws Throwable
		{
			if (_target != null)
			{
				return method.invoke(_target, args);
			}
			else
			{
				if (_calls == null)
				{
					_calls = new ArrayList<Call>();
				}
				_calls.add(new Call(method, args));
				return null;
			}
		}
		
		private Object _target = null;
		private List<Call> _calls;
	}
	
	static private class Call
	{
		public Call(Method method, Object[] args)
		{
			_method = method;
			_args = args;
		}
		
		public void invoke(Object target)
		{
			try
			{
				_method.invoke(target, _args);
			}
			catch (Exception e)
			{
				String msg = String.format(
					"Problem invoking `%s` on %s", _method.getName(), target);
				_logger.warn(msg, e);
			}
		}
		
		final private Method _method;
		final private Object[] _args;
	}
}
