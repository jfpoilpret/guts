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

import com.google.inject.ImplementedBy;
import com.google.inject.TypeLiteral;

/**
 * Service to lookup a {@link ResourceConverter} implementation, given a type.
 * <p/>
 * You normally would use this service in your own {@code ResourceConverter}s,
 * when they need access to other {@code ResourceConverter}s.
 * In such a case though, you might rather consider deriving from 
 * {@link AbstractCompoundResourceConverter} class instead because it already
 * includes methods to lookup {@code ResourceConverter}s.
 *
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(ResourceConverterFinderImpl.class)
public interface ResourceConverterFinder
{
	/**
	 * Finds the {@code ResourceConverter} for a given {@code type}.
	 * 
	 * @param <T> the type for which you want the matching {@code ResourceConverter<T>}
	 * @param type the type for which you want the matching {@code ResourceConverter<T>}
	 * @return the {@code ResourceConverter<T>} matching {@code type}, or {@code null}
	 * if there is no {@code ResourceConverter} for {@code type}
	 */
	public <T> ResourceConverter<T> getConverter(TypeLiteral<T> type);
}
