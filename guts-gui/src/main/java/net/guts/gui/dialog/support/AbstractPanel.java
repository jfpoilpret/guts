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

package net.guts.gui.dialog.support;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.jdesktop.application.Action;
import org.jdesktop.application.Task;

import net.guts.gui.action.ActionManager;
import net.guts.gui.dialog.DefaultButtonHolder;
import net.guts.gui.dialog.ParentDialog;
import net.guts.gui.dialog.ParentDialogAware;
import net.guts.gui.dialog.TitleDialogProvider;
import net.guts.gui.dialog.layout.ButtonsPanelAdder;
import net.guts.gui.dialog.layout.ButtonsPanelAdderFactory;

import com.google.inject.Inject;

/**
 * Abstract Panel that you can (should?) subclass for all your "main" panels,
 * ie panels that are aimed to be set as content panes of a dialog (using
 * {@link net.guts.gui.dialog.DialogFactory#showDialog} methods).
 * <p/>
 * This class manages most repetitive stuff for you: creating OK/Cancel buttons
 * and associated Actions, inject resources into components from properties
 * files...
 * <p/>
 * Typically, your panels would look like:
 * <pre>
 * public class MyPanel extends AbstractPanel {
 *     static final private String ID = "my-panel-id";
 *     
 *     final private JLabel _label = new JLabel();
 *     final private JTextField _field = new JTextField();
 *     ...
 *     &#64;Inject private SomeService _service;
 *     ...
 *     
 *     public MyPanel() {
 *         super(ID);
 *         _label.setName(ID + "-label");
 *         _field.setName(ID + "-field");
 *         ...
 *         // Register listeners
 *     }
 *     
 *     &#64;Override protected void initLayout() {
 *         setLayout(new SomeLayout());
 *         add(_label);
 *         add(_field);
 *         ...
 *     }
 *     
 *     &#64;Override protected Task accept(ParentDialog parent) {
 *         // Something to be done when user clicks OK
 *         ...
 *     }
 * }
 * </pre>
 * <p/>
 * Note that this class won't work well if it uses a {@link java.awt.LayoutManager}
 * different than those supported by {@link ButtonsPanelAdderFactory}. Note that
 * you can register your own {@link ButtonsPanelAdder} implementation by calling
 * {@link ButtonsPanelAdderFactory#registerAdder(Class, ButtonsPanelAdder)} if
 * you want to {@code AbstractPanel} to support another 
 * {@link java.awt.LayoutManager}.
 * 
 * @author Jean-Francois Poilpret
 */
