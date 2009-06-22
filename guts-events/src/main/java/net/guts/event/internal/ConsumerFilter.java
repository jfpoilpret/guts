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

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

public class ConsumerFilter
{	
	ConsumerFilter(
		ChannelKey key, Method consumer, Method filter, int priority, Executor executor)
	{
		_key = key;
		_consumer = consumer;
		_filter = filter;
		_priority = priority;
		_executor = executor;
	}

	public ChannelKey getKey()
	{
		return _key;
	}
	
	public Method getConsumer()
	{
		return _consumer;
	}
	
	public Method getFilter()
	{
		return _filter;
	}
	
	public int getPriority()
	{
		return _priority;
	}

	Executor getExecutor()
	{
		return _executor;
	}

	final private ChannelKey _key;
	final private Method _consumer;
	final private Method _filter;
	final private int _priority;
	final private Executor _executor;
}
