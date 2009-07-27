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

package net.guts.event;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Kinds of errors that can occur during consumer class processing by GUTS-Events.
 * This is sent to {@link ErrorHandler#handleError} whenever an error is 
 * encountered while processing a method annotated with {@link Consumes} or
 * {@link Filters}.
 * 
 * @author Jean-Francois Poilpret
 */
public enum ConsumerClassError
{
	/**
	 * Indicates that a {@code @Consumes}-annotated method is declared with either
	 * no argument or more than one argument. {@code @Consumes} methods must have
	 * exactly one argument.
	 */
	CONSUMES_MUST_HAVE_ONE_ARG(
		ErrorFormats.ERR_CONSUMES_MUST_HAVE_ONE_ARG),
	
	/**
	 * Indicates that a {@code @Consumes}-annotated method has an event type (type 
	 * of its argument) or event {@code topic} that don't match any registered 
	 * {@link Channel} ({@code Channel}s must be registered with 
	 * {@link Events#bindChannel}).
	 */
	CONSUMES_EVENT_MUST_BE_REGISTERED(
		ErrorFormats.ERR_CONSUMES_EVENT_MUST_BE_REGISTERED),
	
	/**
	 * Indicates that a {@code @Consumes}-annotated method is specified a
	 * {@link Consumes#type()} that doesn't match the type of its argument:
	 * {@link Consumes#type()} must be a super-type of the argument type.
	 */
	CONSUMES_TYPE_MUST_BE_ARG_SUPERTYPE(
		ErrorFormats.ERR_CONSUMES_TYPE_MUST_BE_ARG_SUPERTYPE),

	/**
	 * Indicates that a {@code @Consumes}-annotated method has more than one
	 * additional {@code ThreadPolicy}-based annotations (such as {@link InEDT}
	 * and {@link InDeferredThread}). {@code @Consumes} methods may have zero or 
	 * one such annotations, no more.
	 */
	CONSUMES_CANNOT_HAVE_SEVERAL_THREAD_ANNOTATIONS(
		ErrorFormats.ERR_CONSUMES_CANNOT_HAVE_MORE_THAN_ONE_THREAD_ANNOTATION),
	
	/**
	 * Indicates that a {@code @Filters}-annotated method is declared to return
	 * something different than {@code boolean}. {@code @Filters} methods must return
	 * {@code boolean}.
	 */
	FILTERS_MUST_RETURN_BOOLEAN(
		ErrorFormats.ERR_FILTERS_MUST_RETURN_BOOLEAN),
	
	/**
	 * Indicates that a {@code @Filters}-annotated method is declared with either
	 * no argument or more than one argument. {@code @Filters} methods must have
	 * exactly one argument.
	 */
	FILTERS_MUST_HAVE_ONE_ARG(
		ErrorFormats.ERR_FILTERS_MUST_HAVE_ONE_ARG),
	
	/**
	 * Indicates that a {@code @Filters}-annotated method has an event type (type 
	 * of its argument) or event {@code topic} that don't match any registered 
	 * {@link Channel} ({@code Channel}s must be registered with 
	 * {@link Events#bindChannel}).
	 */
	FILTERS_EVENT_MUST_BE_REGISTERED(
		ErrorFormats.ERR_FILTERS_EVENT_MUST_BE_REGISTERED),
	
	/**
	 * Indicates that a {@code @Filters}-annotated method is specified a
	 * {@link Filters#type()} that doesn't match the type of its argument:
	 * {@link Filters#type()} must be a super-type of the argument type.
	 */
	FILTERS_TYPE_MUST_BE_ARG_SUPERTYPE(
		ErrorFormats.ERR_FILTERS_TYPE_MUST_BE_ARG_SUPERTYPE);

	private ConsumerClassError(String format)
	{
		_format = format;
	}
	
	/**
	 * Formats all arguments passed to {@link ErrorHandler#handleError} into a 
	 * nice and clear message, which can be used eg for logging purposes.
	 */
	public String errorMessage(Method method, Type type, String topic)
	{
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		return String.format(_format, className, methodName, type, topic);
	}

	private final String _format;
}

final class ErrorFormats
{
	private ErrorFormats()
	{
	}
	
	// CSOFF: LineLengthCheck
	static final String ERR_CONSUMES_EVENT_MUST_BE_REGISTERED =
		"@Consumes on method '%1$s.2$%s' matches no registered Event Channel (type = %3$s, topic = '%4$s'";
	static final String ERR_CONSUMES_MUST_HAVE_ONE_ARG =
		"@Consumes is forbidden on method '%1$s.%2$s' because it must have exactly one argument";
	static final String ERR_CONSUMES_CANNOT_HAVE_MORE_THAN_ONE_THREAD_ANNOTATION =
		"@Consumes method '%1$s.%2$s' (event type = %3$s, topic ='%4$s') can have at most one thread-policy annotation";
	static final String ERR_CONSUMES_TYPE_MUST_BE_ARG_SUPERTYPE =
		"@Consumes method '%1$s.%2$s' has type annotation which is not supertype of argument (%3$s)";
	static final String ERR_FILTERS_EVENT_MUST_BE_REGISTERED =
		"@Filters on method '%1$s.%2$s' matches no registered Event Channel (type = %3$s, topic = '%4$s'";
	static final String ERR_FILTERS_MUST_HAVE_ONE_ARG =
		"@Filters is forbidden on method '%1$s.%2$s' because it must have exactly one argument";
	static final String ERR_FILTERS_MUST_RETURN_BOOLEAN =
		"@Filters is forbidden on method '%1$s.%2$s' because it must return boolean";
	static final String ERR_FILTERS_TYPE_MUST_BE_ARG_SUPERTYPE =
		"@Filters method '%1$s.%2$s' has type annotation which is not supertype of argument (%3$s)";
	// CSON: LineLengthCheck
}
