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

import javax.swing.JLabel;

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
		Injector injector = Guice.createInjector(new ResourceModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				Resources.bindRootBundle(binder(), ResourceInjectorTest.class);
			}
		});
		ResourceInjector resourceInjector = injector.getInstance(ResourceInjector.class);
		JLabel label = new JLabel();
		label.setName(NAME + "-test1-label");
		resourceInjector.injectComponent(label);
		// Check injection has worked
//		label.setForeground(fg);
//		label.setCursor(cursor);
//		label.setDisplayedMnemonicIndex(index);
//		label.setEnabled(true);
//		label.setFont(font);
//		label.setIcon(icon);
		Assertions.assertThat(label.getText()).as("label.text").isEqualTo("LABEL-TEXT");
		Assertions.assertThat(label.getToolTipText()).as("label.toolTipText").isEqualTo("LABEL-TOOLTIP");
	}
	
	//TODO test component injection of boolean
	//TODO test component injection of int
	//TODO test component injection of Color
	//TODO test component injection of Font
	//TODO test component injection of Icon
	//TODO test component injection of Cursor
	//TODO test mnemonics handling for text injection
	//TODO test hierarchy injection (text only)
}
