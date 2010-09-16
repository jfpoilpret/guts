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
 * Annotates a class (or a package, thus applied to all classes in that package)
 * in order to specify resource bundles that {@link ResourceInjector} will use 
 * when injecting instances of the annotated class.
 * <p/>
 * If used for a whole package and for a class in that package, then the class-level
 * annotation takes precedence (package-level annotation won't be used at all).
 * <p/>
 * Resource bundles are specified as paths to properties files but <b>don't</b>
 * include the {@code properties} extension, as in {@code /mydialog/resources},
 * which will be mapped to a file named {@code "/mydialog/resources.properties"}.
 * <p/>
 * Resource files must be found in the classpath (not on the file system).
 * <p/>
 * Paths may be relative or absolute:
 * <ul>
 * <li>absolute: starts with {@code "/"}, the resource bundle will be looked for
 * at this exact location in the classpath</li>
 * <li>relative: doesn't start with {@code "/"}, the resource bundle will be
 * looked for, in the classpath, at this location, relative to the annotated
 * class or package</li>
 * </ul>
 * <p/>
 * Note that, actually, there can be several resource bundles under the same path,
 * each for a different {@code java.util.Locale}, as defined in standard
 * {@link java.util.ResourceBundle}. E.g. for the example above, we could have
 * {@code "/mydialog/resources.properties"} and {@code "/mydialog/resources_fr.properties"}.
 *
 * @author Jean-Francois Poilpret
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PACKAGE})
public @interface UsesBundles
{
	/**
	 * Ordered list of paths which determine the location to a resource bundle file.
	 * <p/>
	 * During resource injection by {@link ResourceInjector}, bundles in the list 
	 * will be searched in for injectable properties in the order of this list, 
	 * i.e. if the same property is defined in several bundles, then the one from 
	 * the leftmost bundle is used.
	 * <p/>
	 * Note that there is a special "root" bundle that is searched before any 
	 * bundles defined here. This bundle is global for the whole application and
	 * is defined by {@link Resources#bindRootBundle}.
	 * <p/>
	 * When the list of paths is empty, if {@link #useClassName()} is {@code false},
	 * this means that the package of the annotated class (or the annotated package 
	 * itself) <b>is</b> the resource bundle location path. In this case, a resources 
	 * bundle named {@code "resources"} will be added to the list of bundles for the 
	 * annotated class or package; if {@link #useClassName()} is {@code true}, then
	 * the name of the class will be used as the resources bundle name, instead of
	 * "resources".
	 */
	String[] value() default {};

	/**
	 * Used only when {@link #value()} is empty, this indicates how the resources 
	 * bundle file name is to be obtained: if {@code false} (the default), then
	 * the bundle file will be named "resources", otherwise the name of the class
	 * holding the annotation will be used as the bundle file name.
	 * <p/>
	 * In general, you won't need this option, as it is normally more convenient to
	 * have all resources for all classes in one package in only one bundle file, 
	 * rather than several. 
	 */
	boolean useClassName() default false;
}
