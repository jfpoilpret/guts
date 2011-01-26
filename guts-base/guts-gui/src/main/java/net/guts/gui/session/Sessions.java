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

package net.guts.gui.session;

import java.awt.Component;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;

/**
 * Utility class to define, from within a Guice {@link com.google.inject.Module},
 * bindings for {@link SessionState}s and to declare your main application class,
 * which {@link StorageMedium} will use to locate the preferences where to store
 * GUI state.
 *
 * @author Jean-Francois Poilpret
 */
public final class Sessions
{
	private Sessions()
	{
	}

	/**
	 * Defines the application class, which name will be used as the main location
	 * for preferences storage (including GUI Session State).
	 * 
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param application your main application class, which full name (including 
	 * packages) will determine the main location for preferences storage
	 */
	static public void bindApplicationClass(Binder binder, Class<?> application)
	{
		binder.bind(CLASS_LITERAL).annotatedWith(BindSessionApplication.class)
			.toInstance(application);
	}

	/**
	 * Initializes a binding between a given {@link java.awt.Component} type 
	 * and a matching {@link SessionState}.
	 * <p/>
	 * This is based on usual Guice EDSL for bindings:
	 * <pre>
	 * Sessions.bindSessionConverter(binder(), MyComponent.class)
	 *     .to(MyComponentState.class);
	 * </pre>
	 * Where, in the above snippet, {@code MyComponentState} implements 
	 * {@code SessionState<MyComponent>}.
	 * <p/>
	 * This must be called from {@link com.google.inject.Module#configure(Binder)}.
	 * 
	 * @param <T> component type to bind to a {@code SessionState<T>}
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param type component type to bind to a {@code SessionState<T>}
	 * @return a {@link com.google.inject.binder.LinkedBindingBuilder} to bind 
	 * component {@code type} to an {@link SessionState}
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	static public <T extends Component> LinkedBindingBuilder<SessionState<T>> 
		bindSessionConverter(Binder binder, Class<T> type)
	{
		LinkedBindingBuilder builder = converters(binder).addBinding(type);
		return builder;
	}
	
	static MapBinder<Class<?>, SessionState<?>> converters(Binder binder)
	{
		return MapBinder.newMapBinder(binder, CLASS_LITERAL, SESSION_LITERAL);
	}
	
	static final private TypeLiteral<Class<?>> CLASS_LITERAL = 
		new TypeLiteral<Class<?>>(){};
	static final private TypeLiteral<SessionState<?>> SESSION_LITERAL = 
		new TypeLiteral<SessionState<?>>(){};
}
