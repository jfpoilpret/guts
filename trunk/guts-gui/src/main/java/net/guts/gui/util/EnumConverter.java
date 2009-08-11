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

import org.jdesktop.application.ResourceConverter;
import org.jdesktop.application.ResourceMap;

/**
 * General {@link ResourceConverter} to convert strings to enum values. This is
 * implicitly used by the Swing Application Framework whenever resource injection
 * is required. You need to register one instance per enum type to be converted:
 * <pre>
 * ResourceConverter.register(new EnumConverter<MessageType>(MessageType.class));
 * </pre>
 * Inside properties files, its usage is straightforward, you just need to put
 * the enum value:
 * <pre>
 * confirm-delete.messageType=Question
 * </pre>
 * Where {@link net.guts.gui.message.MessageType#Question} is one value of 
 * {@link net.guts.gui.message.MessageType} in this example.
 * <p/>
 * Note: {@link net.guts.gui.message.impl.DefaultMessageFactory} already 
 * registers {@code EnumConverter}s for {@link net.guts.gui.message.MessageType} and
 * {@link net.guts.gui.message.OptionType} so you don't need to register 
 * these by yourself.
 * 
 * @author Jean-Francois Poilpret
 * @param <T> the enum type to convert
 */
public class EnumConverter<T extends Enum<?>> extends ResourceConverter
{
	public EnumConverter(Class<T> type)
	{
		super(type);
		_enumValues = type.getEnumConstants();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jdesktop.application.ResourceConverter#parseString(java.lang.String, org.jdesktop.application.ResourceMap)
	 */
	@Override public Object parseString(String s, ResourceMap r)
	{
		for (T value: _enumValues)
		{
			if (value.toString().equals(s))
			{
				return value;
			}
		}
		return null;
	}
	
	final private T[] _enumValues;
}
