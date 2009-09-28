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

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fest.assertions.Assertions;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "itest")
public class ResourceInjectorTest
{
	static final private String NAME = ResourceInjectorTest.class.getSimpleName();
	
	public void checkTextInjectionOneComponent()
	{
		JLabel label = createAndInjectLabel("test1-label");
		// Check injection has worked
		Assertions.assertThat(label.getText()).as("label.text").isEqualTo("LABEL-TEXT");
		Assertions.assertThat(label.getToolTipText()).as("label.toolTipText").isEqualTo("LABEL-TOOLTIP");
	}
	
	public void checkBooleanInjectionOneComponent()
	{
		JLabel label = createAndInjectLabel("test2-label");
		// Check injection has worked
		Assertions.assertThat(label.isEnabled()).as("label.enabled").isFalse();
	}
	
	public void checkIntInjectionOneComponent()
	{
		JLabel label = createAndInjectLabel("test3-label");
		// Check injection has worked
		Assertions.assertThat(label.getIconTextGap()).as("label.iconTextGap").isEqualTo(2);
	}
	
	public void checkColorInjectionOneComponent()
	{
		JLabel label = createAndInjectLabel("test4-label");
		// Check injection has worked
		Assertions.assertThat(label.getForeground()).as("label.foreground").isEqualTo(new Color(0x4080C0));
	}
	
	public void checkFontInjectionOneComponent()
	{
		JLabel label = createAndInjectLabel("test5-label");
		// Check injection has worked
		Assertions.assertThat(label.getFont()).as("label.font").isEqualTo(Font.decode("Dialog-BOLD-14"));
	}
	
//	label.setCursor(cursor);
//	label.setIcon(icon);
	//TODO test component injection of Icon
	//TODO test component injection of Cursor
	//TODO test mnemonics handling for text injection
	//TODO test component injection of Alpha Color

	public void checkInjectionHierarchy()
	{
		ResourceInjector injector = createInjector();
		JPanel panel = new JPanel();
		panel.setName(NAME + "-hierarchy");
		JLabel label1 = createLabel("hierarchy-label1");
		panel.add(label1);
		JLabel label2 = createLabel("hierarchy-label2");
		panel.add(label2);
		JLabel label3 = createLabel("hierarchy-label3");
		panel.add(label3);
		injector.injectHierarchy(panel);
		// Check injection has worked on every component in panel
		Assertions.assertThat(panel.getForeground()).as("panel.foreground").isEqualTo(new Color(0x4080C0));
		Assertions.assertThat(label1.getText()).as("label1.text").isEqualTo("LABEL1");
		Assertions.assertThat(label2.getText()).as("label2.text").isEqualTo("LABEL2");
		Assertions.assertThat(label3.getText()).as("label3.text").isEqualTo("LABEL3");
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
				Resources.bindRootBundle(binder(), ResourceInjectorTest.class);
			}
		});
		ResourceInjector resourceInjector = injector.getInstance(ResourceInjector.class);
		return resourceInjector;
	}
}
