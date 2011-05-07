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

import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

import net.guts.common.injection.AbstractInjectionListener;
import net.guts.event.Consumes;

import com.google.inject.Inject;

class ResourceInjectionListener extends AbstractInjectionListener<Object>
{
	@Inject void setActionManager(ResourceInjector injector)
	{
		_injector = injector;
	}

	/* (non-Javadoc)
	 * @see net.guts.common.injection.AbstractInjectionListener#registerInjectee(java.lang.Object)
	 */
	@Override protected void registerInjectee(Object injectee)
	{
		// Get the annotation and prefix
		Class<?> clazz = injectee.getClass();
		InjectResources annotation = clazz.getAnnotation(InjectResources.class);
		String prefix = annotation.prefix();
		if (prefix.equals(""))
		{
			prefix = clazz.getSimpleName();
		}
		_injector.injectInstance(injectee, prefix);
		if (annotation.autoUpdate())
		{
			_refsToUpdate.put(injectee, prefix);
		}
	}
	
	@Consumes public void localeChanged(Locale locale)
	{
		for (Map.Entry<Object, String> entry: _refsToUpdate.entrySet())
		{
			_injector.injectInstance(entry.getKey(), entry.getValue());
		}
	}

	private ResourceInjector _injector;
	final private Map<Object, String> _refsToUpdate = new WeakHashMap<Object, String>();
}
