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

package net.guts.gui.window;

import javax.swing.RootPaneContainer;

import net.guts.gui.util.TypeSafeMap;

/**
 * This class acts as a placeholder for properties that are used by
 * {@link WindowController#show} and {@link WindowProcessor}s in order to
 * configure the {@link RootPaneContainer} to be displayed.
 * <p/>
 * This class cannot be instantiated directly, but must be created by
 * special builders, one for each possible subtype of {@link RootPaneContainer}:
 * <ul>
 * <li>{@link JAppletConfig}</li>
 * <li>{@link JDialogConfig}</li>
 * <li>{@link JFrameConfig}</li>
 * <li>{@link JInternalFrameConfig}</li>
 * </ul>
 * <p/>
 * The only class that can create a {@code RootPaneConfig} instance is 
 * {@link AbstractConfig}, which all builders derive from. {@link AbstractConfig}
 * is also the only way to set properties into {@code RootPaneConfig}.
 * <p/>
 * {@link WindowProcessor}s that receive a {@code RootPaneConfig} can get its
 * properties through {@link #get(String, Class)} and {@link #get(Class)}.
 * 
 * @param <T> the actual class implementing {@link RootPaneContainer}, ie one of
 * {@link javax.swing.JApplet}, {@link javax.swing.JDialog}, {@link javax.swing.JFrame},
 * {@link javax.swing.JInternalFrame} or {@link javax.swing.JWindow}; this genericity
 * enables type-safety and consistency between a given {@link RootPaneContainer}
 * instance and the necessary {@code RootPaneConfig} for that specific instance.
 *
 * @author Jean-Francois Poilpret
 */
public class RootPaneConfig<T extends RootPaneContainer>
{
	RootPaneConfig(TypeSafeMap properties)
	{
		_properties = properties;
	}
	
	/**
	 * Get the value of property named {@code key} and of type {@code type}.
	 * You can't have two properties named {@code key} for two different types.
	 * 
	 * @param <V> type of the value for property {@code key}
	 * @param key the name of the property which we want to get the value
	 * @param type type of the value for property {@code key}
	 * @return value for property {@code key}
	 */
	public <V> V get(String key, Class<V> type)
	{
		return _properties.get(key, type);
	}
	
	/**
	 * Get the value of property named {@code type.getName()} and of type {@code type}.
	 * This is a shortcut for {@code get(MyType.class.getName(), MyType.class)}.
	 * 
	 * @param <V> type of the value for property named {@code type.getName()}
	 * @param type type of the value for property {@code type.getName()}
	 * @return value for property of type {@code type} and name {@code type.getName()}
	 */
	public <V> V get(Class<V> type)
	{
		return _properties.get(type);
	}

	final private TypeSafeMap _properties;
}
