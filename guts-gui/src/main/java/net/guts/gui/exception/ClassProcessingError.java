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

/**
 * Kinds of errors that can occur during exception handler class processing by 
 * GUTS-GUI.
 * This is sent to {@link ClassProcessingErrorHandler#handleError} whenever an 
 * error is encountered while processing a method annotated with 
 * {@link HandlesException}.
 * 
 * @author Jean-Francois Poilpret
 */
public enum ClassProcessingError
{
	/**
	 * Indicates that a {@code @HandlesException}-annotated method is declared 
	 * with either no argument or more than one argument. {@code @HandlesException} 
	 * methods must have exactly one argument.
	 */
	HANDLER_MUST_HAVE_ONE_ARG(
		ErrorFormats.ERR_HANDLER_MUST_HAVE_THROWABLE_ARG),
	
	/**
	 * Indicates that a {@code @HandlesException}-annotated method is declared 
	 * with one argument which type is not a subtype of {@link Throwable}.
	 * {@code @HandlesException} methods' argument must be of {@code Throwable}
	 * type or any subtype.
	 */
	HANDLER_ARG_MUST_BE_THROWABLE(
		ErrorFormats.ERR_HANDLER_MUST_HAVE_THROWABLE_ARG),
		
	/**
	 * Indicates that a {@code @HandlesException}-annotated method is declared 
	 * to return something different than {@code boolean}. {@code @HandlesException}
	 * methods must return {@code boolean}.
	 */
	HANDLER_MUST_RETURN_BOOLEAN(
		ErrorFormats.ERR_HANDLER_MUST_RETURN_BOOLEAN);
	
	private ClassProcessingError(String format)
	{
		_format = format;
	}
	
	/**
	 * Formats all arguments passed to {@link ClassProcessingErrorHandler#handleError} 
	 * into a nice and clear message, which can be used eg for logging purposes.
	 */
	public String errorMessage(Method method)
	{
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		return String.format(_format, className, methodName);
	}

	private final String _format;
}

final class ErrorFormats
{
	private ErrorFormats()
	{
	}
	
	// CSOFF: LineLengthCheck
	static final String ERR_HANDLER_MUST_HAVE_THROWABLE_ARG =
		"@HandlesException is forbidden on method '%1$s.%2$s' because it must have exactly one Throwable argument (or any Throwable subtype)";
	static final String ERR_HANDLER_MUST_RETURN_BOOLEAN =
		"@Filters is forbidden on method '%1$s.%2$s' because it must return boolean";
	// CSON: LineLengthCheck
}
