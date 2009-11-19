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
 * This package contains the Guts-GUI API for resource injection.
 * <p/><a name="rsrc1"></a><h3>Resources storage</h3>
 * Before discussing the API, it is important to understand how and where resources
 * must be stored when using Guts-GUI.
 * <p/>
 * Guts-GUI expects resources stored as {@code String} values in resource bundles,
 * which are simple Java properties files containing {@code (key, value)} pairs:
 * <pre>
 * mainFrame.iconImage=icons/main.png
 * contact-first-name-label.text=&amp;First Name
 * contact-last-name-label.text=&amp;Last Name
 * contact-birth-label.text=&amp;Birth Date
 * </pre>
 * In the sample above, there are a few important points to notice:
 * <ul>
 * <li>Keys are made of 2 parts, separated by a dot, as in 
 * {@code contact-first-name-label.text}: the left part is the unique name of the 
 * object into which resources are to be injected (in this case, a 
 * {@link javax.swing.JLabel}), the right part is the property, in that object,
 * that will be injected with the value (here {@code &First Name}</li>
 * <li>The value (on the right of the {@code "="} sign) is a string constant that includes a 
 * special convention to determine an optional mnemonic character (as used in 
 * {@link javax.swing.JLabel#setDisplayedMnemonic(int)}): the mnemonic character is
 * prefixed by {@code "&"}.</li>
 * <li>Resource injection is not limited to text but includes icons (as seen in the snippet
 * above with the {@code mainFrame.iconImage} property); for those kinds of resources, such
 * as {@link javax.swing.Icon}, you can specify a path to a file that can be converted
 * into the required type; that file must be accessible in the classpath.</li>
 * </ul>
 * For Swing components, the unique name to use in the properties file is the name
 * set on each component through {@link java.awt.Component#setName}. Although you are
 * free to name your components the way you like, it is advised you follow some strict
 * conventions in order to ensure uniqueness of names across the whole application.
 * <p/>
 * One suggested convention is to give unique names to every {@link javax.swing.JPanel},
 * and then use this name as a prefix for the name of all components inside the panel, as
 * in {@code PanelName-ComponentName}.
 * <p/>
 * For objects other than Swing components, they must be given a name directly through
 * {@link net.guts.gui.resource.ResourceConverter} API (see below). Optionally, they 
 * will be given, as a default name, the simple name of their class (i.e. 
 * {@link java.lang.Class#getSimpleName()}).
 * <p/>
 * As seen above, some values are to be interpreted as path to some file (such as a 
 * {@code ".png"} image) and then converted into some compatible type (such as 
 * {@code Icon}). Paths can be relative or absolute:
 * <ul>
 * <li>absolute path (starts with {@code "/"}): the file is search with this exact
 * location inside the classpath</li>
 * <li>relative path: the file is search within a directory relative to the current
 * location of the resource bundle file where the property is defined</li>
 * </ul>
 * 
 * <p/><a name="rsrc2"></a><h3>Resources searching principles</h3>
 * As when using Java {@link java.util.ResourceBundle}s, Guts-GUI will search the most
 * specific bundle for the current {@link java.util.Locale} based on the file name, e.g.
 * for a bundle named {@code "resources"}, and french {@code Locale} being current,
 * resources from the file with name {@code "resources_fr.properties"} would take 
 * precedence over those defined in {@code "resources.properties"} in the same location.
 * <p/>
 * Guts-GUI allows you to define a so-called "root bundle" (through 
 * {@link net.guts.gui.resource.Resources#bindRootBundle}) that will be used with highest 
 * precedence for <b>any</b> resource injection. You can possibly use this root bundle to 
 * define all your resources for your whole application (you still have to provide one file 
 * per supported {@link java.util.Locale}).
 * <p/>
 * But Guts-GUI also allows you to define a chain of resource bundles for a given class
 * (or all classes in a given package) to be injected with resources.
 * <p/>
 * A class, generally a subclass of {@link javax.swing.JPanel} but not necessarily, 
 * which instances should be injected with external resources, can explicitly declare
 * a list of bundles to be used when looking for properties to inject into that class;
 * for this it uses the {@link net.guts.gui.resource.UsesBundles} annotation to define the 
 * location of bundles to use:
 * <pre>
 * &#64;UsesBundles("MyPanel")
 * public class MyPanel extends JPanel
 * {
 *     ...
 * }
 * </pre>
 * In the snippet above, we define a bundle, named {@code "MyPanel.properties"}, located
 * in the same package as {@code MyPanel} class, where we define default values for
 * properties that will be used to inject into {@code MyPanel} instances.
 * <p/>
 * Note that those default properties values can always be overridden inside the 
 * "root bundle".
 * <p/>
 * {@link net.guts.gui.resource.UsesBundles} annotation also accepts absolute paths to 
 * resource bundles. It is also possible to add several resource bundles with it, ordered 
 * by precedence (i.e. the first bundle in the list will override properties from the 
 * second in the list, and so on).
 * <p/>
 * This possibility to associate classes with a bundle is useful in various use cases.
 * <p/>
 * One use case is for suppliers of 3rd-party GUI components who want to package these
 * components with resource bundles. In this situation they can annotate components with
 * {@code @UsesBundles} to use the packaged bundle. For end users of these components,
 * it is always possible to override these resources by overriding them in the application
 * "root bundle".
 * <p/>
 * A second use case deals with modular applications, using plugins that embed features
 * along with associated UI. Plugins can come packaged with their own resource bundles
 * and they can be dynamically added to the application without the need to modify the
 * application or its root bundle.
 * 
 * <p/><a name="rsrc3"></a><h3>Resources injection</h3>
 * The API to perform resource injection into a given object is defined by 
 * {@link net.guts.gui.resource.ResourceInjector} which defines a couple of methods to 
 * inject GUI components or hierarchies of components, and also any object of any type.
 * <p/>
 * For your UI components, if you use Guts-GUI as a whole framework for your application,
 * then you won't have to explicitly call {@code ResourceInjector}: 
 * {@link net.guts.gui.application.WindowController} automatically calls it for you when
 * you display a window.
 * <p/>
 * Injection of every single resource into an object's property is normally done through
 * the Java Bean setter for that property.
 * <p/>
 * More specific ways to inject resources into one object can also be defined (see 
 * <a href="#rsrc5"></a> below for more details).
 * <p/>
 * If you need to inject other objects than GUI components inside windows, then you need
 * to get a reference to {@link net.guts.gui.resource.ResourceInjector} and perform direct 
 * calls to its methods; {@code ResourceInjector} can be injected by Guice in the classes 
 * that need it:
 * <pre>
 * public class SomeClass
 * {
 *     &#64;Inject public SomeClass(ResourceInjector injector)
 *     {
 *         injector.injectInstance(this, "somename");
 *     }
 *     ...
 * }
 * </pre>
 * If you don't use Guts-GUI framework for your application but still want to use
 * {@link net.guts.gui.resource.ResourceInjector}, then you should make sure, when you 
 * create a Guice {@link com.google.inject.Injector}, to pass 
 * {@link net.guts.gui.resource.ResourceModule} in the list of
 * {@link com.google.inject.Module}s.
 * 
 * <p/><a name="rsrc4"></a><h3>Support for different types of resources</h3>
 * Guts-GUI is able to convert string values, read from resource bundles, into various 
 * types, as required by the property to be injected into the object, target of resource
 * injection.
 * <p/>
 * The currently Guts-GUI supported types of conversions are listed in 
 * {@link net.guts.gui.resource.ResourceModule}.
 * <p/>
 * If you need to support conversion to another type, you can implement your own
 * {@link net.guts.gui.resource.ResourceConverter} for a given type and register it 
 * with Guts-GUI through one of both {@link net.guts.gui.resource.Resources#bindConverter} 
 * methods.
 * <p/>
 * Guts-GUI also defines a generic {@link net.guts.gui.resource.EnumConverter} class that 
 * allows you to support conversion of a string value into a given {@code enum} instance.
 * 
 * <p/><a name="rsrc5"></a><h3>Support for special injection (other than bean setters)</h3>
 * Guts-GUI is able to inject any kind of resource into any property of any object, 
 * provided that this property has public getter and setter methods, named according
 * to Java Beans specifications.
 * <p/>
 * Unfortunately, sometimes simply using property setters is not an option, or a
 * single resource may need to be broken down and injected into several properties
 * (a typical example is a text with a mnemonic marker to be injected in 
 * {@link javax.swing.JLabel}); for this reason, Guts-GUI has a very open design that
 * allows to create and register special {@link net.guts.gui.resource.InstanceInjector}s 
 * to deal with specific types of objects into which resources are to be injected.
 * <p/>
 * TODO InstanceInjector example, {@link net.guts.gui.resource.Resources#bindInstanceInjector}
 * <p/>
 * Guts-GUI supported types of components are listed in 
 * {@link net.guts.gui.resource.ResourceModule}.
 */
package net.guts.gui.resource;
