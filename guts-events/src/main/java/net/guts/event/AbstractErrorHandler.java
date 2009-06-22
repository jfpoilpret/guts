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
import java.util.EnumMap;

public abstract class AbstractErrorHandler implements ErrorHandler
{
	protected String errorMessage(
		ConsumerClassError error, Method method, Type type, String topic)
	{
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		return String.format(MESSAGES.get(error), className, methodName, type, topic);
	}
	
	// CSOFF: LineLengthCheck
	static final private String ERR_CONSUMES_EVENT_MUST_BE_REGISTERED =
		"@Consumes on method '%1$s.2$%s' matches no registered Event Channel (type = %3$s, topic = '%4$s'";
	static final private String ERR_CONSUMES_MUST_HAVE_ONE_ARG =
		"@Consumes is forbidden on method '%1$s.%2$s' because it must have exactly one argument";
	static final private String ERR_CONSUMES_MUST_RETURN_VOID =
		"@Consumes is forbidden on method '%1$s.%2$s' because it must return void";
	static final private String ERR_CONSUMES_CANNOT_HAVE_MORE_THAN_ONE_THREAD_ANNOTATION =
		"@Consumes method '%1$s.%2$s' can have at most one thread-policy annotation";
	static final private String ERR_FILTERS_EVENT_MUST_BE_REGISTERED =
		"@Filters on method '%1$s.%2$s' matches no registered Event Channel (type = %3$s, topic = '%4$s'";
	static final private String ERR_FILTERS_MUST_HAVE_ONE_ARG =
		"@Filters is forbidden on method '%1$s.%2$s' because it must have exactly one argument";
	static final private String ERR_FILTERS_MUST_RETURN_BOOLEAN =
		"@Filters is forbidden on method '%1$s.%2$s' because it must return boolean";
	// CSON: LineLengthCheck
	static final private EnumMap<ConsumerClassError, String> MESSAGES =
		new EnumMap<ConsumerClassError, String>(ConsumerClassError.class);
	static
	{
		MESSAGES.put(ConsumerClassError.CONSUMES_EVENT_MUST_BE_REGISTERED, 
			ERR_CONSUMES_EVENT_MUST_BE_REGISTERED);
		MESSAGES.put(ConsumerClassError.CONSUMES_MUST_HAVE_ONE_ARG, 
			ERR_CONSUMES_MUST_HAVE_ONE_ARG);
		MESSAGES.put(ConsumerClassError.CONSUMES_MUST_RETURN_VOID, 
			ERR_CONSUMES_MUST_RETURN_VOID);
		MESSAGES.put(ConsumerClassError.CONSUMES_CANNOT_HAVE_SEVERAL_THREAD_ANNOTATIONS, 
			ERR_CONSUMES_CANNOT_HAVE_MORE_THAN_ONE_THREAD_ANNOTATION);
		MESSAGES.put(ConsumerClassError.FILTERS_EVENT_MUST_BE_REGISTERED, 
			ERR_FILTERS_EVENT_MUST_BE_REGISTERED);
		MESSAGES.put(ConsumerClassError.FILTERS_MUST_HAVE_ONE_ARG, 
			ERR_FILTERS_MUST_HAVE_ONE_ARG);
		MESSAGES.put(ConsumerClassError.FILTERS_MUST_RETURN_BOOLEAN, 
			ERR_FILTERS_MUST_RETURN_BOOLEAN);
	}
}
