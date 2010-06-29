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

package net.guts.gui.application.docking;

import org.flexdock.docking.DockingStrategy;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;

public final class Docking
{
	private Docking()
	{
	}
	
	static public LinkedBindingBuilder<DockingStrategy> bindStrategy(
		Binder binder, Class<?> view)
	{
		return strategies(binder).addBinding(view);
	}
	
	static MapBinder<Class<?>, DockingStrategy> strategies(Binder binder)
	{
		return MapBinder.newMapBinder(binder, CLASS_TYPE, STRATEGY_TYPE);
	}

	static public LinkedBindingBuilder<PerspectiveInitializer> bindPerspective(
		Binder binder, String id)
	{
		return perspectives(binder).addBinding(id);
	}
	
	static MapBinder<String,PerspectiveInitializer> perspectives(Binder binder)
	{
		return MapBinder.newMapBinder(binder, String.class, PerspectiveInitializer.class);
	}

	static final private TypeLiteral<Class<?>> CLASS_TYPE = 
		new TypeLiteral<Class<?>>(){};
	static final private TypeLiteral<DockingStrategy> STRATEGY_TYPE = 
		TypeLiteral.get(DockingStrategy.class);
}
