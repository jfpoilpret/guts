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

package net.guts.gui.dialog2.template.okcancel;

import java.awt.LayoutManager;

import net.guts.gui.action.ActionModule;
import net.guts.gui.application.GutsGuiResource;
import net.guts.gui.dialog2.template.TemplateModule;
import net.guts.gui.resource.Resources;
import net.java.dev.designgridlayout.DesignGridLayoutManager;

import com.google.inject.AbstractModule;

public final class OkCancelModule extends AbstractModule
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
		//TODO bind resources for OkCancel to Guts-provided resource bundle
		// This requires to have all actions in a class (currently they are built on the fly)
//		Resources.bindPackageBundles(
//			binder(), GutsApplicationActions.class, GutsGuiResource.PATH);
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
	
	@Override public boolean equals(Object other)
	{
		return other instanceof OkCancelModule;
	}

	@Override public int hashCode()
	{
		return OkCancelModule.class.hashCode();
	}
}
