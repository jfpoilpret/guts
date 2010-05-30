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

package net.guts.gui.application;

/**
 * Called by {@link AbstractApplication#launch} after Guice initialization, the bound
 * implementation of this interface is used to start the real application and, in 
 * particular, display the first window of the application.
 * <p/>
 * You have to provide an implementation of {@code AppLifecycleStarter} and bind it to
 * Guice in one of your {@link com.google.inject.Module}s as set in 
 * {@link AbstractApplication#initModules}:
 * <pre>
 * bind(AppLifecycleStarter.class).to(MyApplicationLifecycleStarter.class);
 * </pre>
 *
 * @author Jean-Francois Poilpret
 */
public interface AppLifecycleStarter
{
	/**
	 * This is the first method called by {@link AbstractApplication#launch}; it should
	 * initialize and display its first window (or dialog).
	 * At this time, Guice has been initialized already, so this {@code AppLifecycleStarter}
	 * implementing class can be injected with any service, and in particular, 
	 * {@link WindowController} that it should use to display its first window.
	 * <p/>
	 * This method is called from within the Event Dispatch Thread.
	 * 
	 * @param args the original arguments passed in the command line that launched the 
	 * application
	 */
	public void startup(String[] args);
	
	/**
	 * Called after the window displayed by {@link #startup} is actually visible and all
	 * events in the Event Dispatch Thread have been processed (ie all UI content has been
	 * painted), this method can be used to perform tasks that require the UI to be visible.
	 * <p/>
	 * In general, you would rarely need to put actual code in this method, but it may be
	 * useful to handle initialization of certain 3rd-party libraries that can't work before
	 * the UI is fully visible.
	 * <p/>
	 * This method is called from within the Event Dispatch Thread.
	 */
	public void ready();
}
