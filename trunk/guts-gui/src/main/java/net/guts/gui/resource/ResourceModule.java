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

import javax.swing.Icon;
import javax.swing.JTable;

import net.guts.gui.util.CursorInfo;

import com.google.inject.AbstractModule;

/**
 * TODO
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
		bindInjector(Component.class,	ComponentPropertiesInjector.class);
		//TODO Bind injectors for more specific components
		bindInjector(JTable.class,		JTableInjector.class);

		//TODO
		// Add type listener for automatic injection of GUI
	}
	
	private <T> void bindConverter(
		Class<T> type, Class<? extends ResourceConverter<T>> converter)
	{
		Resources.bindConverter(binder(), type).to(converter).asEagerSingleton();
	}
	
	private <T> void bindInjector(
		Class<T> type, Class<? extends ComponentInjector<T>> converter)
	{
		Resources.bindComponentInjector(binder(), type).to(converter).asEagerSingleton();
	}
}
