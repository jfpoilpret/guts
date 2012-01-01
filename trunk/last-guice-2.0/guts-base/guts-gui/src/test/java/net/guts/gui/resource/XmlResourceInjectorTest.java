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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "itest")
public class XmlResourceInjectorTest
{
	static final private String NAME = XmlResourceInjectorTest.class.getSimpleName();

	@BeforeMethod public void saveCurrentLocale()
	{
		_currentLocale = Locale.getDefault();
	}
	
	@AfterMethod public void restoreCurrentLocale()
	{
		Locale.setDefault(_currentLocale);
	}
	
	public void checkInjectionFromOneXmlBundle()
	{
		ResourceInjector resourceInjector = createInjector("xml-resources1");
		JLabel label = createLabel("checkInjectionFromOneXmlBundle-label");
		resourceInjector.injectComponent(label);
		// Check injection has worked
		Assertions.assertThat(label.getText()).as("label.text").isEqualTo("XML-LABEL");
	}

	public void checkXmlAndPropertiesBundleAcrossLocales()
	{
		Locale.setDefault(Locale.FRENCH);
		ResourceInjector resourceInjector = createInjector("xml-resources2");
		JLabel label = createLabel("checkXmlAndPropertiesBundleAcrossLocales-label");
		resourceInjector.injectComponent(label);
		// Check injection has worked
		Assertions.assertThat(label.getText()).as("label.text").isEqualTo("FR-LABEL-FROM-XML");
		Assertions.assertThat(label.getToolTipText()).as("label.toolTipText").isEqualTo("DEFAULT-TIP-FROM-PROPERTIES");
	}
	
	public void checkXmlBundleTakesPrecedenceOverProperties()
	{
		ResourceInjector resourceInjector = createInjector("xml-resources3");
		JLabel label = createLabel("checkXmlBundleTakesPrecedenceOverProperties-label");
		resourceInjector.injectComponent(label);
		// Check injection has worked
		Assertions.assertThat(label.getText()).as("label.text").isEqualTo("XML-LABEL");
	}

	
	static private JLabel createLabel(String name)
	{
		JLabel label = new JLabel();
		label.setName(NAME + "-" + name);
		return label;
	}
	
	static private ResourceInjector createInjector(final String bundle)
	{
		Injector injector = Guice.createInjector(new ResourceModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				Resources.bindRootBundle(binder(), "/net/guts/gui/resource/" + bundle);
			}
		});
		ResourceInjector resourceInjector = injector.getInstance(ResourceInjector.class);
		return resourceInjector;
	}

	private Locale _currentLocale;
}
