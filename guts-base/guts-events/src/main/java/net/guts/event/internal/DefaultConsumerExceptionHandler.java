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

import net.guts.event.ConsumerExceptionHandler;

import com.google.inject.Singleton;

@Singleton
public class DefaultConsumerExceptionHandler implements ConsumerExceptionHandler
{
	// CSOFF: RegexpSinglelineJavaCheck
	public void handleException(
		Throwable e, Method method, Object instance, Type eventType, String topic)
	{
		System.err.printf(LOG, 
			method.getDeclaringClass().getName(), method.getName(), instance, e.getMessage());
		e.printStackTrace();
	}
	// CSON: RegexpSinglelineJavaCheck
	
	static final private String LOG = 
		"Consumer %s.%s (instance= %s) has thrown exception %s\n";
}
