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

/**
 * This package contains the Guts-GUI API for persistence of GUI session state.
 * GUI session state is typically the "geometry" of the GUI windows, as set by 
 * the user when she resizes a frame or change the position of a divider in a
 * {@link javax.swing.JSplitPane}...
 * <p/>
 * It is important, for any GUI application to look professional, to be able to
 * persist GUI session state across several launches of the application, so that
 * the end user doesn't have to manually change the geometry for the application
 * every time she launches it.
 * 
 * <p/><a name="session1"></a><h3>GUI Session State Persistence</h3>
 * The API to manage GUI session state is defined by 
 * {@link net.guts.gui.session.SessionManager} which defines a couple of methods to 
 * save and restore state of a GUI component (including its whole hierarchy). It also
 * has methods to be used for persisting any other state, not related to GUI 
 * components (see <a href="#session5">below</a>).
 * <p/>
 * For your UI components, if you use Guts-GUI as the whole framework for your application,
 * then you won't have to explicitly call {@code SessionManager}: 
 * {@link net.guts.gui.window.WindowController} automatically calls it for you when
 * you display or close a window.
 * <p/>
 * If you don't use Guts-GUI framework for your application but still want to use
 * {@link net.guts.gui.session.SessionManager}, then you should make sure, when you 
 * create a Guice {@link com.google.inject.Injector}, to pass 
 * {@link net.guts.gui.session.SessionModule} in the list of
 * {@link com.google.inject.Module}s.
 * <p/>
 * Moreover, in this case, you'll also need to bind your main application class to the
 * {@code SessionManager} (it is used to determine a key, unique to your application,
 * for proper storage):
 * <pre>
 * Sessions.bindApplicationClass(binder(), MyApplication.class);
 * </pre>
 * 
 * <p/><a name="session2"></a><h3>Session State Storage</h3>
 * Physical storage is handled by {@link net.guts.gui.session.StorageMedium} 
 * service, which default implementation uses {@link java.util.prefs.Preferences}
 * API for storage, e.g. on Windows, the registry is used.
 * <p/>
 * Each component state is stored under one key, named after the component name. The
 * value is an XML string which is the result of serialization of the component state
 * through <a hr="http://xstream.codehaus.org/">XStream</a>. Serialization is handled
 * by {@link net.guts.gui.session.SerializationManager} service.
 * <p/>
 * All keys are stored under the same preferences {@link java.util.prefs.Preferences}
 * node, based on the application class name (as bound with 
 * {@link net.guts.gui.session.Sessions#bindApplicationClass}).
 * <p/>
 * Component states are instances of classes implementing 
 * {@link net.guts.gui.session.SessionState}.
 * <p/>
 * Guts-GUI handles the state persistence of several kinds of 
 * {@link java.awt.Component}s, but it allows you to defined special state
 * persistence for specific components.
 * 
 * <p/><a name="session3"></a><h3>GUI components which state is handled by Guts-GUI</h3>
 * Guts-GUI supports state persistence for the following components:
 * <ul>
 * <li>{@link javax.swing.JFrame}: Bounds (i.e. location and size) are stored, as
 * well as the extended state (minimized, maximized, normal...) State restoration 
 * occurs only if the screen(s) configuration has not changed.</li>
 * <li>{@link java.awt.Window}: Bounds (i.e. location and size) are stored; state
 * restoration occurs only if the screen(s) configuration has not changed.</li>
 * <li>{@link javax.swing.JSplitPane}: divider location is stored.</li>
 * <li>{@link javax.swing.JTabbedPane}: last selected tab is stored.</li>
 * <li>{@link javax.swing.JTable}: order and width of columns are stored.</li>
 * </ul>
 * 
 * <p/><a name="session4"></a><h3>Handling State of special GUI components</h3>
 * If you have special needs on state persistence for some GUI components, you
 * can define your own {@link net.guts.gui.session.SessionState} implementation
 * for that component and bind it to {@link net.guts.gui.session.SessionManager}
 * by using {@link net.guts.gui.session.Sessions#bindSessionConverter} from within
 * one of your Guice {@link com.google.inject.Module}:
 * <pre>
 * Sessions.bindSessionConverter(binder(), MyComponent.class).to(MyComponentState.class);
 * </pre>
 * For a {@code SessionState<T>} subclass to be persistable, it just needs to have a 
 * default public constructor. Guts-GUI will persist all its non static fields (even 
 * {@code private}) except those marked with {@code transient}.
 * 
 * <p/><a name="session5"></a><h3>Persisting other state (non GUI)</h3>
 * {@link net.guts.gui.session.SessionManager} also has methods for saving and
 * restoring any kinds of objects:
 * <ul>
 * <li>{@link net.guts.gui.session.SessionManager#save(String, Object)}</li>
 * <li>{@link net.guts.gui.session.SessionManager#restore(String, Object)}</li>
 * </ul>
 * You can use these methods to deal with any object, provided this object has a 
 * public default constructor. If your application needs to manage some general state 
 * (e.g. preferences settings like host URL, timeout...), then you can manually use 
 * {@code SessionManager} to persist it and restore it on next launch. Thus, all your 
 * application settings (GUI and not-GUI) will be stored in the same physical location, 
 * rather than e.g. storing GUI state in preferences (registry on Windows) and your 
 * preferences settings in some user directory.
 */
package net.guts.gui.session;
