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

package net.guts.gui.session;

import java.awt.Component;

import com.google.inject.ImplementedBy;

/**
 * Interface defining the API to save and restore GUI session state.
 * <p/>
 * You normally won't need to use this API directly as it is automatically
 * called by {@link net.guts.gui.application.WindowController}.
 * <p/>
 * Behind the scenes, this service relies on {@link StorageMedium} to perform
 * the actual physical storage of GUI state.
 * <p/>
 * Besides, {@code SessionManager} needs to know, for a given GUI 
 * {@link java.awt.Component}, which class, implementing {@link SessionState},
 * to use.
 * <p/>
 * By default, Guts-GUI supports several components such as {@link java.awt.Window},
 * {@link javax.swing.JFrame}, {@link javax.swing.JTable}... But you can add support for
 * specific components by defining your own {@code SessionState<T>} for them and 
 * registering them with {@link Sessions#bindSessionConverter}.
 * <p/>
 * {@code SessionManager} uses the names of {@code components} (as returned by 
 * {@link java.awt.Component#getName()}) as the key for storing their state.
 *
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(SessionManagerImpl.class)
public interface SessionManager
{
	/**
	 * Save the state of {@code component} (eg geometry) so that it can be 
	 * restored at next launch.
	 * <p/>
	 * All components in the hierarchy (i.e. children of {@code component}) will 
	 * be checked for possible state persistence. For each component which has a 
	 * registered {@link SessionState}, its state will be persisted through the 
	 * matching {@code SessionState<T>}.
	 * 
	 * @param <T> the type of GUI component which state must be saved
	 * @param component the GUI component which state must be saved
	 */
	public <T extends Component> void save(T component);
	
	/**
	 * Restore the state of {@code component} (eg geometry) if it was already
	 * saved during a previous launch. If state for {@code component} is not 
	 * found, then {@code component} isn't changed at all.
	 * 
	 * @param <T> the type of GUI component which state must be restored
	 * @param component the GUI component which state must be restored
	 */
	public <T extends Component> void restore(T component);
}
