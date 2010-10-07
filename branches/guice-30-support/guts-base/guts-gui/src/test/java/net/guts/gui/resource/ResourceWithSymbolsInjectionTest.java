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

package net.guts.gui.resource;

import org.fest.assertions.Assertions;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "itest")
public class ResourceWithSymbolsInjectionTest
{
	static private final String NAME = ResourceWithSymbolsInjectionTest.class.getSimpleName();
	
	public void checkSymbolDefinedAndUsedInRootBundle()
	{
		ResourceInjector injector = createInjector(false);
		InjectableComponent component = new InjectableComponent();
		injector.injectInstance(component, NAME + "-checkSymbolDefinedAndUsedInRootBundle");
		
		// Check injection has worked
		Assertions.assertThat(component.getProperty()).as("component.property").isEqualTo("VALUE-SYMBOL");
	}
	
	public void checkSymbolDefinedInRootAndUsedInOtherBundle()
	{
		ResourceInjector injector = createInjector(true);
		InjectableComponent component = new InjectableComponent();
		injector.injectInstance(component, NAME + "-checkSymbolDefinedInRootAndUsedInOtherBundle");
		
		// Check injection has worked
		Assertions.assertThat(component.getProperty()).as("component.property").isEqualTo("VALUE-SYMBOL");
	}

	public void checkMultiLevelSymbolIndirection()
	{
		ResourceInjector injector = createInjector(false);
		InjectableComponent component = new InjectableComponent();
		injector.injectInstance(component, NAME + "-checkMultiLevelSymbolIndirection");
		
		// Check injection has worked
		Assertions.assertThat(component.getProperty()).as("component.property").isEqualTo("VALUE-SOMETHING-ELSE");
	}
	
	static private ResourceInjector createInjector(final boolean addBundle)
	{
		Injector injector = Guice.createInjector(new ResourceModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				Resources.bindRootBundle(binder(), "/net/guts/gui/resource/symbols-resources");
				if (addBundle)
				{
					Resources.bindClassBundles(binder(), InjectableComponent.class, 
						"ResourceWithSymbolsInjectionTest");
				}
			}
		});
		ResourceInjector resourceInjector = injector.getInstance(ResourceInjector.class);
		return resourceInjector;
	}

	static public class InjectableComponent
	{
		public String getProperty()
		{
			return _property;
		}

		public void setProperty(String property)
		{
			_property = property;
		}

		private String _property;
	}
}
