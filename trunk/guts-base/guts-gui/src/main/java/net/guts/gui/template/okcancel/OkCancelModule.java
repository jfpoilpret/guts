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

package net.guts.gui.template.okcancel;

import java.awt.LayoutManager;

import net.guts.common.injection.AbstractSingletonModule;
import net.guts.gui.action.ActionModule;
import net.guts.gui.application.GutsGuiResource;
import net.guts.gui.resource.Resources;
import net.guts.gui.template.TemplateModule;
import net.java.dev.designgridlayout.DesignGridLayoutManager;

/**
 * Guice {@link com.google.inject.Module} for Guts-GUI "OKCancel" templating 
 * subsystem. 
 * This module must be added to the list of modules passed to 
 * {@link com.google.inject.Guice#createInjector}:
 * <pre>
 * Injector injector = Guice.createInjector(new OkCancelModule(), ...);
 * </pre>
 * <p/>
 * {@code OkCancelModule} makes sure that you can use {@link OkCancel} configuration 
 * object to {@link net.guts.gui.window.WindowController#show show} a view that you
 * want decorated with "OK", "Cancel" or "Apply" buttons.
 * <p/>
 * If {@code DesignGridLayout} is present in the classpath, then a specific
 * {@link OkCancelLayoutAdder} will be bound for it.
 * <p/>
 * Note that {@code OkCancelModule} automatically ensures that {@link TemplateModule},
 * on which it depends, is also installed, hence you don't need to explicitly add
 * {@code TemplateModule} to the list of {@code Module}s used to create a Guice
 * {@code Injector}.
 * 
 * @author jfpoilpret
 */
public final class OkCancelModule extends AbstractSingletonModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		install(new TemplateModule());
		install(new ActionModule());
		OkCancelLayouts.layouts(binder());
		if (isDesignGridLayoutInClasspath())
		{
			OkCancelLayouts.bindLayout(binder(), DesignGridLayoutManager.class)
				.to(OkCancelDesignGridLayoutAdder.class);
		}
		OkCancelLayouts.bindLayout(binder(), LayoutManager.class)
			.to(OkCancelDefaultLayoutAdder.class);
		// Bind resources for OkCancel to Guts-provided resource bundle
		Resources.bindPackageBundles(binder(), OkCancelActions.class, GutsGuiResource.PATH);
	}
	
	static boolean isDesignGridLayoutInClasspath()
	{
		try
		{
			Class.forName("net.java.dev.designgridlayout.DesignGridLayoutManager");
			return true;
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
	}
}
