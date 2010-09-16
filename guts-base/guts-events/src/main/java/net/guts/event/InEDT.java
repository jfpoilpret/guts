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
 * indicate that it should be notified of new events from the Swing Event Dispatch
 * Thread (EDT), whatever the {@link Thread} used by the event supplier.
 * <p/>
 * This is useful when working with Swing and a consumer method must act on a Swing
 * {@link javax.swing.JComponent}.
 * <p/>
 * Here is a typical usage:
 * <pre>
 * public class Consumer {
 *     &#64;Consumes &#64;InEDT public void consumes(Integer event) {...}
 * }
 * </pre>
 * <p/>
 * Note that this annotation uses {@link javax.swing.SwingUtilities#invokeLater} to
 * call the annotated consumer method; if, for the same event, several consumer 
 * methods use this annotation, all of them will be called in the same call to
 * {@link javax.swing.SwingUtilities#invokeLater}.
 * <p/>
 * If the supplier sent the event from the EDT, then the consumer method is called
 * immediately.
 * 
 * @author Jean-Francois Poilpret
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface InEDT
{
}
