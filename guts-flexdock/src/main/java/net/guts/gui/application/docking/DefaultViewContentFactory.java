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

import java.util.Map;

import javax.swing.JComponent;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
class DefaultViewContentFactory implements ViewContentFactory
{
	@Inject DefaultViewContentFactory(
		Injector injector, @BindViewsMap Map<String, Class<? extends JComponent>> views)
	{
		_injector = injector;
		_views = views;
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.application.docking.ViewContentFactory#createContent(java.lang.String)
	 */
	@Override public JComponent createContent(String id)
	{
		Class<? extends JComponent> clazz = _views.get(id);
		if (clazz != null)
		{
			return _injector.getInstance(clazz);
		}
		else
		{
			return null;
		}
	}

	final private Injector _injector;
	final private Map<String, Class<? extends JComponent>> _views;
}
