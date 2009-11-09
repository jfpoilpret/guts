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

import javax.swing.JLabel;

import org.fest.assertions.Assertions;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "itest")
public class LocaleResourceTest
{
	static final private String NAME = LocaleResourceTest.class.getSimpleName();
	
	public void checkCorrectLocaleUsedIfPropertyAvailable()
	{
		Locale.setDefault(Locale.FRENCH);
		JLabel label = createAndInjectLabel("test1-label");
		// Check injection has worked
		Assertions.assertThat(label.getText()).as("label.text").isEqualTo("Label francais 1");
		Assertions.assertThat(label.getToolTipText()).as("label.toolTipText").isEqualTo("Tooltip francais 1");
	}
	
	public void checkDefaultLocaleUsedIfSpecificLocalePropertyUnavailable()
	{
		Locale.setDefault(Locale.FRENCH);
		JLabel label = createAndInjectLabel("test2-label");
		// Check injection has worked
		Assertions.assertThat(label.getText()).as("label.text").isEqualTo("Default Label 2");
		Assertions.assertThat(label.getToolTipText()).as("label.toolTipText").isEqualTo("Default Tooltip 2");
	}
	
	public void checkDefaultLocaleUsedIfSpecificLocaleFileUnavailable()
	{
		Locale.setDefault(Locale.GERMAN);
		JLabel label = createAndInjectLabel("test2-label");
		// Check injection has worked
		Assertions.assertThat(label.getText()).as("label.text").isEqualTo("Default Label 2");
		Assertions.assertThat(label.getToolTipText()).as("label.toolTipText").isEqualTo("Default Tooltip 2");
	}
	
	public void checkLanguageLocaleUsedIfCountryLocaleFileUnavailable()
	{
		Locale.setDefault(Locale.CANADA_FRENCH);
		JLabel label = createAndInjectLabel("test1-label");
		// Check injection has worked
		Assertions.assertThat(label.getText()).as("label.text").isEqualTo("Label francais 1");
		Assertions.assertThat(label.getToolTipText()).as("label.toolTipText").isEqualTo("Tooltip francais 1");
	}
	
	static private JLabel createLabel(String name)
	{
		JLabel label = new JLabel();
		label.setName(NAME + "-" + name);
		return label;
	}
	
	static private JLabel createAndInjectLabel(String name)
	{
		ResourceInjector resourceInjector = createInjector();
		JLabel label = createLabel(name);
		resourceInjector.injectComponent(label);
		return label;
	}
	
	static private ResourceInjector createInjector()
	{
		Injector injector = Guice.createInjector(new ResourceModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				Resources.bindRootBundle(binder(), "/net/guts/gui/resource/locale-test");
			}
		});
		ResourceInjector resourceInjector = injector.getInstance(ResourceInjector.class);
		return resourceInjector;
	}
}
