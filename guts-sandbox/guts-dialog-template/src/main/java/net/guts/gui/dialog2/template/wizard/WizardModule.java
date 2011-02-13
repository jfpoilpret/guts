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

package net.guts.gui.dialog2.template.wizard;

import java.util.List;
import java.util.Map;

import net.guts.gui.application.GutsGuiResource;
import net.guts.gui.resource.Resources;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

public final class WizardModule extends AbstractModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		requestStaticInjection(Wizard.class);
		// Provide default resource values for OK/Cancel/Next/Previous actions
		Resources.bindPackageBundles(
			binder(), WizardDecorator.class, GutsGuiResource.PATH);
		//TODO move to guts-gui resources later on
		TypeLiteral<Map<String, String>> map = (TypeLiteral<Map<String, String>>)
			TypeLiteral.get(Types.mapOf(String.class, String.class));
		TypeLiteral<String> stringType = TypeLiteral.get(String.class); 
		Resources.bindConverter(binder(), map)
			.toInstance(new MapConverter<String, String>(stringType, stringType));
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
