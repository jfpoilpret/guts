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

import net.guts.gui.resource.bundle1.Panel15;
import net.guts.gui.resource.bundle3.Panel31;
import net.guts.gui.resource.bundle3.Panel32;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "itest")
public class UsesBundleIssue11Test
{
	public void checkUsesBundlesOnClass()
	{
		Panel15 panel = new Panel15();
		injectPanel(panel);
		// Check injection has worked from the right class-named bundle
		Assertions.assertThat(panel.getProperty1()).as("panel15.property1").isEqualTo("PROPERTY-1");
		Assertions.assertThat(panel.getProperty2()).as("panel15.property2").isEqualTo("PROPERTY-2");
	}
	
	public void checkUsesBundlesOnPackage()
	{
		Panel31 panel = new Panel31();
		injectPanel(panel);
		// Check injection has worked from the right class-named bundle
		Assertions.assertThat(panel.getProperty1()).as("panel31.property1").isEqualTo("PROPERTY-1");
		Assertions.assertThat(panel.getProperty2()).as("panel31.property2").isEqualTo("PROPERTY-2");
	}
	
	public void checkUsesBundlesOnClassAndPackage()
	{
		Panel32 panel = new Panel32();
		injectPanel(panel);
		// Check injection has worked from the right class-named bundle
		Assertions.assertThat(panel.getProperty1()).as("panel32.property1").isEqualTo("PROPERTY-1");
		Assertions.assertThat(panel.getProperty2()).as("panel32.property2").isEqualTo("PROPERTY-2");
	}
	
	static private void injectPanel(JPanel panel)
	{
		ResourceInjector resourceInjector = createInjector();
		resourceInjector.injectComponent(panel);
	}
	
	static private ResourceInjector createInjector()
	{
		Injector injector = Guice.createInjector(new ResourceModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
//				Resources.bindRootBundle(binder(), "/net/guts/gui/resource/resources");
			}
		});
		ResourceInjector resourceInjector = injector.getInstance(ResourceInjector.class);
		return resourceInjector;
	}
}
