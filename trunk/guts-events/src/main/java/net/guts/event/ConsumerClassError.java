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

public enum ConsumerClassError
{
	CONSUMES_MUST_RETURN_VOID,
	CONSUMES_MUST_HAVE_ONE_ARG,
	CONSUMES_EVENT_MUST_BE_REGISTERED,
	CONSUMES_CANNOT_HAVE_SEVERAL_THREAD_ANNOTATIONS,
	FILTERS_MUST_RETURN_BOOLEAN,
	FILTERS_MUST_HAVE_ONE_ARG,
	FILTERS_EVENT_MUST_BE_REGISTERED,
}
