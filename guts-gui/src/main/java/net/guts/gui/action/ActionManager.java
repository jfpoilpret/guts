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

package net.guts.gui.action;

import javax.swing.Action;


import com.google.inject.ImplementedBy;

/**
 * Interface defining the central manager of Actions across the system.
 * All {@link javax.swing.Action}s used in the application should be 
 * obtained from this manager.
 * <p/>
 * The manager allows registering "ActionSource" objects that contain actions
 * defined through the {@code @Action} annotation.
 * <p/>
 * By default, the {@link net.guts.gui.application.AbstractGuiceApplication} 
 * instance itself is registered as an ActionSource.
 * <p/>
 * This service is directly injectable through Guice facilities.
 * 
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(ActionManagerImpl.class)
public interface ActionManager
{
	/**
	 * Search for a given {@code Action}, given its name, in the list of
	 * all registered ActionSource objects.
	 * <p/>
	 * If several actions exist with the same name (on different ActionSource 
	 * objects), then the action returned by this method is undefined, in this
	 * case, use {@link #getAction(String, Object)} instead.
	 * 
	 * @param name the name of the {@code Action} to search
	 * @return the Action if it exists, else {@code null}
	 */
	public Action getAction(String name);

	/**
	 * Search for a given {@code Action}, given its name, in the passed
	 * ActionSource object.
	 * <p/>
	 * The passed object does not have to be registered in the list of 
	 * ActionSource objects.
	 * 
	 * @param name the name of the {@code Action} to search
	 * @param source the object which contains the {@code Action} to search
	 * @return the Action if it exists, else {@code null}
	 */
	public Action getAction(String name, Object source);

	/**
	 * Registers an ActionSource object to the list of ActionSources that
	 * {@link #getAction(String)} will search in.
	 * 
	 * @param source an object that contains {@code @Action} annotated methods
	 */
	public void addActionSource(Object source);

	/**
	 * Unregisters an ActionSource object from the list of ActionSources that
	 * {@link #getAction(String)} could search in. From now on, this object will 
	 * not be searched through.
	 * 
	 * @param source an object that was previously registered through
	 * {@link #addActionSource(Object)}; if {@code source} was not previously 
	 * registered, then the method does nothing.
	 */
	public void removeActionSource(Object source);
}
