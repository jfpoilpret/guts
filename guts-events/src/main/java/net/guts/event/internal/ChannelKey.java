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

import java.lang.reflect.Type;

// The unique key for every single Event Channel
public class ChannelKey
{
	public ChannelKey(Type type, String topic)
	{
		_type = type;
		_topic = (topic == null ? "" : topic);
	}

	Type getType()
	{
		return _type;
	}

	String getTopic()
	{
		return _topic;
	}

	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + _topic.hashCode();
		result = prime * result + _type.hashCode();
		return result;
	}

	@Override public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		ChannelKey other = (ChannelKey) obj;
		if (!_topic.equals(other._topic))
		{
			return false;
		}
		if (!_type.equals(other._type))
		{
			return false;
		}
		return true;
	}

	final private Type _type;
	final private String _topic;
}
