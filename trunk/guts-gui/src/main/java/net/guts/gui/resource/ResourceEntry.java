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

import java.net.URL;

/**
 * Wrapper of a {@code String} value extracted from a given resource bundle,
 * adding several utilities in relation to that value, in particular the
 * possibility to obtain a file reference (as a {@link java.net.URL} to a file
 * present in classpath) from that value.
 * <p/>
 * This class is used by {@link ResourceConverter}s and is present in
 * {@link ResourceMap}.
 * 
 * @author Jean-Francois Poilpret
 */
public final class ResourceEntry
{
	ResourceEntry(String value, String source)
	{
		_value = value;
		_source = source;
	}

	/**
	 * Creates a new {@code ResourceEntry} from {@code this} one, but with a new 
	 * value. This is useful when the original value contains several pieces of 
	 * information, one of which is a path to a file which we need to extract as
	 * an {@link java.net.URL}, as in the following excerpt from 
	 * {@code ResourceConverter<CursorInfo>} (simplified):
	 * <pre>
	 * &#64;Override public CursorInfo convert(ResourceEntry entry)
	 * {
	 *     // First check if this is a predefined cursor
	 *     CursorType type = CursorType.valueOf(entry.value());
	 *     // Split s into: iconfile[,hotspotx[,hotspoty]]
	 *     StringTokenizer tokenize = new StringTokenizer(entry.value(), ",");
	 *     if (tokenize.countTokens() &lt; 1 || tokenize.countTokens() &gt; 3)
	 *     {
	 *         return null;
	 *     }
	 *     // Convert iconfile to an Icon with default ResourceConverter&lt;Icon&gt;
	 *     ImageIcon icon = (ImageIcon) converter(Icon.class).convert(
	 *         entry.derive(tokenize.nextToken()));
	 *     double x = getHotspotRate(tokenize);
	 *     double y = getHotspotRate(tokenize);
	 *     return CursorHelper.buildCursor(icon, x, y);
	 * }
	 * </pre>
	 * 
	 * @param value the new value with which to create a new {@code ResourceEntry}
	 * @return a new {@code ResourceEntry} wrapping {@code value} and having virtually
	 * the same "source" resource bundle as {@code this}
	 */
	public ResourceEntry derive(String value)
	{
		return new ResourceEntry(value, _source);
	}

	/**
	 * The original {@code String} value of the given property entry from a 
	 * resource bundle. This value is taken "as is" from the properties file.
	 * 
	 * @return the original value from the resource bundle
	 */
	public String value()
	{
		return _value;
	}

	/**
	 * Tries to convert the {@code String} value wrapped by this {@code ResourceEntry}
	 * into a valid path to a file in the classpath, returned as a valid 
	 * {@link java.net.URL}.
	 * <p/>
	 * The original value may be an absolute (starting with '/') or relative file 
	 * path. If relative, the starting directory, used for finding the file, is the
	 * current location of the resource bundle from where the original value was read.
	 * 
	 * @return a valid {@code URL} to the file referenced by the value wrapped by 
	 * {@code this}, or {@code null} if no file can be found at that location
	 */
	public URL valueAsUrl()
	{
		// Build path to resource if not absolute
		String path;
		if (_value.startsWith("/"))
		{
			path = _value.substring(1);
		}
		else
		{
			path = _source.replaceAll("\\.", "\\/") + "/" + _value;
		}
		return Thread.currentThread().getContextClassLoader().getResource(path);
	}

	final private String _value;
	final private String _source;
}
