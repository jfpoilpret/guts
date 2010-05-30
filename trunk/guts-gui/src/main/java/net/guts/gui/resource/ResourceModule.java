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
import javax.swing.KeyStroke;

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
 * If you use Guts-GUI {@link net.guts.gui.application.AbstractApplication}, then
 * {@code ResourceModule} is automatically added to the list of {@code Module}s used
 * by Guts-GUI to create Guice {@code Injector}.
 * <p/>
 * Hence you would care about {@code ResourceModule} only if you intend to use 
 * Guts-GUI Resource Injection system but don't want to use the whole Guts-GUI 
 * framework.
 * <p/>
 *
 * <h3>Standard ResourceConverters</h3>
 * <p>
 * By default, {@code ResourceModule} binds several {@link ResourceConverter}s for
 * various types (examples of values are given for each type):
 * </p>
 *
 * <table border="2">
 * <tr><th>Type                </th><th>Example</th></tr>
 * <tr><td>{@link String}      </td><td>{@code Save}</td></tr>
 * <tr><td>{@link Boolean} and
 *         {@code boolean}     </td><td>{@code true} or {@code false}</td></tr>
 * <tr><td>{@link Integer} and
 *         {@code integer}     </td><td>{@code 12345}, {@code -10}, {@code 0xDEADBEEF} (hexa),
 *                                      {@code #FFFF}, {@code 0777} (octal)</td></tr>
 * <tr><td>{@link Color}       </td><td>{@code #FF808080} (ARGB format in hexa)</td></tr>
 * <tr><td>{@link Font}        </td><td>{@code Dialog-BOLD-14}</td></tr>
 * <tr><td>{@link Icon}        </td><td>{@code /icons/save.png}</td></tr>
 * <tr><td>{@link Image}       </td><td>{@code /icons/save.png}</td></tr>
 * <tr><td>{@link Cursor}      </td><td>{@code CROSSHAIR} (or any value of
 *                             {@link net.guts.gui.util.CursorType}), or for custom cursors,<br>
 *                             {@code /icons/mycursor.png,0.5,0.5} where both double values
 *                             indicate the relative location of the cursor "hot spot"
 *                             (0.0 is topmost or leftmost, 1.0 is bottom-most
 *                             or rightmost)</td></tr>
 * <tr><td>{@link CursorInfo}  </td><td>same examples as above</td></tr>
 * </table>
 *
 * <p>
 * You can add your own {@link ResourceConverter}s by using {@link Resources#bindConverter}
 * methods.
 * <p/>

 * <h3>Standard InstanceInjectors</h3>
 * <p>
 * {@code ResourceModule} also binds various {@link InstanceInjector}s for the
 * following types:
 * </p>
 *
 * <table border="2">
 * <tr><th>Type</th><th>Description</th></tr>
 *
 * <tr>
 * <td>{@link Component}</td>
 * <td>Used by default for all AWT and Swing components. Injects any bean property from the
 *     given resources.</td>
 * </tr>
 *
 * <tr>
 * <td>{@link Object}</td>
 * <td>Used by default for non GUI objects. Injects any bean property from the
 *     given resources.</td>
 * </tr>
 *
 * <tr>
 * <td>{@link JLabel}</td>
 * <td>Special injector for {@code JLabel}s, with mnemonic support.
 *      <br>Sets the properties {@code text}, {@code displayedMnemonic} and
 *     {@code displayedMnemonicIndex} from a given {@code text} resource.
 *     <br>Resource Example: {@code myLabel.text = Hello &World}</td>
 * </tr>
 *
 * <tr>
 * <td>{@link AbstractButton}</td>
 * <td>Special injector for {@code JButton}, {@code JMenu},
 *     {@code JMenuItem}... with mnemonic support.
 *     <br>Sets the properties {@code text}, {@code mnemonic} and
 *     {@code displayedMnemonicIndex} from a given {@code text} resource.
 *     <br>Resource Example: {@code myButton.text = Push &Me}</td>
 * </tr>
 *
 * <tr>
 * <td>{@link JTable}</td>
 * <td>Special injector for titles of {@code JTable} column headers.
 *     <br>Resource Example: {@code myTable.header.1 = Column 1 Header}</td>
 * </tr>
 *
 * <tr>
 * <td>{@link JTabbedPane}</td>
 * <td>Special injector for tabs inside a {@code JTabbedPane}.
 * 	   <br>The format of a resource is:
 *         <code>tab&lt;&lt;index&gt;&gt;-&lt;&lt;resourceName&gt;&gt;</code>
 *     <br>Supported resources are:
 *         <ul>
 *         <li>{@code title} - sets title and mnemonics as described for JLabel</li>
 *         <li>{@code icon}</li>
 *         <li>{@code disabledIcon}</li>
 *         <li>{@code toolTipText}</li>
 *         </ul>
 * </td>
 * </tr>
 *
 * <tr>
 * <td>{@link JFileChooser}</td>
 * <td>Injector for {@code JFileChooser}, with special support
 * 	   of its "accept" button.
 *      <br>Sets the properties {@code approveButtonText} and {@code approveButtonMnemonic}
 *     from a given {@code approveButtonText} resource.
 *     <br>Resource Example: {@code myAttachmentFileChooser.approveButtonText = &Attach}</td>
 * </td>
 * </tr>
 * </table>
 *
 * <p>
 * You can add support for special components (such as 3rd-party widgets) by writing
 * your own {@link InstanceInjector} and bind it through 
 * {@link Resources#bindInstanceInjector}.
 * </p>
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
		bindConverter(KeyStroke.class,	KeyStrokeConverter.class);
		bindConverter(Color.class,		ColorConverter.class);
		bindConverter(Font.class,		FontConverter.class);
		bindConverter(Icon.class,		IconConverter.class);
		bindConverter(Image.class,		ImageConverter.class);
		bindConverter(CursorInfo.class,	CursorInfoConverter.class);
		bindConverter(Cursor.class,		CursorConverter.class);

		// Bind default ComponentInjector
		bindInjector(Component.class, COMPONENT_INJECTOR);
		bindInjector(Object.class, OBJECT_INJECTOR);

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
	
	@Override public boolean equals(Object other)
	{
		return other instanceof ResourceModule;
	}

	@Override public int hashCode()
	{
		return ResourceModule.class.hashCode();
	}

	static final private TypeLiteral<BeanPropertiesInjector<Component>> COMPONENT_INJECTOR =
		new TypeLiteral<BeanPropertiesInjector<Component>>(){};
	static final private TypeLiteral<BeanPropertiesInjector<Object>> OBJECT_INJECTOR =
		new TypeLiteral<BeanPropertiesInjector<Object>>(){};
}
