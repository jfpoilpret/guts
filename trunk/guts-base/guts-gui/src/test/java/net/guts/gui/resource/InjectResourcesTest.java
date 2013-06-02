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

import java.util.Locale;

import org.fest.assertions.Assertions;
import org.testng.annotations.Test;

import net.guts.common.injection.InjectionListeners;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "itest")
public class InjectResourcesTest
{
	public void checkInjectionIntoDefaultAnnotatedInstance()
	{
		Injector injector = createInjector();
		InstanceWithDefaultPrefix instance = injector.getInstance(InstanceWithDefaultPrefix.class);
		Assertions.assertThat(instance.property).as("instance.property").isEqualTo("PROPERTY1");
	}

	public void checkInjectionIntoPrefixAnnotatedInstance()
	{
		Injector injector = createInjector();
		InstanceWithPrefix instance = injector.getInstance(InstanceWithPrefix.class);
		Assertions.assertThat(instance.property).as("instance.property").isEqualTo("PROPERTY2");
	}

	public void checkInjectionIntoAutoUpdateAnnotatedInstance()
	{
		Injector injector = createInjector();
		InstanceWithAutoUpdate instance = injector.getInstance(InstanceWithAutoUpdate.class);
		injector.getInstance(ResourceInjector.class).setLocale(Locale.ENGLISH);
		Assertions.assertThat(instance.property).as("instance.property").isEqualTo("PROPERTY3-DEFAULT");
		injector.getInstance(ResourceInjector.class).setLocale(Locale.FRENCH);
		Assertions.assertThat(instance.property).as("instance.property").isEqualTo("PROPERTY3-FRENCH");
	}

	static private Injector createInjector()
	{
		Injector injector = Guice.createInjector(new ResourceModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				Resources.bindRootBundle(binder(), "/net/guts/gui/resource/InjectResourcesAnnotation");
			}
		});
		InjectionListeners.injectListeners(injector);
		return injector;
	}

	@InjectResources
	static public class InstanceWithDefaultPrefix
	{
		public String property;
	}

	@InjectResources(prefix = "instance-prefix")
	static public class InstanceWithPrefix
	{
		public String property;
	}

	@InjectResources(autoUpdate = true)
	static public class InstanceWithAutoUpdate
	{
		public String property;
	}
}
