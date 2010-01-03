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

package net.guts.gui.action.blocker;

import java.lang.annotation.Annotation;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;
import com.google.inject.multibindings.MapBinder;

public final class Blockers
{
	private Blockers()
	{
	}

	static public void bindInputBlocker(Binder binder, 
		Class<? extends InputBlocker> blocker, Class<? extends Annotation> annotation)
	{
		Provider<InputBlockerFactory> provider = 
			FactoryProvider.newFactory(InputBlockerFactory.class, blocker);
		binder.bind(InputBlockerFactory.class).annotatedWith(annotation).toProvider(provider);
		MapBinder<Class<? extends Annotation>, InputBlockerFactory> mapper =
			MapBinder.newMapBinder(binder, KEY_TYPE, VALUE_TYPE);
		mapper.addBinding(annotation).toProvider(provider);
	}
	
	static final public InputBlocker NO_BLOCKER = new AbstractInputBlocker(null)
	{
		@Override protected void setBlocking(boolean block)
		{
		}
	};
	
	static final private TypeLiteral<Class<? extends Annotation>> KEY_TYPE = 
		new TypeLiteral<Class<? extends Annotation>>(){};
	static final private TypeLiteral<InputBlockerFactory> VALUE_TYPE = 
		TypeLiteral.get(InputBlockerFactory.class);
}
