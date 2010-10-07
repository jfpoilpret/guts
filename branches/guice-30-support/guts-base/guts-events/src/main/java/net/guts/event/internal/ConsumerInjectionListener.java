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

package net.guts.event.internal;

import net.guts.common.injection.AbstractInjectionListener;
import net.guts.event.EventService;

import com.google.inject.Inject;

public class ConsumerInjectionListener extends AbstractInjectionListener<Object>
{
	// Guice Injector is injected as a "trick" in order to delay the call to this method
	// as late as possible during Guice.createInjector()
	@Inject public void setEventService(EventService service)
	{
		_service = service;
	}
	
	@Override protected void registerInjectee(Object injectee)
	{
		_service.registerConsumers(injectee);
	}
	
	private EventService _service = null;
}
