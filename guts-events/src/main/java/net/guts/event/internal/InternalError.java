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

import net.guts.event.ConsumerClassError;

enum InternalError
{
	BAD_RETURN_TYPE(null, ConsumerClassError.FILTERS_MUST_RETURN_BOOLEAN),
	BAD_ARG_NUMBER(ConsumerClassError.CONSUMES_MUST_HAVE_ONE_ARG, 
		ConsumerClassError.FILTERS_MUST_HAVE_ONE_ARG),
	EVENT_NOT_REGISTERED(ConsumerClassError.CONSUMES_EVENT_MUST_BE_REGISTERED,
		ConsumerClassError.FILTERS_EVENT_MUST_BE_REGISTERED),
	EXPLICIT_TYPE_NOT_ARG_SUPERTYPE(ConsumerClassError.CONSUMES_TYPE_MUST_BE_ARG_SUPERTYPE,
		ConsumerClassError.FILTERS_TYPE_MUST_BE_ARG_SUPERTYPE);
	
	private InternalError(
		ConsumerClassError consumesError, ConsumerClassError filtersError)
	{
		_consumesError = consumesError;
		_filtersError = filtersError;
	}
	
	ConsumerClassError getError(boolean forFilter)
	{
		return (forFilter ? _filtersError : _consumesError);
	}
	
	final private ConsumerClassError _consumesError;
	final private ConsumerClassError _filtersError;
}
