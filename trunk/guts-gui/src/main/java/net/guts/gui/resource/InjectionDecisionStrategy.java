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

import com.google.inject.ImplementedBy;

/**
 * Strategy used by {@link ResourceInjector} to know if a given component needs
 * to be injected for a given {@link Locale} or not.
 * <p/>
 * This can prevent. for instance, multiple injection of the same resources for
 * a dialog panel that is reused for several different dialogs during the
 * application lifecycle. Avoiding unnecessary injection is useful because it
 * is a time-consuming operation.
 * <p/>
 * Default strategy systematically re-injects any object or Swing component.
 *
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(SimpleInjectionDecisionStrategy.class)
public interface InjectionDecisionStrategy
{
	/**
	 * Called from {@link ResourceInjector#injectComponent}, 
	 * {@link ResourceInjector#injectHierarchy}, {@link ResourceInjector#injectInstance(Object)}
	 * and {@link ResourceInjector#injectInstance(Object, String)}, prior to any
	 * concrete resource injection, this method can determine whether injection is
	 * needed for the given {@code component} in the given {@code Locale}.
	 * 
	 * @param component component which resources are going to be injected by
	 * {@link ResourceInjector}
	 * @param locale locale that will be used for resource injection of {@code component}
	 * @return the decision on whether resource injection is required or not
	 */
	public InjectionDecision needsInjection(Object component, Locale locale);

	/**
	 * Called from {@link ResourceInjector#injectComponent}, 
	 * {@link ResourceInjector#injectHierarchy}, {@link ResourceInjector#injectInstance(Object)}
	 * and {@link ResourceInjector#injectInstance(Object, String)}, <b>after</b> resource
	 * injection has occurred; that method will be called only if {@link #needsInjection}
	 * for that {@code component} and {@code locale} did not return 
	 * {@link InjectionDecision#DONT_INJECT}.
	 * 
	 * @param component the component which resources were just injected by
	 * {@link ResourceInjector}
	 * @param locale the locale that was used for resource injection of {@code component}
	 */
	public void injectionPerformed(Object component, Locale locale);

	/**
	 * Injection decision returned by {@link InjectionDecisionStrategy#needsInjection}.
	 * This will tell {@code ResourceInjector} if the given component is to be injected
	 * and how.
	 *
	 * @author Jean-Francois Poilpret
	 */
	static public enum InjectionDecision
	{
		/**
		 * Neither the given {@code component}, nor any component in its hierarchy, shall
		 * be injected.
		 */
		DONT_INJECT,
		
		/**
		 * The given {@code component} should be injected, but not its children.
		 */
		INJECT_COMPONENT_ONLY,
		
		/**
		 * The given {@code component} and all its hierarchy should be injected.
		 */
		INJECT_HIERARCHY,
		
//		INJECT_COMPONENT_AND_ASK
	}
}
