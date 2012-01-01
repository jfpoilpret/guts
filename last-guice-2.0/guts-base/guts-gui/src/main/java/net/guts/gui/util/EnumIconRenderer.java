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

import java.awt.Component;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.StringTokenizer;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.gui.resource.AbstractCompoundResourceConverter;
import net.guts.gui.resource.ResourceConverter;
import net.guts.gui.resource.ResourceEntry;
import net.guts.gui.resource.Resources;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * A special {@link javax.swing.table.TableCellRenderer} that will render any 
 * {@code enum} value to a given icon.
 * 
 * @param <T> the enum type for which values will be rendered by this 
 * {@code TableCellRenderer}
 *
 * @author Jean-Francois Poilpret
 */
public class EnumIconRenderer<T extends Enum<T>> extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 4287379175359009250L;

	/**
	 * Creates a {@code ResourceConverter<EnumIconRenderer<T>>} and binds this
	 * {@link ResourceConverter} to {@code EnumIconRenderer<T>} so that any
	 * {@link EnumIconRenderer} can have its resources injected by
	 * {@link net.guts.gui.resource.ResourceInjector}.
	 * <p/>
	 * For resource injection to work onto {@code EnumIconRenderer}, you need the 
	 * following:
	 * <ul>
	 * <li>define a {@code void setRenderer(EnumIconRenderer<YourEnum>)} in the
	 * injected component,</li>
	 * <li>define a binding for {@code ResourceConverter<YourEnum>} with
	 * {@link Resources#bindEnumConverter(Binder, Class)},</li>
	 * <li>call {@link #bind(Binder, Class)} for {@code YourEnum} type.</li>
	 * </ul>
	 * The format of the resource for {@code renderer} is as follows:
	 * <pre>
	 * ComponentName.renderer=EnumValue1:IconPath1,EnumValue2:IconPath2,EnumValue3:IconPath3...
	 * </pre>
	 * 
	 * @param <T> the {@code enum} type class for which to bind resource injection of
	 * the matching {@link EnumIconRenderer}
	 * @param binder the binder passed to {@link com.google.inject.Module#configure(Binder)}
	 * @param enumType the {@code enum} type class for which to bind resource injection of
	 * the matching {@link EnumIconRenderer}
	 */
	@SuppressWarnings("unchecked") 
	static public <T extends Enum<T>> void bind(Binder binder, Class<T> enumType)
	{
		Type rendererType = Types.newParameterizedType(EnumIconRenderer.class, enumType);
		TypeLiteral<EnumIconRenderer<T>> rendererLiteral =
			(TypeLiteral<EnumIconRenderer<T>>) TypeLiteral.get(rendererType);
		Resources.bindConverter(binder, rendererLiteral).toInstance(
			new EnumIconRendererResourceConverter<T>(enumType));
	}
	
	/**
	 * Creates a new {@code EnumIconRenderer} for a given {@code enum type}.
	 * 
	 * @param type the enum type for which to create a {@code EnumIconRenderer}
	 */
	public EnumIconRenderer(Class<T> type)
	{
		setHorizontalAlignment(CENTER);
		_icons = new EnumMap<T, Icon>(type);
	}

	/**
	 * Maps one {@code value} of enum type {@code T} to the given {@code icon}.
	 * 
	 * @param value the {@code T} value to be mapped
	 * @param icon the icon to be mapped to {@code value}
	 * @return {@code this EnumIconRenderer} for easier calls (fluent API)
	 */
	public EnumIconRenderer<T> mapIcon(T value, Icon icon)
	{
		_icons.put(value, icon);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override public Component getTableCellRendererComponent(JTable table, Object value, 
		boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setText("");
		setIcon(_icons.get(value));
		return this;
	}

	final private EnumMap<T, Icon> _icons;
}

class EnumIconRendererResourceConverter<T extends Enum<T>> 
	extends AbstractCompoundResourceConverter<EnumIconRenderer<T>>
{
	EnumIconRendererResourceConverter(Class<T> enumType)
	{
		_enumType = enumType;
	}
	
	/* (non-Javadoc)
	 * @see net.guts.gui.resource.ResourceConverter#convert(net.guts.gui.resource.ResourceEntry)
	 */
	@Override public EnumIconRenderer<T> convert(ResourceEntry entry)
	{
		// Get all required converters for the job: IconConverter, EnumConverter
		ResourceConverter<T> enumConverter = converter(_enumType);
		if (enumConverter == null)
		{
			_logger.error("No ResourceConverter for {}. Binding must be explicitly defined!", 
				_enumType);
			return null;
		}
		ResourceConverter<Icon> iconConverter = converter(Icon.class);
		// Create a new renderer for this type
		EnumIconRenderer<T> renderer = new EnumIconRenderer<T>(_enumType);
		// Tokenize in pairs (enum value:icon path,...)
		StringTokenizer tokenize = new StringTokenizer(entry.value(), ",");
		while (tokenize.hasMoreTokens())
		{
			String pair = tokenize.nextToken();
			int index = pair.indexOf(':');
			if (index == -1 || index == pair.length() - 1)
			{
				_logger.debug("Expected `enumValue:iconPath` for {} enum, but found: {}", 
					_enumType.getSimpleName(), pair);
			}
			else
			{
				String enumValue = pair.substring(0, index);
				String iconPath = pair.substring(index + 1);
				renderer.mapIcon(	enumConverter.convert(entry.derive(enumValue)),
									iconConverter.convert(entry.derive(iconPath)));
			}
		}
		return renderer;
	}
	
	static private final Logger _logger = 
		LoggerFactory.getLogger(EnumIconRendererResourceConverter.class);
	private final Class<T> _enumType;
}
