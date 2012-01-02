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

package net.guts.gui.message;

import net.guts.common.injection.AbstractSingletonModule;
import net.guts.gui.resource.ResourceModule;
import net.guts.gui.resource.Resources;

/**
 * Guice {@link com.google.inject.Module} for Guts-GUI Message Box factory.
 * This module must be added to the list of modules passed to 
 * {@link com.google.inject.Guice#createInjector}:
 * <pre>
 * Injector injector = Guice.createInjector(new MessageModule(), ...);
 * </pre>
 * If you use Guts-GUI {@link net.guts.gui.application.AbstractApplication}, then
 * {@code MessageModule} is automatically added to the list of {@code Module}s used
 * by Guts-GUI to create Guice {@code Injector}.
 * <p/>
 * Hence you would care about {@code MessageModule} only if you intend to use 
 * Guts-GUI Message Box factory but don't want to use the whole 
 * Guts-GUI framework.
 * <p/>
 * {@code MessageModule} uses {@link ResourceModule} and binds special
 * {@code ResourceConverter}s for new enumerated types {@link MessageType} and
 * {@link OptionType}, so that their names can be directly used in resource
 * bundles, as described in {@link MessageFactory} documentation.
 *
 * @author Jean-Francois Poilpret
 */
public final class MessageModule extends AbstractSingletonModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		install(new ResourceModule());
		// Bind special ResourceConverter used by MessageFactoryImpl
		Resources.bindEnumConverter(binder(), MessageType.class);
		Resources.bindEnumConverter(binder(), OptionType.class);
	}
}
