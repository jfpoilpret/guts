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

import net.guts.event.internal.DefaultConsumerExceptionHandler;

import com.google.inject.ImplementedBy;

/**
 * Interface which implementation, if bound with Guice, will get notified of
 * exceptions thrown by event consumer methods upon notification of events sent
 * by a supplier.
 * <p/>
 * Default implementation simply swallows any exception. It is advised to define
 * your own implementation in order, at least, to log exceptions thrown by
 * consumer methods.
 * <p/>
 * To bind your own implementation, include the following code in one of your
 * Guice {@link com.google.inject.Module}s:
 * <pre>
 * bind(ConsumerExceptionHandler.class).to(MyConsumerExceptionHandler.class);
 * </pre>
 * 
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(DefaultConsumerExceptionHandler.class)
public interface ConsumerExceptionHandler
{
	/**
	 * This method is called whenever an event consumer method throws an exception.
	 * You may use this method to log the thrown exception or throw another exception,
	 * which would then interrupt the event notification process and be rethrown to
	 * the initial event supplier.
	 * 
	 * @param e exception thrown by {@code method} of {@code instance}
	 * @param method {@code @Consumes} or {@code @Filters} method which has thrown 
	 * {@code e}
	 * @param instance object from which {@code method} has thrown {@code e}
	 * @param eventType the event type for which {@code method} was called
	 * @param topic the event topic for which {@code method} was called
	 */
	public void handleException(
		Throwable e, Method method, Object instance, Type eventType, String topic);
}
