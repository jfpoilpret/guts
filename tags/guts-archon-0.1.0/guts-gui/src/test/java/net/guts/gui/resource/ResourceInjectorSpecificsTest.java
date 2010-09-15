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
public class ResourceInjectorSpecificsTest
{
	public void checkInstallResourceModuleWithoutRootBundle()
	{
		Injector injector = Guice.createInjector(new ResourceModule());
		injector.getInstance(ResourceInjector.class);
	}

	public void checkInjectionThroughFields()
	{
		Injector injector = Guice.createInjector(new ResourceModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				Resources.bindRootBundle(binder(), "/net/guts/gui/resource/resources");
			}
		});
		ResourceInjector resourceInjector = injector.getInstance(ResourceInjector.class);
		InjectableClass instance = new InjectableClass();
		resourceInjector.injectInstance(instance, getClass().getSimpleName() + "-field-injection");
		Assertions.assertThat(instance.dummy).as("instance.dummy").isEqualTo("TEXT-INJECTED-INTO-FIELD");
	}
	
	static class InjectableClass
	{
		private String dummy = null;
	}
}
