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

import net.guts.event.Channel;
import net.guts.event.EventService;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

public class ChannelProvider<T> implements Provider<Channel<T>>
{
	public ChannelProvider(TypeLiteral<T> type, String topic)
	{
		_eventType = type;
		_topic = topic;
	}
	
	@Inject public void setEventService(EventService service)
	{
		_service = service;
		_service.registerChannel(_eventType, _topic);
	}

	// All provider instances should be bound as singleton, hence get() should
	// be called only once, hence no need for any cache of the event channel
	// returned by the EventService
	public Channel<T> get()
	{
		return _service.getChannel(_eventType, _topic);
	}

	final private TypeLiteral<T> _eventType;
	final private String _topic;
	private EventService _service = null;
}
