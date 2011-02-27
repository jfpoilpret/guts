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

import net.guts.common.type.TypeSafeMap;

import com.google.inject.TypeLiteral;

/**
 * Parent class of all {@link RootPaneConfig} builders, {@code AbstractConfig} is the
 * only class that can instantiate {@code RootPaneConfig} with properties set by its
 * subclasses.
 * <p/>
 * If you need to change one of the default builders (among {@link JAppletConfig},
 * {@link JDialogConfig}, {@link JFrameConfig} and {@link JInternalFrameConfig}),
 * then you should subclass {@code AbstractConfig}.
 * <p/>
 * Here is an excerpt of {@link JAppletConfig} that shows how to use {@code AbstractConfig}:
 * <pre>
 * public class JAppletConfig extends AbstractConfig&lt;JApplet, JAppletConfig&gt;
 * {
 *     private JAppletConfig()
 *     {
 *         set(StatePolicy.class, StatePolicy.RESTORE_IF_EXISTS);
 *     }
 *     
 *     static public JAppletConfig create()
 *     {
 *         return new JAppletConfig();
 *     }
 *     
 *     public JAppletConfig state(StatePolicy state)
 *     {
 *         return set(StatePolicy.class, state);
 *     }
 * }
 * </pre>
 * By convention, all {@link RootPaneConfig} builders:
 * <ul>
 * <li>must have a {@code private} constructor where reasonable defaults are set
 * for every property that the matching {@code RootPaneConfig<T>} must support</li>
 * <li>must have a {@code static create()} method that returns a new instance of 
 * themselves</li>
 * <li>use fluent API to set properties that will eventually be added to the 
 * {@code RootPaneConfig} produced by {@link #config()}</li>
 * </ul>
 * 
 * @param <T> the type of {@link RootPaneContainer} supported by the built
 * {@link RootPaneConfig}
 * @param <U> the type of {@code AbstractConfig} subclass
 *
 * @author Jean-Francois Poilpret
 */
abstract 
public class AbstractConfig<T extends RootPaneContainer, U extends AbstractConfig<T, U>>
{
	//TODO javadoc for all public/protected methods
	protected AbstractConfig()
	{
	}
	
	public RootPaneConfig<T> config()
	{
		return new RootPaneConfig<T>(_properties);
	}
	
	final public U merge(AbstractConfig<? super T, ?> config)
	{
		_properties.putAll(config._properties, false);
		return realThis();
	}
	
	final protected <V> U set(Class<V> type, V value)
	{
		_properties.put(type, value);
		return realThis();
	}

	final protected <V> U set(TypeLiteral<V> type, V value)
	{
		_properties.put(type, value);
		return realThis();
	}

	final protected <V> U set(String key, Class<V> type, V value)
	{
		_properties.put(key, type, value);
		return realThis();
	}
	
	final protected <V> U set(String key, TypeLiteral<V> type, V value)
	{
		_properties.put(key, type, value);
		return realThis();
	}
	
	@SuppressWarnings("unchecked") 
	final protected U realThis()
	{
		return (U) this;
	}

	final private TypeSafeMap _properties = new TypeSafeMap();
}
