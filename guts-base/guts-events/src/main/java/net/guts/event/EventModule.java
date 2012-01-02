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

import net.guts.common.injection.AbstractSingletonModule;
import net.guts.common.injection.InjectionListeners;
import net.guts.common.injection.Matchers;
import net.guts.common.injection.OneTypeListener;
import net.guts.event.internal.AnnotationProcessor;
import net.guts.event.internal.AnnotationProcessorFactory;
import net.guts.event.internal.ChannelFactory;
import net.guts.event.internal.ChannelImpl;
import net.guts.event.internal.ConsumerInjectionListener;
import net.guts.event.internal.InDeferredThreadExecutor;
import net.guts.event.internal.InEDTExecutor;

import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Guice {@link com.google.inject.Module} for GUTS-Events. This module must be added 
 * to the list of modules passed to {@link com.google.inject.Guice#createInjector}:
 * <pre>
 * Injector injector = Guice.createInjector(new EventModule(), ...);
 * InjectionListeners.injectListeners(injector);
 * </pre>
 * Please note the second line which must absolutely be called once {@code Injector}
 * has been created, otherwise event dispatching will not work.
 * <p/>
 * GUTS-Events has some default bindings that can be overridden in your own modules
 * if required:
 * <ul>
 * <li>{@link ConsumerExceptionHandler}: used to handle exceptions thrown by filter
 * and consumer methods during event notification; default behavior is to echo
 * information about the event and the exception to {@code System.err}.</li>
 * </ul>
 * In addition, this module also defines default bindings for Thread Policies
 * annotations, used during event dispatching:
 * <ul>
 * <li>{@link InDeferredThread}: a new thread is created from when events are 
 * dispatched to matching consumers</li>
 * <li>{@link InEDT}: events notification is performed from within <i>Swing 
 * Event-Dispatch Thread</i>.</li>
 * </ul>
 * <p/>
 * Make sure this module doesn't get added twice to the list of modules used to
 * create an {@link com.google.inject.Injector}.
 * 
 * @author Jean-Francois Poilpret
 */
final public class EventModule extends AbstractSingletonModule
{
	@Override protected void configure()
	{
		// Make sure @Consumes annotation get automatically processed for
		// any instance created by Guice
		ConsumerInjectionListener injectionListener = 
			InjectionListeners.requestInjection(binder(), new ConsumerInjectionListener());
		OneTypeListener<Object> typeListener = 
			new OneTypeListener<Object>(Object.class, injectionListener);
		bindListener(Matchers.hasPublicMethodAnnotatedWith(Consumes.class), typeListener);
		
		// Perform assisted inject for AnnotationProcessor
		install(new FactoryModuleBuilder()
			.implement(AnnotationProcessor.class, AnnotationProcessor.class)
			.build(AnnotationProcessorFactory.class));
		
		// Perform assisted inject for ChannelImpl
		install(new FactoryModuleBuilder()
			.implement(ChannelImpl.class, ChannelImpl.class)
			.build(ChannelFactory.class));

		// Initialize MultiMap to contain the dictionary of Executors to be
		// used when @Consumes method have the matching annotations
		// We bind all default Thread Policies supported by GUTS-Events
		Events.bindExecutor(binder(), InDeferredThread.class)
			.to(InDeferredThreadExecutor.class).in(Scopes.SINGLETON);
		Events.bindExecutor(binder(), InEDT.class)
			.to(InEDTExecutor.class).in(Scopes.SINGLETON);
		
		// Initialize empty MultiMap to contain handlers of results returned by consumers
		Events.getHandlerMap(binder());
	}
}
