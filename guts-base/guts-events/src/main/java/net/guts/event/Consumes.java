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
 * Annotates a method that will become a consumer of a given category of events.
 * The annotated method must return {@code void} and take exactly one argument.
 * The category of the consumed event is determined by:
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
 * The event category determines which {@link Channel} can be used, by 
 * suppliers, to send events that will get notified to the annotated method.
 * <p/>
 * All elements of this annotation are optional; in most situations, you won't need 
 * to use any of them when annotating a consumer method. You may sometimes need to
 * use {@link #topic()} to disambiguate several events of the same type.
 * <p/>
 * For the annotated method to be actually notified when events are sent through the
 * matching {@link Channel}, instances of classes containing annotated methods must
 * first be processed by GUTS-Events. This can happen in 2 different ways:
 * <ul>
 * <li>Automatically for all instances created by a Guice 
 * {@link com.google.inject.Injector} (if this {@code Injector} had {@link EventModule}
 * as one of its {@link com.google.inject.Module}s)</li>
 * <li>Or through an explicit call to {@link EventService#registerConsumers} for any
 * object created outside Guice</li>
 * </ul>
 * <p/>
 * It is perfectly acceptable to have several {@code @Consumes}-annotated methods in
 * the same class, even for the same event category; every method is considered an
 * independent consumer.
 * 
 * @author Jean-Francois Poilpret
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Consumes
{
	/**
	 * Topic of the events consumed by the annotated method. Must match an existing 
	 * {@link Channel} bound with the same explicit topic (and matching the event 
	 * type).
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
	 * Order in which the annotated method should be called when a matching event is 
	 * sent; it can be useful when several methods (in the same or different classes) 
	 * consume the same events, but must absolutely be called in a given order.
	 * <p/>
	 * In most cases, you won't need to change the default.
	 */
	public int priority() default 0;

	/**
	 * The identifier of a {@link Filters}-annotated method in the <b>same</b> class.
	 * There must be a method in the same class with the same {@link Filters#id()} and
	 * the same event category.
	 * <p/>
	 * This is only useful when the same class contains several consumer methods for 
	 * the same event category, but each consumer must have a different event filter 
	 * method applied; this situation is normally extremely rare, hence you won't 
	 * generally need to use this element.
	 */
	public String filterId() default "";
}
