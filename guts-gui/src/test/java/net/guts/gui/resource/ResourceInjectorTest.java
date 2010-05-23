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
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

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
		Assertions.assertThat(label.getForeground()).as("label.foreground").isEqualTo(new Color(0x4080C0, true));
	}
	
	public void checkFontInjectionOneComponent()
	{
		JLabel label = createAndInjectLabel("test5-label");
		// Check injection has worked
		Assertions.assertThat(label.getFont()).as("label.font").isEqualTo(Font.decode("Dialog-BOLD-14"));
	}
	
	public void checkMnemonicInjectionOneComponent()
	{
		JLabel label = createAndInjectLabel("test6-label");
		// Check injection has worked
		Assertions.assertThat(label.getText()).as("label.text").isEqualTo("Save As...");
		Assertions.assertThat(label.getDisplayedMnemonic()).as("label.displayedMnemonic").isEqualTo(KeyEvent.VK_A);
		Assertions.assertThat(label.getDisplayedMnemonicIndex()).as("label.displayedMnemonicIndex")
			.isEqualTo(5);
	}
	
	public void checkIconInjectionOneComponent()
	{
		JLabel label = createAndInjectLabel("test7-label");
		// Check injection has worked
		Assertions.assertThat(label.getIcon()).as("label.icon").isNotNull();
		BufferedImage actual = createImageFromIcon(label.getIcon());
		BufferedImage expected = createImageFromIcon("net/guts/gui/resource/images/icon.jpg");
		Assertions.assertThat(actual).as("label.icon").isEqualTo(expected);

		label = createAndInjectLabel("test8-label");
		// Check injection has worked
		Assertions.assertThat(label.getIcon()).as("label.icon").isNotNull();
		actual = createImageFromIcon(label.getIcon());
		Assertions.assertThat(actual).as("label.icon").isEqualTo(expected);
	}
	
	public void checkCursorInjectionOneComponent()
	{
		JLabel label = createAndInjectLabel("test9-label");
		// Check injection has worked
		Assertions.assertThat(label.getCursor()).as("label.cursor").isNotNull();
		Assertions.assertThat(label.getCursor()).as("label.cursor")
			.isEqualTo(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}
	
	public void checkImageInjectionOneComponent()
	{
		ResourceInjector resourceInjector = createInjector();
		JFrame frame = new JFrame();
		frame.setName(NAME + "-" + "test-image");
		resourceInjector.injectComponent(frame);

		// Check injection has worked
		Assertions.assertThat(frame.getIconImage()).as("frame.iconImage").isNotNull();
		BufferedImage actual = null;
		actual = createImageFromImage(frame.getIconImage());
		BufferedImage expected = createImageFromIcon("net/guts/gui/resource/images/icon.jpg");
		Assertions.assertThat(actual).as("frame.iconImage").isEqualTo(expected);
	}
	
	public void checkJTableInjection()
	{
		ResourceInjector resourceInjector = createInjector();
		JTable table = new JTable();
		table.setModel(new DefaultTableModel(new Object[]{"","",""}, 0));
		table.setName(NAME + "-" + "test-jtable");
		resourceInjector.injectComponent(table);

		// Check injection has worked
		TableColumnModel model = table.getColumnModel();
		Assertions.assertThat(model.getColumn(0).getHeaderValue()).as("table.header0").isEqualTo("Column 0");
		Assertions.assertThat(model.getColumn(1).getHeaderValue()).as("table.header1").isEqualTo("Column 1");
		Assertions.assertThat(model.getColumn(2).getHeaderValue()).as("table.header2").isEqualTo("Column 2");
	}
	
	public void checkJTabbedPaneInjection()
	{
		ResourceInjector resourceInjector = createInjector();
		JTabbedPane tabs = new JTabbedPane();
		tabs.setName(NAME + "-" + "test-jtabbedpane");
		// Add tabs!
		tabs.add(new JPanel());
		tabs.add(new JPanel());
		resourceInjector.injectComponent(tabs);

		// Check injection has worked
		Assertions.assertThat(tabs.getTitleAt(0)).as("tabbedpane.tab0-title").isEqualTo("Tab 0");
		Assertions.assertThat(tabs.getTitleAt(1)).as("tabbedpane.tab1-title").isEqualTo("Tab 1");
		Assertions.assertThat(tabs.getToolTipTextAt(0)).as("tabbedpane.tab0-toolTipText").isEqualTo("Tooltip 0");
		Assertions.assertThat(tabs.getToolTipTextAt(1)).as("tabbedpane.tab1-toolTipText").isEqualTo("Tooltip 1");
		// Check mnemonics
		Assertions.assertThat(tabs.getMnemonicAt(0)).as("tabbedpane.tab0-mnemonic").isEqualTo(KeyEvent.VK_T);
		Assertions.assertThat(tabs.getDisplayedMnemonicIndexAt(0)).as("tabbedpane.tab0-displayedMnemonicIndex").isEqualTo(0);
		Assertions.assertThat(tabs.getMnemonicAt(1)).as("tabbedpane.tab1-mnemonic").isEqualTo(KeyEvent.VK_A);
		Assertions.assertThat(tabs.getDisplayedMnemonicIndexAt(1)).as("tabbedpane.tab1-displayedMnemonicIndex").isEqualTo(1);
		// Check icons
		BufferedImage actual = createImageFromIcon(tabs.getIconAt(0));
		BufferedImage expected = createImageFromIcon("net/guts/gui/resource/images/icon.jpg");
		Assertions.assertThat(actual).as("tabbedpane.tab0-icon").isEqualTo(expected);
		actual = createImageFromIcon(tabs.getDisabledIconAt(0));
		Assertions.assertThat(actual).as("tabbedpane.tab0-disabledIcon").isEqualTo(expected);
		Assertions.assertThat(tabs.getIconAt(1)).as("tabbedpane.tab1-icon").isNull();
		Assertions.assertThat(tabs.getDisabledIconAt(1)).as("tabbedpane.tab1-disabledIcon").isNull();
	}
	
	public void checkJFileChooserInjection()
	{
		ResourceInjector resourceInjector = createInjector();
		JFileChooser chooser = new JFileChooser();
		chooser.setName(NAME + "-" + "test-jfilechooser");
		resourceInjector.injectComponent(chooser);

		// Check injection has worked
		Assertions.assertThat(chooser.getDialogTitle()).as("filechooser.dialogTitle").isEqualTo("File Chooser Title");
		Assertions.assertThat(chooser.getApproveButtonToolTipText()).as("filechooser.approveButtonToolTipText").isEqualTo("Approve tooltip");
		Assertions.assertThat(chooser.getApproveButtonText()).as("filechooser.approveButtonText").isEqualTo("Choose File");
		Assertions.assertThat(chooser.getApproveButtonMnemonic()).as("filechooser.approveButtonMnemonic").isEqualTo(KeyEvent.VK_F);
	}
	
	public void checkEnumInjection()
	{
		ResourceInjector resourceInjector = createInjector();
		InjectableComponent component = new InjectableComponent();
		resourceInjector.injectInstance(component, NAME + "-" + "test-enum-component");
		
		// Check injection has worked
		Assertions.assertThat(component.getStatus()).as("component.status").isSameAs(Status.OK);
	}
	
	public void checkClassInjection()
	{
		ResourceInjector resourceInjector = createInjector();
		InjectableComponent component = new InjectableComponent();
		resourceInjector.injectInstance(component, NAME + "-" + "test-class-component");

		// Check injection has worked
		Assertions.assertThat(component.getKeyType()).as("component.keyType").isSameAs(String.class);
		Assertions.assertThat(component.getValueType()).as("component.valueType").isSameAs(JPanel.class);
	}
	
	public void checkListInjection()
	{
		ResourceInjector resourceInjector = createInjector();
		InjectableComponent component = new InjectableComponent();
		resourceInjector.injectInstance(component, NAME + "-" + "test-list-component");
		
		// Check injection has worked
		Assertions.assertThat(component.getStringsList()).as("component.stringsList").containsExactly("A", "B", "C", "D");
		Assertions.assertThat(component.getIconsList()).as("component.iconsList").isNotNull().hasSize(2);
		
		BufferedImage expected = createImageFromIcon("net/guts/gui/resource/images/icon.jpg");
		BufferedImage actual = createImageFromIcon(component.getIconsList().get(0));
		Assertions.assertThat(actual).as("component.iconsList[0]").isEqualTo(expected);
		actual = createImageFromIcon(component.getIconsList().get(1));
		Assertions.assertThat(actual).as("component.iconsList[1]").isEqualTo(expected);
	}
	
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
		Assertions.assertThat(panel.getForeground()).as("panel.foreground").isEqualTo(new Color(0x4080C0, true));
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
				Resources.bindEnumConverter(binder(), Status.class);
				Resources.bindClassConverter(binder(), Object.class);
				Resources.bindClassConverter(binder(), JComponent.class);
				Resources.bindListConverter(binder(), String.class, ":");
				Resources.bindListConverter(binder(), Icon.class, ":");
				Resources.bindRootBundle(binder(), "/net/guts/gui/resource/resources");
			}
		});
		ResourceInjector resourceInjector = injector.getInstance(ResourceInjector.class);
		return resourceInjector;
	}
	
	static private BufferedImage createImageFromIcon(String path)
	{
		URL url = Thread.currentThread().getContextClassLoader().getResource(path);
		return createImageFromIcon(new ImageIcon(url));
	}
	
	static private BufferedImage createImageFromIcon(Icon icon)
	{
		BufferedImage image = new BufferedImage(
			icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graf = image.createGraphics();
		try
		{
			icon.paintIcon(null, graf, 0, 0);
			return image;
		}
		finally
		{
			graf.dispose();
		}
	}

	static private BufferedImage createImageFromImage(Image source)
	{
		if (source instanceof BufferedImage)
		{
			return (BufferedImage) source;
		}
		BufferedImage image = new BufferedImage(
			source.getWidth(null), source.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graf = image.createGraphics();
		try
		{
			graf.drawImage(source, 0, 0, null);
			return image;
		}
		finally
		{
			graf.dispose();
		}
	}

	static enum Status
	{
		OK, 
		KO;
	}
	
	static private class InjectableComponent
	{
		public Class<?> getKeyType()
		{
			return _keyType;
		}
		public void setKeyType(Class<?> keyType)
		{
			_keyType = keyType;
		}
		public Class<? extends JComponent> getValueType()
		{
			return _valueType;
		}
		public void setValueType(Class<? extends JComponent> valueType)
		{
			_valueType = valueType;
		}
		public void setStringsList(List<String> stringList)
		{
			_stringsList = stringList;
		}
		public List<String> getStringsList()
		{
			return _stringsList;
		}
		public void setIconsList(List<Icon> iconsList)
		{
			_iconsList = iconsList;
		}
		public List<Icon> getIconsList()
		{
			return _iconsList;
		}
		public void setStatus(Status status)
		{
			_status = status;
		}
		public Status getStatus()
		{
			return _status;
		}
		private Status _status;
		private Class<?> _keyType;
		private Class<? extends JComponent> _valueType;
		private List<String> _stringsList;
		private List<Icon> _iconsList;
	}
}
