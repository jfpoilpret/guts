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
import static net.guts.common.type.PrimitiveHelper.toWrapper;

import com.google.inject.Inject;


/**
 * TODO
 *
 * @author Jean-Francois Poilpret
 */
abstract public class AbstractBeanInjector<T> implements ComponentInjector<T>
{
	@Inject void setPropertyFactory(UntypedPropertyFactory properties)
	{
		_properties = properties;
	}

	protected UntypedPropertyFactory properties()
	{
		return _properties;
	}

	protected UntypedProperty writableProperty(String name, Class<?> bean)
	{
		UntypedProperty property = _properties.property(name, bean);
		if (property == null || !property.isWritable())
		{
			String msg = String.format("Unknown property `%s` of type `%s`", name, bean);
			_logger.info(msg);
			return null;
		}
		return property;
	}
	
	protected void setProperty(T bean, String name, Object value)
	{
		Class<?> type = bean.getClass();
		UntypedProperty property = _properties.property(name, type);
		if (property == null || !property.isWritable())
		{
			//TODO log something
			return;
		}
		if (value != null && !toWrapper(property.type()).isAssignableFrom(value.getClass()))
		{
			//TODO log something
			return;
		}
		property.set(bean, value);
	}
	
	final protected Logger _logger = LoggerFactory.getLogger(getClass());
	private UntypedPropertyFactory _properties;
}
