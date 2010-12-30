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

package net.guts.gui.window;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;

/**
 * Utility class to define, from within a Guice {@link com.google.inject.Module},
 * bindings for {@link WindowProcessor}s.
 *
 * @author Jean-Francois Poilpret
 */
public final class Windows
{
	private Windows()
	{
	}

	//TODO javadoc
	@SuppressWarnings("unchecked") 
	static public LinkedBindingBuilder<WindowProcessor> bindWindowProcessor(
		Binder binder, int callingOrder)
	{
		LinkedBindingBuilder builder = windowProcessors(binder).addBinding(callingOrder);
		return builder;
	}
	
	static MapBinder<Integer, WindowProcessor> windowProcessors(Binder binder)
	{
		return MapBinder.newMapBinder(binder, INTEGER_LITERAL, WINDOW_PROCESSOR_LITERAL);
	}
	
	static final private TypeLiteral<Integer> INTEGER_LITERAL = TypeLiteral.get(Integer.class);
	static final private TypeLiteral<WindowProcessor> WINDOW_PROCESSOR_LITERAL = 
		TypeLiteral.get(WindowProcessor.class);
}
