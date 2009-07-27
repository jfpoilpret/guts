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

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import net.guts.event.ConsumerClassError;
import net.guts.event.ErrorHandler;

import com.google.inject.Singleton;

@Singleton
public class DefaultErrorHandler implements ErrorHandler
{
	public void handleError(ConsumerClassError error, Method method, Type type, String topic)
	{
		throw new IllegalArgumentException(error.errorMessage(method, type, topic));
	}
}
