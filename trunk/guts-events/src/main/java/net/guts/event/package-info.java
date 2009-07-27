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

/**
 * This package contains the complete API of GUTS-Events, the Event Bus for Guice.
 * <p/>
 * The package documentation is broken down in several sections, which you can 
 * follow from the beginning to the end as a first usage tutorial, or you may
 * directly jump to the section of interest to you, from the following table of
 * contents:
 * <ul>
 * <li><a href="#guts1">Main concepts</a></li>
 * <li><a href="#guts2">Simple usage</a></li>
 * <li><a href="#guts3">Event filtering</a></li>
 * <li><a href="#guts4">Controlling priority of consumers notification</a></li>
 * <li><a href="#guts5">Controlling thread executing consumers notification</a></li>
 * <li><a href="#guts6">Adding special processing to consumer returned values</a></li>
 * <li><a href="#guts7">Handling exceptions thrown by consumers</a></li>
 * <li><a href="#guts8">Handling configuration errors</a></li>
 * <li><a href="#guts9">Miscellaneous</a></li>
 * </ul>
 * 
 * <p/><a name="guts1"></a><h3>Main concepts</h3>
 * GUTS-Events is based on the following concepts:
 * <ul>
 * <li>An <b>event</b> is a piece of information produced by one (or more) 
 * <b>supplier</b> and notified to one (or more) <b>consumer</b>; an event can be 
 * an instance of any Java class or even a value of a primitive type 
 * {@code boolean}, {@code int}, {@code long}...)</li>
 * <li>A <b>Channel</b> is the "tube" through which events flow, from supplier(s) to 
 * consumer(s)</li>
 * <li>Events are categorized based on their <b>type</b>, and optionally on a 
 * <b>topic</b> ({@code String} literal)</li>
 * <li>There is exactly one {@code Channel} per event category</li>
 * </ul>
 * The main advantage in using a {@code Channel<T>} is <b>total decoupling</b> 
 * between suppliers and consumers. Suppliers don't know (and don't have to know) what
 * consumers exist for the events they produce, it is even possible that no consumer
 * exists at a given time for a produced event; similarly, consumers don't know what
 * suppliers produce the events they are interested in; it may even happen that at a
 * given time, no supplier at all produces these events.
 * <p/>
 * In GUTS-Events, consumers are just any method of any instance, annotated with 
 * {@link net.guts.event.Consumes}. Such method must return {@code void} and take 
 * exactly one argument, the type of which determines the kind of event consumed.
 * <p/>
 * {@link net.guts.event.Channel} is the way for a supplier to send an event to consumers.
 * <p/>
 * An event {@link net.guts.event.Channel} is the only way for an event supplier to send
 * events to consumers. {@link net.guts.event.Channel}s are strongly-typed, optionally 
 * named (i.e. given an {@link net.guts.event.Event#topic}) and must be bound explicitly
 * within a Guice module for them to be injectable into Guice-injected objects.
 * <p/>
 * GUTS-Events has no concrete representation of suppliers: they can be any method from
 * any class, using a {@link net.guts.event.Channel} to send one or more events.
 * 
 * <p/><a name="guts2"></a><h3>Simple usage</h3>
 * <h4>Simple types of events</h4>
 * Snippets below show a simple common usage of GUTS Events.
 * <p/>
 * First of all, GUTS-Events must be initialized in Guice through 
 * {@link net.guts.event.EventModule} and a binding must be created for every 
 * {@link net.guts.event.Channel}, this is achieved through one of 
 * {@link net.guts.event.Events#bindChannel} helper methods:
 * <pre>
 * Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
 * {
 *     &#64;Override protected void configure()
 *     {
 *         Events.bindChannel(binder(), Integer.class);
 *     }
 * });
 * </pre>
 * The example above creates a binding to a {@code Channel<Integer>}, with no 
 * name (no topic), that can be then injected into any instance (supplier) that 
 * needs it:
 * <pre>
 * public class Supplier
 * {
 *     &#64;Inject Supplier(Channel&lt;Integer&gt; channel)
 *     {
 *         // Send an event to this channel (i.e. to all its consumers)
 *         channel.publish(10);
 *         ...
 *     }
 *     ...
 * }
 * </pre>
 * Note that the above {@code channel} will not allow sending anything else than
 * {@code Integer} events since all {@link net.guts.event.Channel}s are strongly 
 * typed.
 * <p/>
 * Now how are consumers defined for this kind of events?
 * <pre>
 * public class MyClass
 * {
 *     &#64;Consumes public void push(Integer event)
 *     {
 *         ...
 *     }
 *     ...
 * }
 * </pre> 
 * For each instance of {@code MyClass} created by Guice, its {@code push} method
 * will be called for every {@code Integer} event sent by {@code channel} above.
 * <p/>
 * With GUTS-Events, consumers have no special interface to implement, they just
 * have to use the {@link net.guts.event.Consumes} annotation for each consumer 
 * method. During class instantiation by Guice, GUTS-Events will automatically look 
 * for such annotations and, when found, will "attach" the instance and 
 * the annotated method to the right event {@link net.guts.event.Channel}.
 * <p/>
 * If {@code MyClass} is not instantiated by Guice, you can still use it with 
 * GUTS-Events, this is further described in {@link net.guts.event.EventService} API.
 * 
 * <h4>Named events (topic)</h4>
 * GUTS-Events also supports named events (with a non-null topic), in order to 
 * differentiate different kinds of events of the same type:
 * <pre>
 * Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
 * {
 *     &#64;Override protected void configure()
 *     {
 *         Events.bindChannel(binder(), Integer.class, "TOPIC1");
 *         Events.bindChannel(binder(), Integer.class, "TOPIC2");
 *     }
 * });
 * </pre>
 * Injection of the matching {@code Channel} is done by using {@link net.guts.event.Event}
 * annotation:
 * <pre>
 *     &#64;Inject Supplier(&#64;Event(topic = "TOPIC") Channel&lt;Integer&gt; channel)
 *     {
 *         ...
 *     }
 * </pre>
 * Consumer methods must also define which event topic they are interested in:
 * <pre>
 * public class MyClass
 * {
 *     &#64;Consumes(topic = "TOPIC1") public void push1(Integer event)
 *     {
 *         ...
 *     }
 *     &#64;Consumes(topic = "TOPIC2") public void push1(Integer event)
 *     {
 *         ...
 *     }
 *     ...
 * }
 * </pre>
 * 
 * <h4>Events of generic types</h4>
 * GUTS-Events, thanks to Guice, also supports events of generic types, by using Guice
 * {@link com.google.inject.TypeLiteral}:
 * <pre>
 * Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
 * {
 *     &#64;Override protected void configure()
 *     {
 *         Events.bindChannel(binder(), new TypeLiteral&lt;List&lt;Integer&gt;&gt;(){});
 *     }
 * });
 * </pre>
 * And it is directly injected as simply as before:
 * <pre>
 *     &#64;Inject Supplier(Channel&lt;List&lt;Integer&gt;&gt; channel)
 *     {
 *         ...
 *     }
 * </pre>
 * Event consumer methods are also straightforward:
 * <pre>
 * public class MyClass
 * {
 *     &#64;Consumes public void push(List&lt;Integer&gt; event)
 *     {
 *         ...
 *     }
 *     ...
 * }
 * </pre>
 * 
 * <h4>Primitive types of events</h4>
 * GUTS-Events also supports events of primitive types (e.g. {@code boolean}, 
 * {@code int}...), although the injected matching {@code Channel}s must be 
 * parameterized with the wrapper types (e.g. {@code Boolean}, {@code Integer}...):
 * <pre>
 * Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
 * {
 *     &#64;Override protected void configure()
 *     {
 *         Events.bindChannel(binder(), int.class);
 *     }
 * });
 * </pre>
 * In order to differentiate {@code Channel<Integer>} for {@code Integer} and
 * {@code int}, you can once again use {@link net.guts.event.Event} annotation:
 * <pre>
 *     &#64;Inject Supplier(&#64;Event(primitive = true) Channel&lt;Integer&gt; channel)
 *     {
 *         ...
 *     }
 * </pre>
 * Consumer methods take the primitive type as their sole argument:
 * <pre>
 * public class MyClass
 * {
 *     &#64;Consumes public void push(int event)
 *     {
 *         ...
 *     }
 *     ...
 * }
 * </pre>
 * 
 * <h4>Several consumers in the same class</h4>
 * In GUTS-Events, one class can have several consumer methods for different types
 * of events or even for the same type:
 * <pre>
 * public class MyClass
 * {
 *     &#64;Consumes public void push1(Integer event)
 *     {
 *         ...
 *     }
 *     &#64;Consumes public void push2(Integer event)
 *     {
 *         ...
 *     }
 *     &#64;Consumes public void push3(String event)
 *     {
 *         ...
 *     }
 *     ...
 * }
 * </pre>
 * In this snippet, for every {@code Integer} event, both {@code push1} and 
 * {@code push2} consumer methods will be called, whereas {@code push3} will be 
 * called for {@code String} events.
 * 
 * <p/><a name="guts3"></a><h3>Event filtering</h3>
 * <h4>Filter methods</h4>
 * Classes with {@code @Consumer}-annotated methods can also have 
 * {@link net.guts.event.Filters}-annotated methods that take as sole argument
 * an event and return a {@code boolean}. These methods get called prior to
 * notifying the matching consumer methods, in order to determine if these
 * consumer methods should get called for that event or not:
 * <pre>
 * public class MyClass
 * {
 *     &#64;Consumes public void push(Integer event)
 *     {
 *         ...
 *     }
 *     &#64;Filters public boolean filters(Integer event)
 *     {
 *         ...
 *     }
 *     ...
 * }
 * </pre>
 * In this snippet, every new {@code Integer} event is first passed to the 
 * {@code filters} method, and if {@code filters} returns {@code true} then
 * this event will be passed to the {@code push} method.
 * <p/>
 * Why would you need an extra method for filtering events rather than deciding
 * whether to process an event directly inside a consumer method? This is
 * in fact useful when using "Thread Policy" annotations in consumer methods, 
 * indicating that the consumer method should be run in a specific 
 * {@link java.lang.Thread}; contrarily to consumer methods, filter 
 * methods are always executed in the same {@code Thread} as the one used by a
 * supplier to send an event.
 * 
 * <h4>Automatic filtering based on argument type</h4>
 * GUTS-Events also supports automatic filtering of events based on their actual
 * type as in the following example:
 * <pre>
 * public class MyClass
 * {
 *     &#64;Consumes(type = Number.class) public void push(Integer event)
 *     {
 *         ...
 *     }
 *     ...
 * }
 * </pre>
 * In this example, {@code push} method is getting notified of events of type
 * {@code Number}, only if their actual type is {@code Integer} ({@code Integer}
 * is a subtype of {@code Number}); this means that only events sent through
 * {@code Channel<Number>} (not through {@code Channel<Integer>}) can be received
 * by {@code push}. More information can be found in the API documentation for
 * {@link net.guts.event.Consumes}.
 * <p/>
 * Now what about generic types? Suppose you want to get notified of events of type
 * {@code Set<Integer>} but you are only interested in {@code SortedSet<Integer>}:
 * <pre>
 * public class MyClass
 * {
 *     &#64;Consumes(type = Set&lt;Integer&gt;.class) public void push(SortedSet&lt;Integer&gt; event)
 *     {
 *         ...
 *     }
 *     ...
 * }
 * </pre>
 * The above snippet doesn't compile because {@code Set<Integer>.class} is not 
 * legal Java, you need to write as follows instead:
 * <pre>
 * public class MyClass
 * {
 *     &#64;Consumes(type = Set.class) public void push(SortedSet&lt;Integer&gt; event)
 *     {
 *         ...
 *     }
 *     ...
 * }
 * </pre>
 * GUTS-Events will take care of linking the {@code push} consumer method above to
 * the correct channel, {@code Channel<Set<Integer>>}, and not {@code Channel<Set>},
 * but only events of actual type {@code SortedSet<Integer>} will get passed to the
 * {@code push} method.
 * <p/>
 * Even filter methods can use this automatic filtering (which operates as a 
 * pre-filter for the filter method itself).
 * 
 * <h4>Linking filters and consumers</h4>
 * As mentioned in the previous section, it is possible to have, in the same class,
 * several consumer methods for the same event; but you may want to have several 
 * consumer methods for the same event, each with its own associated filter method,
 * this is possible by explicitly associating a consumer method with a filter method
 * through {@link net.guts.event.Consumes#filterId()} and
 * {@link net.guts.event.Filters#id()} respectively:
 * <pre>
 * public class MyClass
 * {
 *     &#64;Consumes(filterId = "FILTER1") public void push1(Integer event) {...}
 *     &#64;Consumes(filterId = "FILTER2") public void push2(Integer event) {...}
 *     &#64;Filters(id = "FILTER1" public boolean filters1(Integer event) {...}
 *     &#64;Filters(id = "FILTER2") public boolean filters2(Integer event) {...}
 * }
 * </pre>
 * The snippet above shows how to link consumer method {@code push1} with filter
 * method {@code filters1}, and consumer {@code push2} with filter {@code filters2}.
 * <p/>
 * Note that filter method {@code id}s are in a local "namespace" i.e. they are
 * local to the class where they are declared.
 * 
 * <p/><a name="guts4"></a><h3>Controlling priority of consumers notification</h3>
 * Sometimes you have several consumer methods (in several instances or classes) for 
 * the same event but you need one called before the other. By default, with 
 * GUTS-Events, you cannot predict the order in which consumer methods are called for
 * the same event. However, you can set an optional 
 * {@link net.guts.event.Consumes#priority()} to each consumer method to force some
 * order in calling consumer methods; when you don't specify it, {@code priority}
 * defaults to {@code 0}.
 * <pre>
 * public class MyClass
 * {
 *     &#64;Consumes(priority = 1) public void push1(Integer event)
 *     {
 *         ...
 *     }
 *     &#64;Consumes(priority = 2) public void push2(Integer event)
 *     {
 *         ...
 *     }
 * }
 * </pre>
 * In this example, {@code push1} will always be called before {@code push2} for 
 * every {@code Integer} event.
 * <p/>
 * Note that priorities respect is guaranteed only under certain situations, as 
 * described in the next section.
 * 
 * <p/><a name="guts5"></a><h3>Controlling thread executing consumers notification</h3>
 * By default, when a supplier sends an event, all matching consumer methods get
 * called by GUTS-Events in the same {@link java.lang.Thread} as the supplier.
 * <p/>
 * You can, however, change this behavior, easily; GUTS-Events provides a mechanism
 * to define what {@link java.lang.Thread} -or more precisely, what 
 * {@link java.util.concurrent.Executor}- will be in charge of calling a consumer 
 * method; this mechanism is called <b>"Thread Policy"</b>. Which thread policy 
 * should be used for a given consumer method is based on specific annotations put
 * on that method; default behavior requires no annotation.
 * <p/>
 * In addition to default behavior, GUTS-Events pre-defines two Thread Policies 
 * accessible by the following annotations (follow the javadoc links below for 
 * further details):
 * <ul>
 * <li>{@link net.guts.event.InDeferredThread}</li>
 * <li>{@link net.guts.event.InEDT}</li>
 * </ul>
 * <p/>
 * Note that filter methods are not impacted by thread policies on consumer 
 * methods, they are always executed on the same thread as the supplier.
 * <p/>
 * <b>Important</b>: consumer priorities (see {@link net.guts.event.Consumes#priority()})
 * are not fully guaranteed when using different Thread Policies for a set of 
 * consumer methods. Calling order is guaranteed only within the same Thread Policy.
 * <p/>
 * GUTS-Events also allows you to provide your own Thread Policies. For instance,
 * you can use Java 5 {@link java.util.concurrent.ThreadPoolExecutor} or your own
 * implementation {@link java.util.concurrent.Executor} and link it to a new 
 * annotation to be added to consumer methods (by calling 
 * {@link net.guts.event.Events#bindExecutor}). This is demonstrated below:
 * <pre>
 * // Definition of the thread policy annotation
 * &#64;Retention(RetentionPolicy.RUNTIME) &#64;Target(ElementType.METHOD)
 * public &#64;interface InPooledThread {}
 * 
 * // Usage in a consumer method
 * &#64;Consumes &#64;InPooledThread public void push(String event) {...}
 * 
 * // Bind the Executor to the thread policy annotation in a Module
 * &#64;Override protected void configure()
 * {
 *     Events.bindExecutor(binder(), InPooledThread.class)
 *         .toInstance(new ThreadPoolExecutor(...));
 * }
 * </pre>
 * 
 * <p/><a name="guts6"></a><h3>Adding special processing to consumer returned values</h3>
 * Until now, all examples have shown consumer methods that return {@code void},
 * but what if a consumer method returned something?
 * <p/>
 * GUTS-Events allows you to add special processing of values returned by
 * consumer methods, based on the declared return type of these methods.
 * <p/>
 * For this, you have to implement the generic interface
 * {@link net.guts.event.ConsumerReturnHandler} for the chosen return type and 
 * then add a "binding" between the return type and your {@code ConsumerReturnHandler}
 * implementation for that type; this binding can be done in any of your Guice
 * {@link com.google.inject.Module}s by calling, from the {@code configure} method, 
 * {@link net.guts.event.Events#bindHandler}.
 * <p/>
 * A concrete example of this feature is given 
 * {@linkplain net.guts.event.ConsumerReturnHandler here}.
 * 
 * <p/><a name="guts7"></a><h3>Handling exceptions thrown by consumers</h3>
 * By default, GUTS-Events swallows any exception thrown by filter or consumer
 * methods, but you can change this behavior by specifying your own
 * {@link net.guts.event.ConsumerExceptionHandler}:
 * <pre>
 * // Specific ConsumerExceptionHandler
 * public class LoggingConsumerExceptionHandler implements ConsumerExceptionHandler
 * {
 *     static final private Logger _logger =
 *         Logger.getLogger(LoggingConsumerExceptionHandler.class.getName());
 *     public void handleException(Throwable e, Method method, Object instance,
 *         Type eventType, String topic)
 *     {
 *         String message = String.format(
 *             "Consumer method %s.%s of object %s for event type %s and topic '%s'",
 *             method.getDeclaringClass().getName(), method.getName(), instance.toString(),
 *             eventType, topic);
 *         _logger.log(Level.WARNING, message, e);
 *     }
 * }
 * 
 * // Add binding when in a Module
 * Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
 * {
 *     &#64;Override protected void configure()
 *     {
 *         bind(ConsumerExceptionHandler.class).to(LoggingConsumerExceptionHandler.class);
 *     }
 * });
 * </pre>
 * The example above simply logs all exceptions thrown by consumer methods.
 * 
 * <p/><a name="guts8"></a><h3>Handling configuration errors</h3>
 * When processing a class containing {@code @Consumes} and {@code @Filters}
 * annotations (which is done automatically when instantiating such a class through
 * a Guice {@link com.google.inject.Injector}), in case of any error, GUTS-Events 
 * will, by default, throw {@link java.lang.IllegalArgumentException}. 
 * You may, if you need, change this behavior by specifying your own 
 * {@link net.guts.event.ErrorHandler}. As usual, you'll have to add a new Guice 
 * binding in your module:
 * <pre>
 * Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
 * {
 *     &#64;Override protected void configure()
 *     {
 *         bind(ErrorHandler.class).to(MyErrorHandler.class);
 *     }
 * });
 * </pre>
 * 
 * <p/><a name="guts9"></a><h3>Miscellaneous</h3>
 * In GUTS-Events, all reference to instances of classes containing consumer methods
 * are {@link java.lang.ref.WeakReference}s, which means that your code is responsible
 * to hold a strong references to them as long as you want them to receive events
 * notification. If you use Guice to instantiate these classes as 
 * {@link com.google.inject.Singleton}, then Guice will hold strong references on your
 * behalf.
 */
package net.guts.event;

