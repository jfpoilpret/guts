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

package net.guts.binding;

import java.util.ArrayList;
import java.util.List;

import net.guts.properties.Bean;
import net.guts.properties.Property;

final public class GutsTableModelBuilder<B>
{
	static public <B> GutsTableModelBuilder<B> newTableModelFor(Class<B> clazz)
	{
		return new GutsTableModelBuilder<B>(clazz);
	}
	
	private GutsTableModelBuilder(Class<B> clazz)
	{
		_helper = Bean.create(clazz);
	}

	public B of()
	{
		return _helper.mock();
	}
	
	public <T> GutsTableModelBuilder<B> addProperty(T getter)
	{
		_properties.add(_helper.property(getter));
		return this;
	}
	
	public GutsTableModel<B> buildModel()
	{
		return new GutsTableModel<B>(_properties);
	}
	
	final private Bean<B> _helper;
	final private List<Property<B, ?>> _properties = new ArrayList<Property<B, ?>>();
}
