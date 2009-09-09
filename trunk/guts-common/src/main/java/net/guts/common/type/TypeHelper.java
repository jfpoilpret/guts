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

package net.guts.common.type;

import com.google.inject.TypeLiteral;

/**
 * 
 *
 * @author Jean-Francois Poilpret
 */
public final class TypeHelper
{
	private TypeHelper()
	{
	}
	
	static public boolean typeIsSubtypeOf(
		TypeLiteral<?> subtype, TypeLiteral<?> supertype)
	{
		// First check that raw types are compatible
		// Then check that generic types are compatible! HOW????
		return (	subtype.equals(supertype)
			||	(	supertype.getRawType().isAssignableFrom(subtype.getRawType())
				&&	supertype.equals(subtype.getSupertype(supertype.getRawType()))));
	}
}
