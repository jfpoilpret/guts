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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * Binding annotation used for injecting {@link Channel}s. This annotation is required 
 * only in one of the following situations:
 * <ul>
 * <li>The {@link Channel} to bind has an associated topic</li>
 * <li>The {@link Channel} to bind has a primitive type of events (eg {@code int})</li>
 * </ul>
 * It is used as follows:
 * <pre>
 * &#64;Inject &#64;Event(primitive = true) private Channel&lt;Integer&gt; channel;
 * &#64;Inject &#64;Event(topic = "TOPIC") private Channel&lt;Integer&gt; channel;
 * </pre>
 * 
 * @author Jean-Francois Poilpret
 */
@BindingAnnotation
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD}) 
@Retention(RetentionPolicy.RUNTIME)
public @interface Event
{
	/**
	 * Topic of the events sent by the injected {@link Channel}.
	 * <p/>
	 * If not provided, the injected {@link Channel} handles events defined
	 * only by their type.
	 */
	public String topic() default "";
	
	/**
	 * If {@code true}, indicates that the injected {@link Channel} sends events of
	 * a primitive (eg {@code boolean}, {@code int}...) type.
	 * 
	 * @see Channel
	 */
	public boolean primitive() default false;
}
