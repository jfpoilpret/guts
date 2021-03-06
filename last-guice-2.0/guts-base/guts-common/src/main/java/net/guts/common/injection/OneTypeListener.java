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

package net.guts.common.injection;

import net.guts.common.type.TypeHelper;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class OneTypeListener<T> implements TypeListener
{
	public OneTypeListener(Class<T> type, InjectionListener<T> listener)
	{
		this(TypeLiteral.get(type), listener);
	}
	
	public OneTypeListener(TypeLiteral<T> type, InjectionListener<T> listener)
	{
		_type = type;
		_listener = listener;
	}

	/* (non-Javadoc)
	 * @see com.google.inject.spi.TypeListener#hear(com.google.inject.TypeLiteral, com.google.inject.spi.TypeEncounter)
	 */
	@SuppressWarnings("unchecked") @Override 
	public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter)
	{
		// Check that type is assignable to _type
		if (TypeHelper.typeIsSubtypeOf(type, _type))
		{
			encounter.register((InjectionListener<? super I>) _listener);
		}
	}
	
	final private TypeLiteral<T> _type;
	final private InjectionListener<T> _listener;
}
