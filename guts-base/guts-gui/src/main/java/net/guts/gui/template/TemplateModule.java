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

package net.guts.gui.template;

import net.guts.common.injection.AbstractSingletonModule;
import net.guts.gui.window.WindowProcessor;
import net.guts.gui.window.Windows;

/**
 * Guice {@link com.google.inject.Module} for Guts-GUI templating system. 
 * This module must be added to the list of modules passed to 
 * {@link com.google.inject.Guice#createInjector}:
 * <pre>
 * Injector injector = Guice.createInjector(new TemplateModule(), ...);
 * </pre>
 * <p/>
 * {@code TemplateModule} makes sure that {@link TemplateDecorator} implementations
 * will be used by {@code net.guts.gui.window.WindowController#show} when needed.
 * 
 * @author jfpoilpret
 */
public final class TemplateModule extends AbstractSingletonModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		Windows.bindWindowProcessor(binder(), WindowProcessor.TEMPLATE_DECORATION)
			.to(WpTemplateDecoration.class);
	}
}
