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

public final class Sessions
{
	private Sessions()
	{
	}

	static public void bindApplicationClass(Binder binder, Class<?> application)
	{
		binder.bind(CLASS_LITERAL).annotatedWith(BindSessionApplication.class)
			.toInstance(application);
	}
	
	@SuppressWarnings("unchecked") 
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
