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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a method that will get called whenever an exception is processed by
 * {@link ExceptionHandlerManager}, typically uncaught exceptions that occur in 
 * the Event Dispatch Thread.
 * <p/>
 * The annotated method must return a {@code boolean} and take exactly one argument 
 * which type must be a subclass of {@link java.lang.Throwable}. It will be notified 
 * only of exceptions that match its argument type.
 * <p/>
 * Guts-GUI {@code ExceptionHandlerManager} allows chaining {@code HandlesException}
 * methods, sorted according to {@code priority}: each of them will be called in
 * sequence until one of them returns {@code true}.
 * <p/>
 * The annotated method can do whatever it wants with the passed exception e.g.
 * display a message to the end user, send a bug report to the developers...
 * <p/>
 * The annotated method should not make any assumption as to which thread it is
 * called in, this may be the Event Dispatch Thread or another thread.
 *
 * @author Jean-Francois Poilpret
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandlesException
{
	/**
	 * The order in which the method should be called when there are several
	 * methods annotated with {@code @HandlesException}. The lower the value,
	 * the earlier the method will be called.
	 */
	int priority() default 0;
}
