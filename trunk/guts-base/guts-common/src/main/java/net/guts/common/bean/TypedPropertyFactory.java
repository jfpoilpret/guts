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

import com.google.inject.ImplementedBy;
import com.google.inject.TypeLiteral;

/**
 * TODO
 *
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(TypedPropertyFactoryImpl.class)
public interface TypedPropertyFactory
{
	public <T> TypedProperty<T, ?> property(String name, Class<T> bean);
	public <T, V> TypedProperty<T, V> property(String name, Class<T> bean, Class<V> type);
//	public <T, V> Property<T, V> property(String name, Class<T> bean, TypeLiteral<V> type);
}
