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

package net.guts.gui.naming;

import java.awt.Component;

import com.google.inject.ImplementedBy;

/**
 * This Guts-GUI service is in charge of setting names to {@link java.awt.Component}s
 * of Guice-injected components.
 * <p/>
 * This is automatically called after injection of a {@code Component}, provided you
 * have installed {@link ComponentNamingModule} when creating Guice 
 * {@link com.google.inject.Injector}.
 * <p/>
 * A default implementation is provided and works as described in {@link #setComponentName}.
 * <p/>
 * You can change, to some extent, the {@link ComponentNamePolicy naming policy} used
 * by this default implementation to build the names of components:
 * <pre>
 * bind(ComponentNamePolicy.class).to(MyNamePolicy.class);
 * </pre>
 * <p/>
 * You can also, if changing the naming policy is not sufficient, completely override
 * {@code ComponentNamingService} implementation and bind it in one of your Guice
 * {@link com.google.inject.Module}s:
 * <pre>
 * bind(ComponentNamingService.class).to(MyNamingService.class);
 * </pre>
 *
 * @see ComponentNamePolicy
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(ComponentNamingServiceImpl.class)
public interface ComponentNamingService
{
	/**
	 * This method is in charge of naming {@code parent} and its `children` 
	 * components. The default {@code ComponentNamingService} implementation
	 * proceeds as follows:
	 * <ul>
	 * <li>the name of a {@code Component} will be automatically set only if it 
	 * hasn't been set before (by calling {@link java.awt.Component#setName}), 
	 * i.e. it will be set only if {@link java.awt.Component#getName} is {@code null}</li>
	 * <li>name creation is performed by the {@link ComponentNamePolicy} which default
	 * implementation {@link DefaultComponentNamePolicy} can be overridden</li>
	 * <li>{@code parent}'s name is set first</li>
	 * <li>if {@code parent} has fields of type {@code Component} (or any subclass),
	 * their names will be set then</li>
	 * <li>if {@code parent} has fields of type {@link ComponentHolder} (or any subclass),
	 * they will be recursively traversed and all found {@code Component} will have
	 * their names set</li>
	 * </ul>
	 * 
	 * @param parent the component which name and fields' names you want to 
	 * automatically set
	 */
	public void setComponentName(Component parent);
}
