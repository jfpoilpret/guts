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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AnnotationProcessor
{
	static final private Logger _logger = LoggerFactory.getLogger(AnnotationProcessor.class);

	public List<ExceptionHandler> process(Class<?> clazz)
	{
		synchronized (_inspectedClasses)
		{
			// First check if clazz has been processed already
			List<ExceptionHandler> handlers = _inspectedClasses.get(clazz);
			if (handlers == null)
			{
				handlers = findHandlers(clazz);
				_inspectedClasses.put(clazz, handlers);
			}
			return handlers;
		}
	}
	
	private List<ExceptionHandler> findHandlers(Class<?> clazz)
	{
		List<ExceptionHandler> handlers = new ArrayList<ExceptionHandler>();
		Class<?> oneClass = clazz;
		while (oneClass != null)
		{
			for (Method method: oneClass.getDeclaredMethods())
			{
				// Analyze each consumer method
				HandlesException handles = method.getAnnotation(HandlesException.class);
				if (handles != null)
				{
					// Check that method has the right prototype: boolean f(Throwable e)
					ExceptionHandler handler = analyzeMethod(method, handles.priority());
					if (handler != null)
					{
						handlers.add(handler);
					}
				}
			}
			oneClass = oneClass.getSuperclass();
		}
		return handlers;
	}

	private ExceptionHandler analyzeMethod(Method m, int priority)
	{
		// Check method is void and has one parameter
		if (m.getReturnType() != boolean.class)
		{
			logError(ERR_HANDLER_MUST_RETURN_BOOLEAN, m);
			return null;
		}
		if (m.getParameterTypes().length != 1)
		{
			logError(ERR_HANDLER_MUST_HAVE_ONE_THROWABLE_ARG, m);
			return null;
		}

		// Find type of event
		Class<?> argType = m.getParameterTypes()[0];
		// Check that the declared type is a subclass of Throwable
		if (!Throwable.class.isAssignableFrom(argType))
		{
			logError(ERR_HANDLER_MUST_HAVE_ONE_THROWABLE_ARG, m);
			return null;
		}
		return new ExceptionHandler(m, argType.asSubclass(Throwable.class), priority);
	}
	
	private void logError(String format, Method method)
	{
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		_logger.error(format, className, methodName);
	}

	// CSOFF: LineLengthCheck
	static final private String ERR_HANDLER_MUST_HAVE_ONE_THROWABLE_ARG =
		"@HandlesException is forbidden on method '{}.{}' because it must have exactly one Throwable argument (or any Throwable subtype)";
	static final private String ERR_HANDLER_MUST_RETURN_BOOLEAN =
		"@HandlesException is forbidden on method '{}.{}' because it must return boolean";
	// CSON: LineLengthCheck

	final private Map<Class<?>, List<ExceptionHandler>> _inspectedClasses = 
		new HashMap<Class<?>, List<ExceptionHandler>>();
}
