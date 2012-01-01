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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates an event consumer method (already annotated with {@link Consumes}) to
 * indicate that it should not be notified of new events from the {@link Thread} that 
 * has been used by the events supplier, but from a new {@link Thread}.
 * <p/>
 * Here is a typical usage:
 * <pre>
 * public class Consumer {
 *     &#64;Consumes &#64;InDeferredThread public void consumes(Integer event) {...}
 * }
 * </pre>
 * <p/>
 * Note that if, for the same event, several consumer methods (even in different 
 * classes and instances) use this annotation, all of them will be called in one
 * <b>same</b> new {@link Thread}, different from the one which the event supplier
 * sent the event from.
 * 
 * @author Jean-Francois Poilpret
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface InDeferredThread
{
}
