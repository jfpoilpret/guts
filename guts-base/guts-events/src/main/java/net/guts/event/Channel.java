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

/**
 * Implemented by all event channels managed by GUTS-Events, this interface allows
 * suppliers to send (aka push) events to consumers.
 * <p/>
 * You can declare bindings to {@code Channel} for any types of events, with or 
 * without a named topic, by using {@link Events#bindChannel} in your own
 * {@link com.google.inject.Module}s. Then you can inject bound {@code Channel}s
 * into your own classes as any other bound object in Guice.
 * <p/>
 * Note that GUTS-Events can bind primitive event types (eg {@code boolean},
 * {@code int}, {@code long}...) However, since Java generics don't support
 * primitive types, those types of events must be bound to Channels of the
 * matching wrapper type (eg {@code int} event type can be bound to 
 * {@code Channel<Integer>}), however for injection, these primitive events
 * channels must be differentiated from other (non primitive) channels by the
 * {@link Event#primitive()} annotation:
 * <pre>
 * &#64;Inject &#64;Event(primitive = true) private Channel&lt;Integer&gt; channel;
 * </pre>
 * Hence it is still possible to have event channels for both types {@code int} and
 * {@code Integer}.
 *
 * @param <T> Type of the event published by {@code this} channel
 * 
 * @author Jean-Francois Poilpret
 */
public interface Channel<T>
{
	/**
	 * Sends {@code event} to all consumers of {@code this} Channel.
	 * 
	 * @param event the event to be sent to consumers
	 */
	public void publish(T event);
}
