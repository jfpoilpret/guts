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

package net.guts.common.bean;

import com.google.inject.TypeLiteral;

//TODO Improve to make it type-safe somehow!
/**
 * TODO
 *
 * @author Jean-Francois Poilpret
 */
public class TypedProperty<T, V>
{
	TypedProperty(UntypedProperty property)
	{
		_property = property;
	}

	@SuppressWarnings("unchecked") 
	public TypeLiteral<? extends V> type()
	{
		return (TypeLiteral<? extends V>) _property.type();
	}
	
	public boolean isReadable()
	{
		return _property.isReadable();
	}
	
	public boolean isWritable()
	{
		return _property.isWritable();
	}
	
	public void set(T bean, V value)
	{
		_property.set(bean, value);
	}
	
	@SuppressWarnings("unchecked") 
	public V get(T bean)
	{
		return (V) _property.get(bean);
	}
	
	final private UntypedProperty _property;
}
