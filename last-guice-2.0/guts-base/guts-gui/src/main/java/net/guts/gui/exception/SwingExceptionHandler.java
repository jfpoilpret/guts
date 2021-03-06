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

package net.guts.gui.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Special handler for all exceptions thrown inside the EDT. This handler simply
 * delegates all exception handling to {@link ExceptionHandlerManager}.
 * <p/>
 * This handler is automatically registered with Swing through 
 * {@link ExceptionHandlingModule}. Although {@code public}, it is not part of the
 * official Guts-GUI API, it has to be {@code public} because of the Swing trick it
 * uses to be notified of exceptions occurring in the EDT.
 * 
 * @author Jean-Francois Poilpret
 */
public class SwingExceptionHandler
{
	static private final Logger _logger = LoggerFactory.getLogger(SwingExceptionHandler.class);
	static private final String	HANDLER_PROPERTY = "sun.awt.exception.handler";

	static
	{
		System.setProperty(HANDLER_PROPERTY, SwingExceptionHandler.class.getName());
	}
	
	public void	handle(Throwable e)
	{
		if (_manager != null)
		{
			_manager.handleException(e);
		}
		else
		{
			_logger.info("Impossible to handle exception because _manager is null.", e);
		}
	}

	/**
	 * Initialization method statically injected by Guice.
	 * 
	 * @param manager the {@link ExceptionHandlerManager} service to which 
	 * exception handling is delegated
	 */
	@Inject static void setExceptionHandler(ExceptionHandlerManager manager)
	{
		_manager = manager;
	}

	static private ExceptionHandlerManager _manager;
}
