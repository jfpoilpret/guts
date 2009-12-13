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

package net.guts.event.internal;

enum InternalError
{
	BAD_THREAD_POLICY(
		ErrorFormats.ERR_CONSUMES_CANNOT_HAVE_MORE_THAN_ONE_THREAD_ANNOTATION, null),
	BAD_RETURN_TYPE(null, ErrorFormats.ERR_FILTERS_MUST_RETURN_BOOLEAN),
	BAD_ARG_NUMBER(ErrorFormats.ERR_CONSUMES_MUST_HAVE_ONE_ARG, 
		ErrorFormats.ERR_FILTERS_MUST_HAVE_ONE_ARG),
	EVENT_NOT_REGISTERED(ErrorFormats.ERR_CONSUMES_EVENT_MUST_BE_REGISTERED,
		ErrorFormats.ERR_FILTERS_EVENT_MUST_BE_REGISTERED),
	BAD_EXPLICIT_TYPE(ErrorFormats.ERR_CONSUMES_TYPE_MUST_BE_ARG_SUPERTYPE,
		ErrorFormats.ERR_FILTERS_TYPE_MUST_BE_ARG_SUPERTYPE);
	
	private InternalError(String consumesError, String filtersError)
	{
		_consumesError = consumesError;
		_filtersError = filtersError;
	}
	
	String getError(boolean forFilter)
	{
		return (forFilter ? _filtersError : _consumesError);
	}
	
	final private String _consumesError;
	final private String _filtersError;
}

final class ErrorFormats
{
	private ErrorFormats()
	{
	}
	
	// CSOFF: LineLengthCheck
	static final String ERR_CONSUMES_EVENT_MUST_BE_REGISTERED =
		"@Consumes on method '%1$s.2$%s' matches no registered Event Channel (type = %3$s, topic = '%4$s')";
	static final String ERR_CONSUMES_MUST_HAVE_ONE_ARG =
		"@Consumes is forbidden on method '%1$s.%2$s' because it must have exactly one argument";
	static final String ERR_CONSUMES_CANNOT_HAVE_MORE_THAN_ONE_THREAD_ANNOTATION =
		"@Consumes method '%1$s.%2$s' (event type = %3$s, topic ='%4$s') can have at most one thread-policy annotation";
	static final String ERR_CONSUMES_TYPE_MUST_BE_ARG_SUPERTYPE =
		"@Consumes method '%1$s.%2$s' has type annotation which is not supertype of argument (%3$s)";
	static final String ERR_FILTERS_EVENT_MUST_BE_REGISTERED =
		"@Filters on method '%1$s.%2$s' matches no registered Event Channel (type = %3$s, topic = '%4$s')";
	static final String ERR_FILTERS_MUST_HAVE_ONE_ARG =
		"@Filters is forbidden on method '%1$s.%2$s' because it must have exactly one argument";
	static final String ERR_FILTERS_MUST_RETURN_BOOLEAN =
		"@Filters is forbidden on method '%1$s.%2$s' because it must return boolean";
	static final String ERR_FILTERS_TYPE_MUST_BE_ARG_SUPERTYPE =
		"@Filters method '%1$s.%2$s' has type annotation which is not supertype of argument (%3$s)";
	// CSON: LineLengthCheck
}
