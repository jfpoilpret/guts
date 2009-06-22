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

import net.guts.event.internal.AnnotationProcessor;
import net.guts.event.internal.AnnotationProcessorFactory;
import net.guts.event.internal.ConsumerInjectionListener;
import net.guts.event.internal.ConsumerTypeListener;
import net.guts.event.internal.InCurrentThreadExecutor;
import net.guts.event.internal.InDeferredThreadExecutor;
import net.guts.event.internal.InEDTExecutor;
import net.guts.event.internal.Matchers;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;

final public class EventModule extends AbstractModule
{
	@Override protected void configure()
	{
		// Make sure @Consumes annotation get automatically processed for
		// any instance created by Guice
		ConsumerInjectionListener injectionListener = new ConsumerInjectionListener();
		requestInjection(injectionListener);
		ConsumerTypeListener typeListener = new ConsumerTypeListener(injectionListener);
		bindListener(Matchers.consumer(), typeListener);
		
		// Perform assisted inject for AnnotationProcessor
		bind(AnnotationProcessorFactory.class).toProvider(FactoryProvider.newFactory(
			AnnotationProcessorFactory.class, AnnotationProcessor.class));
		
		// Initialize MultiMap to contain the dictionary of Executors to be
		// used when @Consumes method have the matching annotations
		// We bind all default Thread Policies supported by GUTS-Events
		Events.bindExecutor(binder(), InCurrentThread.class, new InCurrentThreadExecutor());
		Events.bindExecutor(binder(), InDeferredThread.class, new InDeferredThreadExecutor());
		Events.bindExecutor(binder(), InEDT.class, new InEDTExecutor());
	}
}
