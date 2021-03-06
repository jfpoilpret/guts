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

package net.guts.gui.docking;

import javax.swing.JComponent;

import bibliothek.gui.dock.common.CControl;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;

public final class Docking
{
	private Docking()
	{
	}

	static public LinkedBindingBuilder<DockableFactory> bindDockableFactory(Binder binder)
	{
		return binder.bind(DockableFactory.class).annotatedWith(BindDockableFactory.class);
	}
	
	static public LinkedBindingBuilder<LayoutInitializer> bindLayout(
		Binder binder, String id)
	{
		return layouts(binder).addBinding(id);
	}
	
	static public LinkedBindingBuilder<LayoutInitializer> bindDefaultLayout(
		Binder binder)
	{
		return bindLayout(binder, DEFAULT_PERSPECTIVE);
	}

	static MapBinder<String, LayoutInitializer> layouts(Binder binder)
	{
		return MapBinder.newMapBinder(binder, String.class, LayoutInitializer.class);
	}
	
	static public void bindView(
		Binder binder, String id, Class<? extends JComponent> viewClass)
	{
		views(binder).addBinding(id).toInstance(viewClass);
	}

	static MapBinder<String, Class<? extends JComponent>> views(Binder binder)
	{
		return MapBinder.newMapBinder(
			binder, TypeLiteral.get(String.class), COMPONENT_CLASS_TYPE, BindViewsMap.class);
	}

	static public void bindDefaultContentArea(Binder binder)
	{
		bindContentArea(binder, CControl.CONTENT_AREA_STATIONS_ID);
	}
	
	static public void bindContentArea(Binder binder, String id)
	{
		contentAreas(binder).addBinding().toInstance(id);
	}
	
	static Multibinder<String> contentAreas(Binder binder)
	{
		return Multibinder.newSetBinder(binder, String.class, BindContentAreasSet.class);
	}

	static public void bindWorkingArea(Binder binder, String id)
	{
		workingAreas(binder).addBinding().toInstance(id);
	}
	
	static Multibinder<String> workingAreas(Binder binder)
	{
		return Multibinder.newSetBinder(binder, String.class, BindWorkingAreasSet.class);
	}

	static public void bindGridArea(Binder binder, String id)
	{
		gridAreas(binder).addBinding().toInstance(id);
	}
	
	static Multibinder<String> gridAreas(Binder binder)
	{
		return Multibinder.newSetBinder(binder, String.class, BindGridAreasSet.class);
	}

	static public void bindMinimizeArea(Binder binder, String id)
	{
		minimizeAreas(binder).addBinding().toInstance(id);
	}
	
	static Multibinder<String> minimizeAreas(Binder binder)
	{
		return Multibinder.newSetBinder(binder, String.class, BindMinimizeAreasSet.class);
	}

	static final String DEFAULT_PERSPECTIVE = "DEFAULT_PERSPECTIVE";

	static final private TypeLiteral<Class<? extends JComponent>> COMPONENT_CLASS_TYPE = 
		new TypeLiteral<Class<? extends JComponent>>(){};
}