public abstract class AbstractPanel extends JPanel
	implements	DefaultButtonHolder,
				ParentDialogAware,
				TitleDialogProvider
{
	/**
	 * Constructs a new abstract panel, with a unique identifier, used for 
	 * resources internationalization.
	 * <p/>
	 * If this panel implements {@link Acceptor} then an "OK" button will be 
	 * created and {@link Acceptor#accept(ParentDialog)} will be called on
	 * {@code this} when the user clicks "OK"; otherwise, there will be no
	 * "OK" button in the dialog.
	 * 
	 * @param id unique identifier for this dialog panel
	 */
	protected AbstractPanel(String id)
	{
		setName(id);
		_acceptor = (this instanceof Acceptor ? (Acceptor) this : null);
	}

	/**
	 * Constructs a new abstract panel, with a unique identifier, used for 
	 * resources internationalization.
	 * 
	 * @param id unique identifier for this dialog panel
	 * @param acceptor the acceptor to call back when the user clicks the "OK"
	 * button; if {@code null}, then no "OK" button will be created.
	 */
	protected AbstractPanel(String id, Acceptor acceptor)
	{
		setName(id);
		_acceptor = acceptor;
	}

	/**
	 * Indicates which button is the default button dialog.
	 * <p/>
	 * By default, if the subclass defines an {@code accept()} action method,
	 * then the matching button will be the default, otherwise, the "Cancel"
	 * button will be the default. Override this method if you need more
	 * control on this behavior (e.g. if you have more than the 2 "OK" and
	 * "Cancel" buttons and one of your extra buttons should be the default).
	 * 
	 * @see DefaultButtonHolder#getDefaultButton()
	 */
	public JButton getDefaultButton()
	{
		if (_accept != null)
		{
			return _accept;
		}
		else
		{
			return _cancel;
		}
	}

	/**
	 * This method is called after the panel instance has been injected by
	 * Guice; it does nothing by default. 
	 * <p/>
	 * It should be overridden to layout this panel instance if this task 
	 * requires access to injected fields, otherwise it is always possible to 
	 * set the panel layout directly in the class constructor, although not 
	 * advised for the sake of consistency.
	 * <p/>
	 * Note that if you want to add buttons besides the usual "OK" and "Cancel"
	 * buttons (automatically added by {@code AbstractPanel}), then you should 
	 * not use this method but rather override 
	 * {@link #setupButtonsList(List, JButton, JButton)}.
	 * <p/>
	 * <b>CAUTION!</b> {@code initLayout()} is actually called <u>during</u> 
	 * Guice injection, not <u>after</u>, hence there is no guarantee that, 
	 * except for constructor-injection, all injection has been performed on
	 * {@code this}. As a rule of thumb, you should use only 
	 * constructor-injection for your dialog panels, to avoid potential problems.
	 * <p/>
	 * This word of caution may be relaxed in a future version (based on a new
	 * version of Guice).
	 */
	protected void initLayout()
	{
	}

	/**
	 * Creates 2 default buttons for the dialog panel: "OK" and "Cancel". 
	 * The "OK" button will be created <b>only if</b> an {@code @Action}
	 * method named {@code accept()} exists in this panel class.
	 * <p/>
	 * This method also takes care of mapping the "escape" key to the "Cancel"
	 * action.
	 */
	final private void initButtons()
	{
		_cancel = createButton("cancel", "cancel");
		_accept = (_acceptor != null ? createButton("ok", "ok") : null);

		// Set escape button
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		getActionMap().put("cancel", _cancel.getAction());
		
		// Add these and all buttons to the list of buttons that will be added
		// at the bottom of the dialog
		List<JButton> buttons = new ArrayList<JButton>();
		setupButtonsList(buttons, _accept, _cancel);

		// Make sure "ok" and "cancel" have been added to the list else add them
		if (_accept != null && !buttons.contains(_accept))
		{
			buttons.add(0, _accept);
		}
		if (!buttons.contains(_cancel))
		{
			buttons.add(_cancel);
		}

		// Add the buttons to the layout
		ButtonsPanelAdder adder = ButtonsPanelAdderFactory.getAdder(this);
		if (adder != null)
		{
			adder.addButtons(this, buttons);
		}
	}

	/**
	 * Sets up the list of buttons that must appear at the bottom of the dialog.
	 * Does nothing by default. {@code AbstractPanel} always makes sure that
	 * the "OK" and "Cancel" buttons are added to the dialog if needed.
	 * <p/>
	 * You need to override this method if you need to add your own buttons to
	 * this dialog. After creating the buttons from the {@code @Action} methods
	 * in your {@code AbstractPanel} subclass (you can use 
	 * {@link #createButton(String, String)} for that), you should add these to
	 * the {@code buttons} list.
	 * <p/>
	 * You may also add the 2 buttons {@code ok} and {@code cancel} passed as
	 * arguments to the list in the order you want them appear. If you don't add
	 * them, they will be automatically added: {@code ok} leftmost and 
	 * {@code cancel} rightmost.
	 * <p/>
	 * Overriding this method is particularly useful for creating new types of
	 * dialogs such as wizards (wizards generally have four buttons: "&lt;&lt;",
	 * "&gt;&gt;", "Finish" and "Cancel").
	 * 
	 * @param buttons list to which you add your own buttons
	 * @param ok reference to the automatically created "OK" button
	 * @param cancel reference to the automatically created "Cancel" button
	 */
	protected void setupButtonsList(
		List<JButton> buttons, JButton ok, JButton cancel)
    {
    }

	/**
	 * Returns a list of arguments that will be used when formatting the dialog
	 * title. The format must be found in the resources properties file.
	 * The format must follow {@link java.util.Formatter} rules.
	 * <p/>
	 * By default, an empty array of arguments is returned, which is suitable
	 * for any dialog title that is static (completely defined in the resources).
	 * <p/>
	 * You should <b>override</b> this method if the dialog title has to be
	 * formatted according to dynamic information.
	 * 
	 * @return the arguments that will be applied to the format in order to
	 * create the dialog title
	 */
	protected Object[] getTitleFormatArgs()
	{
		return EMPTY_ARGS;
	}

	/**
	 * Action called when the user clicks "Cancel". Simply closes the containing
	 * dialog.
	 */
	@Action final public void cancel()
	{
		_parent.close(true);
	}
	
	@Action(enabledProperty = ACCEPT_ENABLED)
	final public Task<?, ?> ok()
	{
		if (_acceptor != null)
		{
			return _acceptor.accept(_parent);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Creates an action and a button for it.
	 * <p/>
	 * The method names the new button based on the given name and the unique
	 * id of this panel: "{@code id-name}".
	 * <p/>
	 * If there is no action with the given name, the method does nothing.
	 * 
	 * @param name name of the new button
	 * @param actionName name of the action to create; a method with this name
	 * must exist and be annotated with {@code @Action}.
	 * @return the new button or {@code null} if {@code actionName} matches no
	 * real action method
	 */
	final protected JButton	createButton(String name, String actionName)
	{
		javax.swing.Action action = getAction(actionName);
		if (action != null)
		{
			JButton button = new JButton(action);
			button.setName(getName() + "-" + name);
			return button;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Creates an {@link javax.swing.Action} from an {@code @Action} annotated
	 * method in the class of this instance (or any superclass).
	 * <p/>
	 * This method should be called exclusively from one of the following
	 * overridden methods:
	 * <ul>
	 * <li>{@link #initLayout()}</li>
	 * <li>{@link #setupButtonsList(List, JButton, JButton)}</li>
	 * </ul>
	 * 
	 * @param action the name of the {@code @Action} annotated method
	 * @return the {@link javax.swing.Action} matching {@code action}, or 
	 * {@code null} if no match exists
	 */
	final protected javax.swing.Action getAction(String action)
	{
		return _actionManager.getAction(action, this);
	}

	/*
	 * (non-Javadoc)
	 * @see ParentDialogAware#setParent(ParentDialog)
	 */
	final public void setParent(ParentDialog parent)
	{
		_parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * @see TitleDialogProvider#getDialogTitle()
	 */
	final public String getDialogTitle()
	{
		return String.format(_title, getTitleFormatArgs());
	}

	/**
	 * This method is internally used for resource injection, it is not intended 
	 * for actual use. 
	 * <p/>
	 * Unfortunately it can't be made {@code private} because
	 * Swing Application Framework resource injection works only with
	 * {@code public} bean properties.
	 */
	final public void setTitle(String title)
	{
		_title = title;
	}

	/**
	 * This method is implicitly used by the {@link #ok()} method which 
	 * {@code @Action} is dynamically enabled based on this property.
	 */
	final public boolean isAcceptEnabled()
	{
		return _enabled;
	}

	/**
	 * Enables or disables the {@code @Action public void accept()} action.
	 * Call this method according to current user input (e.g. through a
	 * {@link javax.swing.event.DocumentListener} on a mandatory field).
	 */
	final protected void setAcceptEnabled(boolean enabled)
	{
		boolean old = _enabled;
		_enabled = enabled;
		firePropertyChange(ACCEPT_ENABLED, old, enabled);
	}

	//FIXME there is a risk calling overridden methods (initLayout()...)
	// before the instance is fully injected by Guice! Only full constructor-
	// injected instances are safely used. At least this should be documented
	// in all called overridable methods
	@Inject final void init(ActionManager actionManager)
	{
		// Initialize all properties
		_actionManager = actionManager;
		
		// Setup layout for buttons and internationalize the whole panel
		initLayout();
		initButtons();
	}

	static final private long serialVersionUID = 9182634284361356808L;

	static final private String ACCEPT_ENABLED = "acceptEnabled";
	static final private Object[] EMPTY_ARGS = new Object[0];

	final private Acceptor _acceptor;
	private ActionManager _actionManager;
	private ParentDialog _parent;
	private String _title = "";
	private boolean _enabled =  true;
	private JButton _cancel;
	private JButton _accept;
}
