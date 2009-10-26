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

//TODO find a better name like SingleResourceInjector? InstanceResourcesInjector?...
/**
 * TODO
 *
 * @author Jean-Francois Poilpret
 */
public interface ComponentInjector<T>
{
	// resources is the list of all resources (strongly typed) for this
	// component
	// Implementations of this method should iterate through resources and
	// inject each individual resource into component
	public void inject(T component, ResourceMap resources);
}