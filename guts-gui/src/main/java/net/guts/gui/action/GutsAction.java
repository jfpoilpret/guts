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

package net.guts.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.google.inject.Inject;

/**
 * Embeds an {@link Action} to make it easier for a guts-gui based application to
 * internationalize and manage its actions.
 * <p/>
 * Each {@code GutsAction} instance must have a name, supposedly unique, that will be 
 * used as a key to find resources to inject into that action. if a {@code GutsAction}
 * doesn't have a name, guts-gui will try, through {@link ActionRegistrationManager} to
 * automatically assign one to it, but this works only for actions that are fields
 * of objects injected by Guice.
 * <p/>
 * {@code GutsAction} subclasses must implement method {@link #perform()} which will
 * be called every time the underlying {@link javax.swing.Action} is triggered.
 * <p/>
 * Typically, you would create a {@code GutsAction} subclass to define an action when 
 * you need it, but preferably as a field of a Guice-injected class instance. The reason
 * for that is {@code GutsAction}s must be registered with guts-gui framework (through
 * {@link ActionRegistrationManager}) for them to be functional (i.e. have their
 * resources injected for i18n, and automatically updated when current 
 * {@link java.util.Locale} changes); if you define and initialize {@code GutsAction} 
 * fields in a class that is instantiated or member-injected by Guice, then these
 * fields will automatically be registered by {@link ActionRegistrationManager}.
 * <p/>
 * In addition, when applying the MVC pattern, you can potentially define your 
 * {@code GutsAction}s as {@code public final} fields in the Controller, so that you 
 * can define them there (in MVC model, actions normally belong to Controllers) and 
 * refer to them in the View where they are needed (actions must be linked to some 
 * components -{@code JButton}, {@code JMenuItem}- of the View).
 * <p/>
 * In most cases, your {@code GutsAction} subclasses would be inlined into a "container"
 * class, rather than creating a named {@code GutsAction} subclass.
 * <p/>
 * For a given {@code GutsAction} (identified by its constructor-provided {@code name}),
 * resources are injected by {@link net.guts.gui.resource.ResourceInjector} from resource
 * bundles (properties or xml); the following snippet demonstrates the injectable 
 * resources for a given {@code GutsAction}:
 * <pre>
 * paste.text=&Paste
 * </pre>
 * <p/>
 * The list of injectable properties is follwoing:
 * <ul>
 * <li>{@code text}: the text of the action (can include the mnemonic, prefixed with 
 * {@code &} as in the above snippet)</li>
 * <li>{@code accelerator}: the code of a {@link javax.swing.KeyStroke} as accepted by
 * {@link javax.swing.KeyStroke#getKeyStroke(String)}</li>
 * <li>{@code toolTipText}</li>
 * <li>{@code smallIcon}, {@code largeIcon}: the icons to display for the action</li>
 * <li>{@code longDescription}: directly maps to 
 * {@link javax.swing.Action#LONG_DESCRIPTION} and could be used for contextual help</li>
 * </ul>
 * Note that, when a given component is set to a given action, you can always override 
 * action resources for that component:
 * <pre>
 * # resources of the action (used in menu bar and tool bar)
 * cut.text=Cut
 * cut.smallIcon=cut.jpg
 * # resources of the tool bar: remove text to only show the icon
 * toolbar-cut.text=
 * </pre>
 *
 * @author Jean-Francois Poilpret
 */
// CSOFF: AbstractClassNameCheck
abstract public class GutsAction
{
	/**
	 * Create a new {@code GutsAction} and assign it a {@code name} that will be used
	 * as a key for resource injection.
	 * 
	 * @param name the name used as a key for resource injection
	 */
	protected GutsAction(String name)
	{
		_name = name;
	}

	/**
	 * Create a new {@code GutsAction} without a name. A name will be automatically
	 * set by Guts-GUI before injecting resources into it, according to the naming
	 * policy defined by a Guice binding for {@link ActionNamePolicy}.
	 */
	protected GutsAction()
	{
		this(null);
	}

	/**
	 * Get the underlying {@link javax.swing.Action} so that you can bind it to a Swing 
	 * component (such as a {@link javax.swing.JButton} or a {@link javax.swing.JMenuItem}),
	 * or change its {@code enabled} state.
	 */
	final public Action action()
	{
		return _action;
	}

	/**
	 * Get the name that was given to {@code this} action at construction time.
	 */
	final public String name()
	{
		return _name;
	}

	/**
	 * Get the event that triggered the underlying {@link javax.swing.Action} of 
	 * {@code this} action. You normally don't need this method except in exceptional 
	 * situations, inside {@link #perform()} method.
	 * <p/>
	 * This method may also be used from {@link net.guts.gui.task.blocker.InputBlocker} 
	 * concrete implementations.
	 */
	final public ActionEvent event()
	{
		return _event;
	}

	/**
	 * This is called when the underlying {@link javax.swing.Action} is triggered. You
	 * must implement this method in your concrete actions. 
	 * <p/>
	 * If needed, you can call {@link #event()} to find out the root event that triggered
	 * the underlying {@link javax.swing.Action}.
	 * <p/>
	 * {@code perform()} is guaranteed to be called in the EDT, hence the usual warnings 
	 * apply: don't code long operations in this method. If long operations are to be 
	 * launched from this method, consider suclassing {@link TaskAction} or 
	 * {@link TasksGroupAction} instead.
	 */
	abstract protected void perform();

	// Internal method used by ActionRegistrationManager to automatically name 
	// action if necessary
	void name(String name)
	{
		if (_name == null)
		{
			_name = name;
		}
	}
	
	@SuppressWarnings("serial") 
	private class InternalAction extends AbstractAction
	{
		@Override public void actionPerformed(ActionEvent event)
		{
			_event = event;
			perform();
		}
	}
	
	@Override final public boolean equals(Object that)
	{
		return this == that;
	}

	@Override final public int hashCode()
	{
		return System.identityHashCode(this);
	}
	
	@Inject void init()
	{
		_injectedAlready = true;
	}

	// Used by ActionRegistrationManager to prevent Guice injection occurring twice
	boolean isMarkedInjected()
	{
		return _injectedAlready;
	}
	
	final private Action _action = new InternalAction();
	private String _name;
	private ActionEvent _event = null;
	
	private boolean _injectedAlready = false;
}
//CSON: AbstractClassNameCheck
