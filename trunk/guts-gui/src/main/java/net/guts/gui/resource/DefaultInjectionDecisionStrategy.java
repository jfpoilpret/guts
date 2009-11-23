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

import javax.swing.JComponent;
import javax.swing.RootPaneContainer;

import com.google.inject.Singleton;

@Singleton
class DefaultInjectionDecisionStrategy implements InjectionDecisionStrategy
{
	//CSOFF: ReturnCountCheck
	@Override public InjectionDecision needsInjection(Object component, Locale locale)
	{
		// Several cases based on type of component
		// (yeah I know, not really good OO design here, but sometimes 
		// you just want the job done, however it looks...)
		
		// Window case
		if (component instanceof RootPaneContainer)
		{
			RootPaneContainer container = (RootPaneContainer) component;
			// If window is already OK, nothing more to do
			if (!checkLocale(container.getRootPane(), locale))
			{
				return InjectionDecision.DONT_INJECT;
			}
			// If window needs translation, check if content pane needs also
			if (!checkLocale(container.getContentPane(), locale))
			{
				return InjectionDecision.INJECT_COMPONENT_ONLY;
			}
			else
			{
				return InjectionDecision.INJECT_HIERARCHY;
			}
		}
		
		// Swing JComponent ==> inject whole hierarchy if client property not OK
		// Other types (including pure AWT components) ==> always inject whole hierarchy
		if (checkLocale(component, locale))
		{
			return InjectionDecision.INJECT_HIERARCHY; 
		}
		else
		{
			return InjectionDecision.DONT_INJECT;
		}
	}
	//CSON: ReturnCountCheck
	
	static private boolean checkLocale(Object component, Locale locale)
	{
		if (component instanceof JComponent)
		{
			Object oldLocale = ((JComponent) component).getClientProperty(RESOURCES_LOCALE);
			return (oldLocale == null || !locale.equals(oldLocale));
		}
		else
		{
			return true;
		}
	}

	@Override public void injectionPerformed(Object component, Locale locale)
	{
		if (component instanceof RootPaneContainer)
		{
			RootPaneContainer container = (RootPaneContainer) component;
			markComponent(container.getRootPane(), locale);
			markComponent(container.getContentPane(), locale);
		}
		else
		{
			markComponent(component, locale);
		}
	}
	
	static private void markComponent(Object component, Locale locale)
	{
		if (component instanceof JComponent)
		{
			((JComponent) component).putClientProperty(RESOURCES_LOCALE, locale);
		}
	}
	
	static final private String RESOURCES_LOCALE =
		DefaultInjectionDecisionStrategy.class.getSimpleName() + ".ResourcesLocale";
}
