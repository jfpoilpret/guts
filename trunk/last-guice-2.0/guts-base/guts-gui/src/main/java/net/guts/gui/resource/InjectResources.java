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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a class to tell {@link ResourceModule} that any instances, created 
 * by Guice, of the annotated class, should have resources injected into them
 * (by {@link ResourceInjector#injectInstance(Object, String)}).
 * 
 * @author Jean-Francois Poilpret
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface InjectResources
{
	/**
	 * The prefix that will be used when looking for properties to inject into instances 
	 * of the annotated class. By default, the name of the annotated class is used as
	 * prefix of property keys.
	 */
	String prefix() default "";
	
	/**
	 * If {@code true}, later changes of current {@link java.util.Locale}, when set by
	 * {@link ResourceInjector#setLocale}, instances of the annotated class will be
	 * re-injected with resources for the new current {@code Locale}.
	 */
	boolean autoUpdate() default false;
}
