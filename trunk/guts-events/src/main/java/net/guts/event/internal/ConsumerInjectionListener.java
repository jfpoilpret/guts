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

import java.util.ArrayList;
import java.util.List;

import net.guts.event.EventService;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.spi.InjectionListener;

public class ConsumerInjectionListener implements InjectionListener<Object>
{
	// Guice Injector is injected as a "trick" in order to delay the call to this method
	// as late as possible during Guice.createInjector()
	@Inject public void setEventService(EventService service, Injector injector)
	{
		_service = service;
		// Register all pending instances with the newly injected EventService
		for (Object injectee: _pendingInjectees)
		{
			registerInjectee(injectee);
		}
		_pendingInjectees.clear();
	}
	
	public void afterInjection(Object injectee)
	{
		registerInjectee(injectee);
	}
	
	private void registerInjectee(Object injectee)
	{
		// At Injector creation, it may happen some objects are injected and passed
		// to afterInjection() _before_ setEventService() has been called by Guice;
		// In such cases, consumer registration is deferred for those objects until
		// setEventService() gets called.
		if (_service != null)
		{
			_service.registerConsumers(injectee);
		}
		else
		{
			_pendingInjectees.add(injectee);
		}
	}
	
	private EventService _service = null;
	final private List<Object> _pendingInjectees = new ArrayList<Object>();
}
