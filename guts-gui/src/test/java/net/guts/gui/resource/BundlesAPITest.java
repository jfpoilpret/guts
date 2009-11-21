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

import javax.swing.JPanel;

import org.fest.assertions.Assertions;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "itest")
public class BundlesAPITest
{
	public void checkBindClassToOneBundle()
	{
		TestPanel1 panel = new TestPanel1("checkBindClassToOneBundle");
		injectPanel(panel, "checkBindClassToOneBundle");
		// Check injection has worked from all bundles
		Assertions.assertThat(panel.getProperty1()).as("checkBindClassToOneBundle.property1").isEqualTo("PROPERTY-1");
		Assertions.assertThat(panel.getProperty2()).as("checkBindClassToOneBundle.property2").isEqualTo("PROPERTY-2");
	}

	public void checkBindClassToSeveralBundles()
	{
		TestPanel1 panel = new TestPanel1("checkBindClassToSeveralBundles");
		injectPanel(panel, "checkBindClassToSeveralBundles1", "checkBindClassToSeveralBundles2");
		// Check injection has worked from all bundles
		Assertions.assertThat(panel.getProperty1()).as("checkBindClassToSeveralBundles.property1").isEqualTo("PROPERTY-1");
		Assertions.assertThat(panel.getProperty2()).as("checkBindClassToSeveralBundles.property2").isEqualTo("OVERRIDE-PROPERTY-2");
	}
	
	public void checkBindClassBundlesOverridesUsesBundles()
	{
		TestPanel2 panel = new TestPanel2("checkBindClassBundlesOverridesUsesBundles");
		injectPanel(panel, "checkBindClassBundlesOverridesUsesBundles");
		// Check injection has worked from all bundles
		Assertions.assertThat(panel.getProperty1()).as("checkBindClassBundlesOverridesUsesBundles.property1").isEqualTo("PROPERTY-1");
		Assertions.assertThat(panel.getProperty2()).as("checkBindClassBundlesOverridesUsesBundles.property2").isEqualTo("PROPERTY-2");
	}
	
	static private void injectPanel(JPanel panel, String... bundles)
	{
		ResourceInjector resourceInjector = createInjector(panel.getClass(), bundles);
		resourceInjector.injectComponent(panel);
	}
	
	static private ResourceInjector createInjector(
		final Class<?> type, final String... bundles)
	{
		Injector injector = Guice.createInjector(new ResourceModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				Resources.bindRootBundle(binder(), "/net/guts/gui/resource/resources2");
				Resources.bindClassBundles(binder(), type, bundles);
			}
		});
		ResourceInjector resourceInjector = injector.getInstance(ResourceInjector.class);
		return resourceInjector;
	}
	
	static public class TestPanel1 extends AbstractPanel
	{
		public TestPanel1(String name)
		{
			setName(name);
		}
	}

	@UsesBundles("never-used-bundle")
	static public class TestPanel2 extends AbstractPanel
	{
		public TestPanel2(String name)
		{
			setName(name);
		}
	}
}
