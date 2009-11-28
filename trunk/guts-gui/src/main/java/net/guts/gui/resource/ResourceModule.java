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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.util.Locale;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import net.guts.event.EventModule;
import net.guts.event.Events;
import net.guts.gui.util.CursorInfo;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * Guice {@link com.google.inject.Module} for Guts-GUI Resource Injection system. 
 * This module must be added to the list of modules passed to 
 * {@link com.google.inject.Guice#createInjector}:
 * <pre>
 * Injector injector = Guice.createInjector(new ResourceModule(), ...);
 * </pre>
 * If you use Guts-GUI {@link net.guts.gui.application.AbstractAppLauncher}, then
 * {@code ResourceModule} is automatically added to the list of {@code Module}s used
 * by Guts-GUI to create Guice {@code Injector}.
 * <p/>
 * Hence you would care about {@code ResourceModule} only if you intend to use 
 * Guts-GUI Resource Injection system but don't want to use the whole Guts-GUI 
 * framework.
 * <p/>
 * By default, {@code ResourceModule} binds several {@link ResourceConverter}s for
 * various types (examples of values are given for each type):
 * <ul>
 * <li>{@link String}: {@code Save}</li>
 * <li>{@link Boolean} and {@code boolean}: {@code true} or {@code false}</li>
 * <li>{@link Integer} and {@code integer}: {@code 12345}, {@code -10}, 
 * {@code 0xDEADBEEF} (hexa), {@code #FFFF}, {@code 0777} (octal)</li>
 * <li>{@link Color}: {@code #FF808080} (ARGB format in hexa)</li>
 * <li>{@link Font}: {@code Dialog-BOLD-14}</li>
 * <li>{@link Icon}: {@code /icons/save.png}</li>
 * <li>{@link Image}: {@code /icons/save.png}</li>
 * <li>{@link Cursor}: {@code CROSSHAIR} (or any value of 
 * {@link net.guts.gui.util.CursorType}), or for custom cursors, 
 * {@code /icons/mycursor.png,0.5,0.5} where both double values indicate the relative
 * location of the cursor "hot spot" (0.0 is topmost or leftmost, 1.0 is bottom-most 
 * or rightmost)</li>
 * <li>{@link CursorInfo}: same examples as above</li>
 * </ul>
 * You can add your own {@link ResourceConverter}s by using {@link Resources#bindConverter}
 * methods.
 * <p/>
 * Besides, {@code ResourceModule} binds various {@link InstanceInjector}s for the
 * following types:
 * <ul>
 * <li>{@link Component}: used by default for all AWT and Swing components</li>
 * <li>{@link Object}: used by default for non GUI objects</li>
 * <li>{@link JLabel}: special injector for {@code JLabel}s, with mnemonic support</li>
 * <li>{@link AbstractButton}: special injector for {@code JButton}, {@code JMenu},
 * {@code JMenuItem}... with mnemonic support</li>
 * <li>{@link JTable}: special injector for titles of {@code JTable} column headers</li>
 * <li>{@link JTabbedPane}: special injector with support for mnemonics, tooltips, 
 * and icons for tabs inside a {@code JTabbedPane}</li>
 * <li>{@link JFileChooser}: injector for {@code JFileChooser}, with special support
 * of its "accept" button</li>
 * </ul>
 * You can add support for special components (such as 3rd-party widgets) by writing
 * your own {@link InstanceInjector} and bind it through 
 * {@link Resources#bindInstanceInjector}.
 *
 * @author Jean-Francois Poilpret
 */
public final class ResourceModule extends AbstractModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		// Make sure EventModule is installed so that we can define Channel<Locale>
		install(new EventModule());
		Events.bindChannel(binder(), Locale.class);
		
		// Bind ResourceConverters for various default types: String, boolean, int, Color...
		bindConverter(String.class,		StringConverter.class);
		bindConverter(Boolean.class,	BooleanConverter.class);
		bindConverter(boolean.class,	BooleanConverter.class);
		bindConverter(Integer.class,	IntConverter.class);
		bindConverter(int.class,		IntConverter.class);
		bindConverter(Color.class,		ColorConverter.class);
		bindConverter(Font.class,		FontConverter.class);
		bindConverter(Icon.class,		IconConverter.class);
		bindConverter(Image.class,		ImageConverter.class);
		bindConverter(CursorInfo.class,	CursorInfoConverter.class);
		bindConverter(Cursor.class,		CursorConverter.class);

		// Bind default ComponentInjector
		bindInjector(Component.class, 
			new TypeLiteral<BeanPropertiesInjector<Component>>(){});
		bindInjector(Object.class, 
			new TypeLiteral<BeanPropertiesInjector<Object>>(){});
		// Bind injectors for more specific components
		bindInjector(JLabel.class,			JLabelInjector.class);
		bindInjector(AbstractButton.class,	AbstractButtonInjector.class);
		bindInjector(JTable.class,			JTableInjector.class);
		bindInjector(JTabbedPane.class,		JTabbedPaneInjector.class);
		bindInjector(JFileChooser.class,	JFileChooserInjector.class);

		// Initialize MapBinder<Class<?>, List<String>> for bundles per class
		Resources.classBundlesMap(binder());
		// Initialize MapBinder<String, List<String>> for bundles per package
		Resources.packageBundlesMap(binder());
	}
	
	private <T> void bindConverter(
		Class<T> type, Class<? extends ResourceConverter<T>> converter)
	{
		Resources.bindConverter(binder(), type).to(converter).asEagerSingleton();
	}
	
	private <T> void bindInjector(
		Class<T> type, Class<? extends InstanceInjector<T>> converter)
	{
		Resources.bindInstanceInjector(binder(), type).to(converter).asEagerSingleton();
	}

	private <T> void bindInjector(
		Class<T> type, TypeLiteral<? extends InstanceInjector<T>> converter)
	{
		Resources.bindInstanceInjector(binder(), type).to(converter).asEagerSingleton();
	}
}
