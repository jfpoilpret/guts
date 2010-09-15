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

import java.lang.annotation.Annotation;

import net.guts.event.Event;

public class EventImpl implements Event
{
	public EventImpl(String value, boolean primitive)
	{
		_topic = value;
		_primitive = primitive;
	}
	
	public String topic()
	{
		return _topic;
	}
	
	public boolean primitive()
	{
		return _primitive;
	}

	public Class<? extends Annotation> annotationType()
	{
		return Event.class;
	}

	//CSOFF: MagicNumberCheck
	@Override public int hashCode()
	{
		// This is specified in java.lang.Annotation.
		return	((127 * "topic".hashCode()) ^ _topic.hashCode()) + 
				((127 * "primitive".hashCode() ^ Boolean.valueOf(_primitive).hashCode()));
	}
	//CSON: MagicNumberCheck

	@Override public boolean equals(Object o)
	{
		if (!(o instanceof Event))
		{
			return false;
		}
		
		Event other = (Event) o;
		return _topic.equals(other.topic()) && _primitive == other.primitive();
	}

	@Override public String toString()
	{
		return "@" + Event.class.getName() + 
			"(topic=" + _topic + ", primitive=" + _primitive + ")";
	}

	final private String _topic;
	final private boolean _primitive;
}
