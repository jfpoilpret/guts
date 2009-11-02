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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.bean.UntypedProperty;
import net.guts.common.bean.UntypedPropertyFactory;
import net.guts.gui.resource.ResourceMap.Key;

import com.google.inject.Inject;

/**
 * Generic {@link InstanceInjector} implementation that can inject resources
 * into any java bean instances, by using normal property setters of these beans.
 * <p/>
 * If you need to implement a specific {@code InstanceInjector} for your own
 * classes, you should consider subclassing {@code BeanPropertiesInjector} and 
 * overriding some methods to deal with peculiarities of the class to inject.
 * <p/>
 * Guts-GUI {@link ResourceModule} defines a binding to an instance of 
 * {@code BeanPropertiesInjector<Object>} so that injection of an instance for
 * which no specific {@code InstanceInjector} can be found will fall back to that
 * general implementation.
 * <p/>
 * Note that, in the current version, {@code BeanPropertiesInjector} will inject
 * only bean properties that have a getter and setter, both public. This may be 
 * enhanced in a future version.
 * <p/>
 * Hereafter is a concrete example of a {@code BeanPropertiesInjector} subclass,
 * actually used inside Guts-GUI:
 * <pre>
 * class JLabelInjector extends BeanPropertiesInjector&lt;JLabel&gt;
 * {
 *     @Override protected boolean handleSpecialProperty(
 *         JLabel label, Key key, ResourceMap resources)
 *     {
 *         // Special handling for mnemonics
 *         if ("text".equals(key.name()))
 *         {
 *             String value = resources.getValue(key, String.class);
 *             MnemonicInfo info = MnemonicInfo.extract(value);
 *             // Set the property with the resource value
 *             label.setText(info.getText());
 *             label.setDisplayedMnemonic(info.getMnemonic());
 *             label.setDisplayedMnemonicIndex(info.getMnemonicIndex());
 *             return true;
 *         }
 *         return false;
 *     }
 * }
 * </pre>
 * 
 * @param <T> type that can be injected
 *
 * @author Jean-Francois Poilpret
 */
public class BeanPropertiesInjector<T> implements InstanceInjector<T>
{
	@Inject final void setPropertyFactory(UntypedPropertyFactory properties)
	{
		_properties = properties;
	}

	/**
	 * Iterates through every property, found in {@code resources}, matching the 
	 * {@code prefix} key; for each property, {@link #handleSpecialProperty} is first
	 * called, giving subclasses a chance, if they override that method, to perform
	 * specific injection of the given property. If ({@link #handleSpecialProperty}
	 * returns {@code false}, which means that the property was not injected, then
	 * {@link #injectProperty(Object, Key, ResourceMap)} is called to inject the 
	 * given property through bean property setters.
	 */
	@Override public final void inject(T component, String prefix, ResourceMap resources)
	{
		// For each injectable resource
		for (Key key: resources.keys(prefix))
		{
			if (!handleSpecialProperty(component, key, resources))
			{
				injectProperty(component, key, resources);
			}
		}
	}

	/**
	 * Override this method if there are properties that must be injected in a
	 * special way (ie, not through bean property setters).
	 * 
	 * @param component the object the property shall be injected to
	 * @param key the key to the property to be injected; use this key as an 
	 * argument to {@link ResourceMap#getValue} in order to retrieve the actual
	 * property value to be injected.
	 * @param resources the map of resources extracted from all resource bundles
	 * mapped to the class of {@code component}
	 * @return {@code true} if the property referenced by {@code key} was already
	 * injected by this method, or {@code false} if it needs to be injected through
	 * a bean property setter; by default, this method returns {@code false}, which
	 * means that all properties are injected through bean setters.
	 */
	protected boolean handleSpecialProperty(
		T component, ResourceMap.Key key, ResourceMap resources)
	{
		return false;
	}

	/**
	 * Injects the property from {@code resources} identified with the given 
	 * {@code key} into {@code component}.
	 * <p/>
	 * You can possibly override this method to change the way to inject a 
	 * property into an object. By default, the method uses bean introspection
	 * and reflection.
	 * <p/>
	 * You generally wouldn't need to override that method.
	 * 
	 * @param component the object the property shall be injected to
	 * @param key the key to the property to be injected; use this key as an 
	 * argument to {@link ResourceMap#getValue} in order to retrieve the actual
	 * property value to be injected.
	 * @param resources the map of resources extracted from all resource bundles
	 * mapped to the class of {@code component}
	 */
	protected void injectProperty(T component, Key key, ResourceMap resources)
	{
		// Check that this property exists
		UntypedProperty property = writableProperty(key.name(), component.getClass());
		if (property != null)
		{
			Class<?> type = property.type();
			// Get the value in the correct type
			Object value = resources.getValue(key, type);
			// Set the property with the resource value
			property.set(component, value);
		}
	}

	/**
	 * Looks for a bean property named {@code name }in {@code bean} class, and checks
	 * it is writable. If no property exists or it exists but is not writable, this 
	 * method logs a message (using {@linkplain SLF4J}) and returns {@code null}.
	 * 
	 * @param name the name of the bean property to look for
	 * @param bean the bean class in which to look for a writable property named 
	 * {@code name}
	 * @return an {@code UntypedProperty} mapping to the required property, which
	 * can then be used to set this property in a {@code bean} instance; 
	 * {@code null} if the property does not exist or is not writable.
	 */
	final protected UntypedProperty writableProperty(String name, Class<?> bean)
	{
		UntypedProperty property = _properties.property(name, bean);
		if (property == null)
		{
			_logger.debug("Property {} in class {} doesn't exist.", name, bean);
			return null;
		}
		if (!property.isWritable())
		{
			_logger.debug("Property {} in class {} isn't writable.", name, bean);
			return null;
		}
		return property;
	}
	
	/**
	 * Subclasses can use this {@link org.slf4j.Logger} to log problems they 
	 * encounter when injecting resources into an object. Logging level to use
	 * in such situation is normally {@code DEBUG}.
	 */
	final protected Logger _logger = LoggerFactory.getLogger(getClass());
	private UntypedPropertyFactory _properties;
}
