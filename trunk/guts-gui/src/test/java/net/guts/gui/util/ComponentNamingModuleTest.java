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

package net.guts.gui.util;

import javax.swing.JLabel;
import javax.swing.JPanel;

import static org.fest.assertions.Assertions.assertThat;
import org.testng.annotations.Test;

import net.guts.common.injection.InjectionListeners;

import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "itest")
public class ComponentNamingModuleTest
{
	public void checkNamedPanelWithUnnamedChild()
	{
		NamedPanelWithUnnamedChild panel = getInstance(NamedPanelWithUnnamedChild.class);
		assertThat(panel.getName()).as("Panel Name").isEqualTo("NAME1");
		assertThat(panel.myLabel.getName()).as("Widget Name").isEqualTo("NAME1-myLabel");
	}
	static public class NamedPanelWithUnnamedChild extends JPanel
	{
		final public JLabel myLabel = new JLabel();
		public NamedPanelWithUnnamedChild()
		{
			setName("NAME1");
		}
	}

	public void checkNamedPanelWithNamedChild()
	{
		NamedPanelWithNamedChild panel = getInstance(NamedPanelWithNamedChild.class);
		assertThat(panel.getName()).as("Panel Name").isEqualTo("NAME2");
		assertThat(panel.myLabel.getName()).as("Widget Name").isEqualTo("NAME3");
	}
	static public class NamedPanelWithNamedChild extends JPanel
	{
		final public JLabel myLabel = new JLabel();
		public NamedPanelWithNamedChild()
		{
			setName("NAME2");
			myLabel.setName("NAME3");
		}
	}

	public void checkNamedPanelWithMixedChildren()
	{
		NamedPanelWithMixedChildren panel = getInstance(NamedPanelWithMixedChildren.class);
		assertThat(panel.getName()).as("Panel Name").isEqualTo("NAME4");
		assertThat(panel.myLabel1.getName()).as("Widget Name").isEqualTo("NAME5");
		assertThat(panel.myLabel2.getName()).as("Widget Name").isEqualTo("NAME4-myLabel2");
	}
	static public class NamedPanelWithMixedChildren extends JPanel
	{
		final public JLabel myLabel1 = new JLabel();
		final public JLabel myLabel2 = new JLabel();
		public NamedPanelWithMixedChildren()
		{
			setName("NAME4");
			myLabel1.setName("NAME5");
		}
	}

	public void checkUnnamedPanelWithUnnamedChild()
	{
		UnnamedPanelWithUnnamedChild panel = getInstance(UnnamedPanelWithUnnamedChild.class);
		String expected = panel.getClass().getSimpleName(); 
		assertThat(panel.getName()).as("Panel Name").isEqualTo(expected);
		assertThat(panel.myLabel.getName()).as("Widget Name").isEqualTo(expected + "-myLabel");
	}
	static public class UnnamedPanelWithUnnamedChild extends JPanel
	{
		final public JLabel myLabel = new JLabel();
	}

	public void checkUnnamedPanelWithNamedChild()
	{
		UnnamedPanelWithNamedChild panel = getInstance(UnnamedPanelWithNamedChild.class);
		String expected = panel.getClass().getSimpleName(); 
		assertThat(panel.getName()).as("Panel Name").isEqualTo(expected);
		assertThat(panel.myLabel.getName()).as("Widget Name").isEqualTo("NAME6");
	}
	static public class UnnamedPanelWithNamedChild extends JPanel
	{
		final public JLabel myLabel = new JLabel();
		public UnnamedPanelWithNamedChild()
		{
			myLabel.setName("NAME6");
		}
	}

	public void checkUnnamedPanelWithMixedChildren()
	{
		UnnamedPanelWithMixedChildren panel = getInstance(UnnamedPanelWithMixedChildren.class);
		String expected = panel.getClass().getSimpleName(); 
		assertThat(panel.getName()).as("Panel Name").isEqualTo(expected);
		assertThat(panel.myLabel1.getName()).as("Widget Name").isEqualTo("NAME7");
		assertThat(panel.myLabel2.getName()).as("Widget Name").isEqualTo(expected + "-myLabel2");
	}
	static public class UnnamedPanelWithMixedChildren extends JPanel
	{
		final public JLabel myLabel1 = new JLabel();
		final public JLabel myLabel2 = new JLabel();
		public UnnamedPanelWithMixedChildren()
		{
			myLabel1.setName("NAME7");
		}
	}

	public void checkNonComponentWithUnnamedChild()
	{
		NonComponentWithUnnamedChild panel = getInstance(NonComponentWithUnnamedChild.class);
		assertThat(panel.myLabel.getName()).as("Widget Name").isNull();
	}
	static public class NonComponentWithUnnamedChild
	{
		final public JLabel myLabel = new JLabel();
	}

	public void checkUnnamedPanelWithInheritedChild()
	{
		PanelWithInheritedChild panel = getInstance(PanelWithInheritedChild.class);
		String expected = panel.getClass().getSimpleName(); 
		assertThat(panel.getName()).as("Panel Name").isEqualTo(expected);
		assertThat(panel.myLabel1.getName()).as("Widget Name").isEqualTo(expected + "-myLabel1");
		assertThat(panel.myLabel2.getName()).as("Widget Name").isEqualTo(expected + "-myLabel2");
	}
	static public class PanelWithUnnamedChild extends JPanel
	{
		final public JLabel myLabel1 = new JLabel();
	}
	static public class PanelWithInheritedChild extends PanelWithUnnamedChild
	{
		final public JLabel myLabel2 = new JLabel();
	}

	// - test with a private field also
	public void checkUnnamedPanelWithUnnamedPrivateChild()
	{
		UnnamedPanelWithUnnamedPrivateChild panel = getInstance(UnnamedPanelWithUnnamedPrivateChild.class);
		String expected = panel.getClass().getSimpleName(); 
		assertThat(panel.getName()).as("Panel Name").isEqualTo(expected);
		assertThat(panel.myLabel.getName()).as("Widget Name").isEqualTo(expected + "-myLabel");
	}
	static public class UnnamedPanelWithUnnamedPrivateChild extends JPanel
	{
		final private JLabel myLabel = new JLabel();
	}

	static private <T> T getInstance(Class<T> clazz)
	{
		Injector injector = Guice.createInjector(new ComponentNamingModule());
		InjectionListeners.injectListeners(injector);
		return injector.getInstance(clazz);
	}
}
