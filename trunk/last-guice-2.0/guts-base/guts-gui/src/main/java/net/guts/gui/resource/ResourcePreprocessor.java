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

import com.google.inject.ImplementedBy;

/**
 * When bound to Guts-GUI (with {@link Resources#bindResourcePreprocessor}), an 
 * implementation of this interface will be called on any property value, before it
 * is converted through {@link ResourceConverter}.
 * <p/>
 * This allows performing any necessary processing to the "raw" string value of
 * resource properties.
 * <p/>
 * Default Guts-GUI implementation of {@code ResourcePreprocessor} performs symbol
 * substitution, i.e. any occurrence of {@code $&#123;name&#125;} is replaced by the 
 * value of the property named {@code name} if it exists, else the occurrence 
 * remains intact.
 * <p/>
 * This enables you to define commonly used values in one bundle and use them in
 * another bundle, as in the following example:
 * <pre>
 * # in root bundle
 * application-version = 1.0
 * ...
 * # in another bundle
 * about-dialog-version-label.text=${application-version}
 * </pre>
 * <p/>
 * You can override default behavior by providing your own implementation and 
 * binding it to Guts-GUI by using {@link Resources#bindResourcePreprocessor}.
 * 
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(SymbolSubstitutionResourcePreprocessor.class)
public interface ResourcePreprocessor
{
	/**
	 * Convert the raw string value {@code source} of a resource bundle property
	 * into a processed string value, ready to be converted and injected into a 
	 * component.
	 * 
	 * @param map the {@code ResourceMap} from which {@code source} is issued; you
	 * can use it to lookup other properties.
	 * @param source the raw value of a resource property
	 * @return the processed value for {@code source}
	 */
	public String convert(ResourceMap map, String source);
}
