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

import com.google.inject.Inject;

class AnnotationProcessor
{
	@Inject
	public AnnotationProcessor(ClassProcessingErrorHandler handler)
	{
		_handler = handler;
	}
	
	//TODO synchronization?
	public List<ExceptionHandler> process(Class<?> clazz)
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
	
	private List<ExceptionHandler> findHandlers(Class<?> clazz)
	{
		List<ExceptionHandler> handlers = new ArrayList<ExceptionHandler>();
		for (Method method: clazz.getMethods())
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
		return handlers;
	}

	@SuppressWarnings("unchecked") 
	private ExceptionHandler analyzeMethod(Method m, int priority)
	{
		// Check method is void and has one parameter
		if (m.getReturnType() != boolean.class)
		{
			_handler.handleError(ClassProcessingError.HANDLER_MUST_RETURN_BOOLEAN, m);
			return null;
		}
		if (m.getParameterTypes().length != 1)
		{
			_handler.handleError(ClassProcessingError.HANDLER_MUST_HAVE_ONE_ARG, m);
			return null;
		}

		// Find type of event
		Class<?> argType = m.getParameterTypes()[0];
		// Check that the declared type is a subclass of Throwable
		if (!Throwable.class.isAssignableFrom(argType))
		{
			_handler.handleError(ClassProcessingError.HANDLER_ARG_MUST_BE_THROWABLE, m);
			return null;
		}
		return new ExceptionHandler(m, argType.asSubclass(Throwable.class), priority);
	}
	
	final private ClassProcessingErrorHandler _handler;
	final private Map<Class<?>, List<ExceptionHandler>> _inspectedClasses = 
		new HashMap<Class<?>, List<ExceptionHandler>>();
}
