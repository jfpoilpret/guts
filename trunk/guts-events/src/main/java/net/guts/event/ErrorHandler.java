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

import net.guts.event.internal.DefaultErrorHandler;

import com.google.inject.ImplementedBy;

/**
 * Interface which implementation, if bound with Guice, will get notified of
 * problems during processing of classes having methods annotated with
 * {@link Consumes} or {@link Filters}.
 * <p/>
 * Default implementation simply throws {@link IllegalArgumentException}.
 * <p/>
 * To bind your own implementation, include the following code in one of your
 * Guice {@link com.google.inject.Module}s:
 * <pre>
 * bind(ErrorHandler.class).to(MyErrorHandler.class);
 * </pre>
 * 
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(DefaultErrorHandler.class)
public interface ErrorHandler
{
	/**
	 * Called whenever an error is detected while processing a method annotated
	 * with {@link Consumes} or {@link Filters}.
	 * 
	 * @param error the kind of error found during processing of {@code method}
	 * @param method method which processing incurred {@code error}
	 * @param type event type of the consumer or filter method which incurred 
	 * {@code error}; {@code null} when {@code error} is one of 
	 * {@link ConsumerClassError#CONSUMES_MUST_HAVE_ONE_ARG},
	 * {@link ConsumerClassError#FILTERS_MUST_RETURN_BOOLEAN} or
	 * {@link ConsumerClassError#FILTERS_MUST_HAVE_ONE_ARG}.
	 * @param topic event topic of the consumer or filter method which incurred
	 * {@code error}; {@code null} when {@code error} is one of 
	 * {@link ConsumerClassError#CONSUMES_MUST_HAVE_ONE_ARG},
	 * {@link ConsumerClassError#FILTERS_MUST_RETURN_BOOLEAN} or
	 * {@link ConsumerClassError#FILTERS_MUST_HAVE_ONE_ARG}.
	 */
	public void handleError(
		ConsumerClassError error, Method method, Type type, String topic);
}
