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

import net.guts.event.internal.SameAsArgumentType;

/**
 * Annotates a method that will filter events prior to them being sent to consumer
 * methods (annotated with {@link Consumes}) in the same class.
 * The annotated method must return {@code boolean} and take exactly one argument.
 * The category of the filtered event is determined by:
 * <ul>
 * <li>The exact type of the method argument; this includes any generic parameters, 
 * for example, {@code List<String>} is different from {@code List<Integer>}.</li>
 * <li>The provided {@link #topic()} if any; if none is provided, then the event is
 * only determined by its type. Event categories with the same type and different 
 * topics are different.</li>
 * <li>If {@link #type()} is provided, then this type determines the event rather 
 * than the argument type; however, when the argument type is generic (e.g. 
 * {@code SortedSet<Integer>}), {@link #type()} can only be a {@code class} (e.g. 
 * {@code Set.class}), in this case the event type is automatically determined by 
 * combining both information (e.g. {@code Set<Integer>}).</li>
 * </ul>
 * <p/>
 * This annotation is ineffective if there is no matching consumer method in the
 * same class.
 * <p/>
 * All elements of this annotation are optional; in most situations, you won't need 
 * to use any of them when annotating a consumer method. You may sometimes need to
 * use {@link #topic()} to disambiguate several events of the same type.
 * <p/>
 * It is not acceptable to have several {@code @Filters}-annotated methods in
 * the same class for the same event category, except if they are disambiguated by
 * the {@link #id()} element; if several filter methods exist for the same event
 * category (i.e. same type and topic), then only one of them will be used to filter
 * events.
 * 
 * @author Jean-Francois Poilpret
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Filters
{
	/**
	 * Topic of the events filtered by the annotated method. Must match an existing 
	 * {@link Channel} bound with the same explicit topic (and matching the event 
	 * type). There also must be a matching consumer method (annotated with
	 * {@link Consumes}) in the same class.
	 * <p/>
	 * If not provided, the annotated method must match an existing 
	 * {@link Channel} with the same event type (and no topic).
	 */
	public String topic() default "";
	
	/**
	 * Explicitly determines the event type (to match an existing {@link Channel} with
	 * the same type) in lieu of the method argument type. It must be a super-type of
	 * the argument type in the annotated method.
	 * <p/>
	 * When specified, events sent through the matching {@link Channel} will be 
	 * automatically filtered out if their type is not compatible with the actual 
	 * argument type of the method.
	 * <p/>
	 * If not provided, the argument type of the annotated method solely determines the
	 * event type.
	 */
	public Class<?> type() default SameAsArgumentType.class;
	
	/**
	 * The identifier of this {@link Filters}-annotated method in this class.
	 * There can be a consumer method in the same class with the same 
	 * {@link Consumes#filterId()} and the same event category.
	 * <p/>
	 * This is only useful when the same class contains several consumer methods for 
	 * the same event category, but each consumer must have a different event filter 
	 * method applied; this situation is normally extremely rare, hence you won't 
	 * generally need to use this element.
	 */
	public String id() default "";
}
