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

package net.guts.gui.dialog2.template;

import net.java.dev.designgridlayout.DesignGridLayoutManager;

import com.google.inject.AbstractModule;

public final class OkCancelModule extends AbstractModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
//		Dialogs.bindTemplate(binder(), OkCancelConfiguration.class).to(OkCancelTemplate.class);
		OkCancelLayouts.layouts(binder());
		OkCancelLayouts.bindLayout(binder(), DesignGridLayoutManager.class)
			.to(OkCancelDesignGridLayoutAdder.class);
//		bind(OkCancelButtonsPanelFactory.class).toProvider(FactoryProvider.newFactory(
//			OkCancelButtonsPanelFactory.class, OkCancelButtonsPanel.class));
//		String pack = "/" + TypeHelper.getPackagePath(AbstractApplication.class);
		// Provide default resource values for OK/Cancel/Apply actions
//		Resources.bindPackageBundles(binder(), OkCancelButtonsPanel.class, pack + "/guts-gui");
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
