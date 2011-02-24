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

package net.guts.gui.template.wizard;

import net.guts.gui.application.GutsGuiResource;
import net.guts.gui.resource.Resources;
import net.guts.gui.template.TemplateModule;

import com.google.inject.AbstractModule;

/**
 * Guice {@link com.google.inject.Module} for Guts-GUI "Wizard" templating 
 * subsystem. 
 * This module must be added to the list of modules passed to 
 * {@link com.google.inject.Guice#createInjector}:
 * <pre>
 * Injector injector = Guice.createInjector(new WizardModule(), ...);
 * </pre>
 * <p/>
 * {@code WizardModule} makes sure that you can use {@link Wizard} configuration 
 * object to {@link net.guts.gui.window.WindowController#show show} a wizard
 * dialog with a standard look throughout the whole application.
 * <p/>
 * Note that {@code WizardModule} automatically ensures that {@link TemplateModule},
 * on which it depends, is also installed, hence you don't need to explicitly add
 * {@code TemplateModule} to the list of {@code Module}s used to create a Guice
 * {@code Injector}.
 * 
 * @author jfpoilpret
 */
public final class WizardModule extends AbstractModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		install(new TemplateModule());
		requestStaticInjection(Wizard.class);
		// Provide default resource values for OK/Cancel/Next/Previous actions
		Resources.bindPackageBundles(
			binder(), WizardDecorator.class, GutsGuiResource.PATH);
		Resources.bindMapConverter(binder(), String.class, String.class);
	}
	
	@Override public boolean equals(Object other)
	{
		return other instanceof WizardModule;
	}

	@Override public int hashCode()
	{
		return WizardModule.class.hashCode();
	}
}
