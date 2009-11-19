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

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

/**
 * Abstract {@link ResourceConverter} implementation, to be used as superclass for any
 * {@code ResourceConverter} that needs to use other types of {@code ResourceConverter}s.
 * <p/>
 * A typical example is {@code ListConverter<T>}, which is a 
 * {@code ResourceConverter<List<T>>}, and therefore needs access to a 
 * {@code ResourceConverter<T>} to perform its work.
 * 
 * @param <T> the type to which {@code this} converter can convert a {@code String}
 * value
 *
 * @author Jean-Francois Poilpret
 */
public abstract class AbstractCompoundResourceConverter<T> implements ResourceConverter<T>
{
	@Inject void setFinder(ResourceConverterFinder finder)
	{
		_finder = finder;
	}

	/**
	 * Finds the {@code ResourceConverter} for a given {@code type}.
	 * 
	 * @param <U> the type for which you want the matching {@code ResourceConverter<U>}
	 * @param type the type for which you want the matching {@code ResourceConverter<U>}
	 * @return the {@code ResourceConverter<U>} matching {@code type}, or {@code null}
	 * if there is no {@code ResourceConverter} for {@code type}
	 */
	final protected <U> ResourceConverter<U> converter(TypeLiteral<U> type)
	{
		return _finder.getConverter(type);
	}

	/**
	 * Finds the {@code ResourceConverter} for a given {@code type}.
	 * 
	 * @param <U> the type for which you want the matching {@code ResourceConverter<U>}
	 * @param type the type for which you want the matching {@code ResourceConverter<U>}
	 * @return the {@code ResourceConverter<U>} matching {@code type}, or {@code null}
	 * if there is no {@code ResourceConverter} for {@code type}
	 */
	final protected <U> ResourceConverter<U> converter(Class<U> type)
	{
		return _finder.getConverter(TypeLiteral.get(type));
	}

	private ResourceConverterFinder _finder;
}
