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

import net.guts.gui.resource.bundle1.Panel11;
import net.guts.gui.resource.bundle1.Panel12;
import net.guts.gui.resource.bundle1.Panel13;
import net.guts.gui.resource.bundle1.Panel14;
import net.guts.gui.resource.bundle2.Panel21;
import net.guts.gui.resource.bundle2.Panel22;
import net.guts.gui.resource.bundle2.Panel23;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "itest")
public class UsesBundleTest
{
	public void checkUsesBundlesOnClassNoOverride()
	{
		Panel11 panel = new Panel11();
		injectPanel(panel);
		// Check injection has worked from all bundles
		Assertions.assertThat(panel.getProperty1()).as("panel11.property1").isEqualTo("PROPERTY-1");
		Assertions.assertThat(panel.getProperty2()).as("panel11.property2").isEqualTo("PROPERTY-2");
	}
	
	public void checkUsesBundlesOnClassWithOverride()
	{
		Panel12 panel = new Panel12();
		injectPanel(panel);
		// Check injection has worked from all bundles in the right order
		Assertions.assertThat(panel.getProperty1()).as("panel12.property1").isEqualTo("PROPERTY-1-OVERRIDE");
		Assertions.assertThat(panel.getProperty2()).as("panel12.property2").isEqualTo("PROPERTY-2");
	}
	
	public void checkUsesBundlesOnPackageNoOverride()
	{
		Panel21 panel = new Panel21();
		injectPanel(panel);
		// Check injection has worked from all bundles
		Assertions.assertThat(panel.getProperty1()).as("panel21.property1").isEqualTo("PROPERTY-1");
		Assertions.assertThat(panel.getProperty2()).as("panel21.property2").isEqualTo("PROPERTY-2");
	}
	
	public void checkUsesBundlesOnPackageWithOverride()
	{
		Panel22 panel = new Panel22();
		injectPanel(panel);
		// Check injection has worked from all bundles in the right order
		Assertions.assertThat(panel.getProperty1()).as("panel22.property1").isEqualTo("PROPERTY-1-OVERRIDE");
		Assertions.assertThat(panel.getProperty2()).as("panel22.property2").isEqualTo("PROPERTY-2");
	}
	
	public void checkUsesBundlesOnPackageAndClass()
	{
		Panel23 panel = new Panel23();
		injectPanel(panel);
		// Check injection has worked from all bundles
		Assertions.assertThat(panel.getProperty1()).as("panel23.property1").isEqualTo("PROPERTY-1");
		Assertions.assertThat(panel.getProperty2()).as("panel23.property2").isEqualTo("PROPERTY-2");
	}
	
	public void checkMultipleUsesBundlesOnClassNoOverride()
	{
		AbstractPanel panel = new Panel13();
		injectPanel(panel);
		// Check injection has worked from all bundles
		Assertions.assertThat(panel.getProperty1()).as("panel13.property1").isEqualTo("PROPERTY-1");
		Assertions.assertThat(panel.getProperty2()).as("panel13.property2").isEqualTo("PROPERTY-2");
	}
	
	public void checkMultipleUsesBundlesOnClassWithOverride()
	{
		AbstractPanel panel = new Panel14();
		injectPanel(panel);
		// Check injection has worked from all bundles
		Assertions.assertThat(panel.getProperty1()).as("panel14.property1").isEqualTo("PROPERTY-1-OVERRIDE");
		Assertions.assertThat(panel.getProperty2()).as("panel14.property2").isEqualTo("PROPERTY-2");
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
				Resources.bindRootBundle(binder(), "/net/guts/gui/resource/resources");
			}
		});
		ResourceInjector resourceInjector = injector.getInstance(ResourceInjector.class);
		return resourceInjector;
	}
}
