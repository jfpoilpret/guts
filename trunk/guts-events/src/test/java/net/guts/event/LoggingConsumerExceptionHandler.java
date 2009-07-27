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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingConsumerExceptionHandler implements ConsumerExceptionHandler
{
	static final private Logger _logger = 
		Logger.getLogger(LoggingConsumerExceptionHandler.class.getName());
	public void handleException(Throwable e, Method method, Object instance,
		Type eventType, String topic)
	{
		String message = String.format(
			"Consumer method %s.%s of object %s for event type %s and topic '%s'",
			method.getDeclaringClass().getName(), method.getName(), instance.toString(),
			eventType, topic);
		_logger.log(Level.WARNING, message, e);
	}
}
