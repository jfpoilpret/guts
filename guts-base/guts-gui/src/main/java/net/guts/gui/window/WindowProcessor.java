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

package net.guts.gui.window;

import javax.swing.RootPaneContainer;

/**
 * This interface is used by {@link WindowController#show}, which calls all
 * registered implementations in the order defined by {@link Windows#bindWindowProcessor}.
 * Based on the order in which they are called, implementations can operate on
 * {@code root} before or after it is displayed.
 * <p/>
 * For implementations that don't support all {@link javax.swing.RootPaneContainer}s,
 * it may be easier to subclass {@link AbstractWindowProcessor}.
 *
 * @author Jean-Francois Poilpret
 */
public interface WindowProcessor
{
	//TODO do these constant belong here?
	static final public int TEMPLATE_DECORATION = 1000;
	static final public int RESOURCE_INJECTION = 2000;
	static final public int BOUNDS_INIT = 3000;
	static final public int SESSION_STORAGE = 4000;
	static final public int CLOSE_CHECKER_SETUP = 9000;
	static final public int DISPLAY = 10000;

	/**
	 * Called to process {@code root} according to {@code config} which actual type is
	 * guaranteed to hold the necessary information for the type {@code T} of {@code root}.
	 * 
	 * @param <T> actual {@code RootPaneContainer} implementation class of {@code root}
	 * @param root the window to process
	 * @param config the configuration for {@code root} according to its type {@code T}
	 */
	public <T extends RootPaneContainer> void process(T root, RootPaneConfig<T> config);
}
