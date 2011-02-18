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

package net.guts.gui.template.okcancel;

import java.awt.LayoutManager;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;

/**
 * Utility class to define, from within a Guice {@link com.google.inject.Module},
 * bindings for {@link OkCancelLayoutAdder}s.
 * <p/>
 * {@link OkCancelLayoutAdder}s allow {@link OkCancel} template to "optimize"
 * the addition of buttons to a view, based on the {@link LayoutManager} that
 * handles this view.
 * 
 * @author jfpoilpret
 **/
public final class OkCancelLayouts
{
	private OkCancelLayouts()
	{
	}
	
	/**
	 * Initializes a binding between a given {@link LayoutManager} subtype
	 * and a matching {@link OkCancelLayoutAdder}.
	 * <p/>
	 * This is based on usual Guice EDSL for bindings:
	 * <pre>
	 * OkCancelLayouts.bindLayout(binder(), MigLayout.class)
	 *     .to(OkCancelMigLayoutAdder.class);
	 * </pre>
	 * <p/>
	 * This must be called from {@link com.google.inject.Module#configure(Binder)}.
	 * 
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param layout {@link LayoutManager} type to bind to a {@link OkCancelLayoutAdder}
	 * @return a {@link com.google.inject.binder.LinkedBindingBuilder} to bind 
	 * {@code layout} to an {@link OkCancelLayoutAdder}
	 */
	static public LinkedBindingBuilder<OkCancelLayoutAdder> bindLayout(
		Binder binder, Class<? extends LayoutManager> layout)
	{
		return layouts(binder).addBinding(layout);
	}
	
	static MapBinder<Class<? extends LayoutManager>, OkCancelLayoutAdder> layouts(
		Binder binder)
	{
		return MapBinder.newMapBinder(binder, LAYOUT_CLASS, LAYOUT_ADDER_CLASS);
	}
	
	static private final TypeLiteral<Class<? extends LayoutManager>> LAYOUT_CLASS =
		new TypeLiteral<Class<? extends LayoutManager>>() {};
	static private final TypeLiteral<OkCancelLayoutAdder> LAYOUT_ADDER_CLASS =
		TypeLiteral.get(OkCancelLayoutAdder.class);
}
