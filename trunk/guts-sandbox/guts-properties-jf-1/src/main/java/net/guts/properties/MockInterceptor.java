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

package net.guts.properties;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

class MockInterceptor implements MethodInterceptor
{
	MockInterceptor(PropertyDescriptor[] properties)
	{
		_properties = properties;
	}

	//CSOFF: IllegalThrows
	@Override public Object intercept(
		Object target, Method method, Object[] args, MethodProxy proxy) throws Throwable
	{
		// Check this is a getter
		for (PropertyDescriptor descriptor : _properties)
		{
			if (method.equals(descriptor.getReadMethod()))
			{
				RecordedGetters.pushProperty(descriptor);

				//  Return a new Bean mock when possible
				Class<?> returnType = descriptor.getPropertyType();
				if (Modifier.isFinal(returnType.getModifiers()))
				{
					//TODO avoid returning null if possible, eg try reflection to instantiate
					// a default instance...
					return null;
				}
				else
				{
					return Bean.create(returnType).mock();
				}
			}
		}

		// For any non-property call, we just don't care
		//TODO or should we throw an exception because it's a case of misuse?
		return null;
	}
	//CSON: IllegalThrows

	private final PropertyDescriptor[] _properties;
}
