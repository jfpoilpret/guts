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

import com.google.inject.ImplementedBy;

/**
 * Service managing uncaught exceptions. Uncaught exceptions are fed through
 * {@link #handleException}, and dispatched to all objects with 
 * {@link HandlesException}-annotated methods, provided these objects have been
 * registered through {@link #registerExceptionHandlers}.
 * <p/>
 * You normally won't need direct access to this service. Once 
 * {@link ExceptionHandlingModule} has been used to initialize Guice, this
 * service is already connected to other parts of Guts-GUI framework; in
 * particular, uncaught exceptions occurring in the Event Dispatch Thread will
 * be automatically handled by this service.
 *
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(ExceptionHandlerManagerImpl.class)
public interface ExceptionHandlerManager
{
	public void handleException(Throwable e);
	public void registerExceptionHandlers(Object instance);
}
