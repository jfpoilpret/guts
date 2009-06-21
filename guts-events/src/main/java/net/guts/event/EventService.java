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

package net.guts.event;

import net.guts.event.internal.EventServiceImpl;

import com.google.inject.ImplementedBy;
import com.google.inject.TypeLiteral;

@ImplementedBy(EventServiceImpl.class)
public interface EventService
{
	//TODO why would we need registration before getChannel?
	//TODO pass more setup info such as: executor? consumer exception handler?
	public<T> void registerChannel(TypeLiteral<T> type, String topic);
	
	public<T> Channel<T> getChannel(TypeLiteral<T> type, String topic)
		throws IllegalArgumentException;

	public void registerConsumers(Object instance);
	//TODO allow consumer unregistration? 
	//Why? It is automatic based on weak references!
}
