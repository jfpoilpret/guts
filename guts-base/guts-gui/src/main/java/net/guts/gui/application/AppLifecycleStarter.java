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
 * Called by {@link AbstractApplication#launch} after Guice initialization, the 
 * bound implementation of this interface is used to start the real application 
 * (or applet) and, in particular, display the first window of the application or 
 * set the applet's content pane.
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
	 * This is the name of the event sent (through guts-events) just after the 
	 * application has started and made its content (frame, dialog...) visible. 
	 * It gives all consumers of that event a chance to perform specific action
	 * that can operate only on a visible GUI.
	 * <p/>
	 * Here is an example of such a hook:
	 * <pre>
	 * &#64;Consumes(topic = AppLifecycleStarter.READY_EVENT)
	 * public void ready(Void nothing)
	 * {
	 *     // Some specific task here
	 * }
	 * </pre>
	 * Note that the method needs to take a {@code Void} argument (due to a limitation
	 * of guts-events library that requires every consumer to have exactly one 
	 * argument of the type of the event).
	 * <p/>
	 * In general, you would rarely need to listen for this event, but it may be
	 * useful to handle initialization of certain 3rd-party libraries that can't work before
	 * the UI is fully visible.
	 * <p/>
	 * This event is handled from within the Event Dispatch Thread.
	 */
	static final public String READY_EVENT = "net.guts.gui.AppLifecycleStarter.ready";
	
	/**
	 * This is the first method called by {@link AbstractApplication#launch}; it should
	 * initialize and display its first window (or dialog).
	 * At this time, Guice has been initialized already, so this {@code AppLifecycleStarter}
	 * implementing class can be injected with any service, and in particular, 
	 * {@link net.guts.gui.window.WindowController} that it should use to display its 
	 * first window.
	 * <p/>
	 * This method is called from within the Event Dispatch Thread.
	 * 
	 * @param args the original arguments passed in the command line that launched the 
	 * application
	 */
	public void startup(String[] args);
}
