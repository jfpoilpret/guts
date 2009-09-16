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

import javax.swing.JComponent;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

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
		bindConverter(String.class, StringConverter.class);
		bindConverter(Integer.class, IntConverter.class);
		bindConverter(Color.class, ColorConverter.class);
		bindConverter(Font.class, FontConverter.class);

		// Bind default ComponentInjector
		Resources.bindComponentInjector(binder(), JComponent.class).to(BeanPropertiesInjector.class);
		// TODO bind injectors for more specific components

		//TODO
		// Add type listener for automatic injection of GUI
	}
	
	private <T> void bindConverter(
		Class<T> type, Class<? extends ResourceConverter<T>> converter)
	{
		Resources.bindConverter(binder(), type).to(converter).asEagerSingleton();
	}
}
