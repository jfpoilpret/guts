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

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.gui.action.GutsAction;
import net.guts.gui.dialog.ParentDialog;
import net.guts.gui.dialog.ParentDialogAware;
import net.guts.gui.dialog.layout.ButtonsPanelAdder;
import net.guts.gui.dialog.layout.ButtonsPanelAdderFactory;

import com.google.inject.Inject;

/**
 * Abstract Panel that you can (should?) subclass for all your "main" panels,
 * ie panels that are aimed to be set as content panes of a dialog (using
 * {@link net.guts.gui.dialog.DialogFactory} {@code showDialog} methods).
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
public abstract class AbstractPanel extends JPanel implements ParentDialogAware
{
	//TODO move some of the doc below into the class javadoc
	/**
	 * Constructs a new abstract panel, with a unique identifier, used for 
	 * resources internationalization.
	 * <p/>
	 * You must pass the {@code accept} action that will be mapped to the 
	 * default "OK" button created and laid out by the panel. 
	 * {@code AbstractPanel} always adds a "Cancel" button which cannot be
	 * removed. The action performed on "Cancel" cannot be changed either,
	 * however, you can override {@link #cancel()} that is called before closing
	 * the parent dialog.
	 * <p/>
	 * If you have other buttons to be added to the buttons bar, then you should
	 * pass them in {@code actions}; {@code AbstractPanel} will automatically
	 * create buttons from the passed {@link GutsAction}s and add them to the bar.
	 * <p/>
	 * If {@code actions} also contains {@code accept}, then its position in the 
	 * list will be used to lay out the whole buttons bar; if not, then the "OK" 
	 * button will always be laid out on the left. The "Cancel" button position 
	 * cannot be changed, it is always laid out on the right.
	 * 
	 * @param id unique identifier for this dialog panel
	 */
	protected AbstractPanel(String id)
	{
		setName(id);
	}
	
	@Inject void checkConstructionComplete()
	{
		if (!_initButtonsCalled)
		{
			_logger.error("initButtons() was not called from {}'s constructor",
				getClass().getSimpleName());
		}
	}
	
	/**
	 * Creates 2 default buttons for the dialog panel: "OK" and "Cancel". 
	 * The "OK" button will be created <b>only if</b> an {@code @Action}
	 * method named {@code accept()} exists in this panel class.
	 * <p/>
	 * This method also takes care of mapping the "escape" key to the "Cancel"
	 * action.
	 */
	final protected void initButtons(AcceptGutsAction accept, GutsAction... actions)
	{
		_ok = validateAcceptAction(accept);
		JButton btnCancel = createButton(_cancel);
		JButton btnAccept = (_ok != null ? createButton(_ok) : null);

		// Set escape button
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		getActionMap().put("cancel", btnCancel.getAction());
		
		// Add these and all buttons to the list of buttons that will be added
		// at the bottom of the dialog
		boolean hasAccept = false;
		List<JButton> buttons = new ArrayList<JButton>();
		for (GutsAction action: actions)
		{
			if (action != null)
			{
				if (action != _ok)
				{
					buttons.add(createButton(action));
				}
				else
				{
					hasAccept = true;
					buttons.add(btnAccept);
				}
			}
		}
		// Make sure "ok" and "cancel" have been added to the list else add them
		if (!hasAccept)
		{
			buttons.add(0, btnAccept);
		}
		buttons.add(btnCancel);

		// Add the buttons to the layout
		// NB: don't try to replace this with an injected Map, it would have to be static!
		ButtonsPanelAdder adder = ButtonsPanelAdderFactory.getAdder(this);
		if (adder != null)
		{
			adder.addButtons(this, buttons);
		}
		else
		{
			_logger.warn("initButtons(): No ButtonsPanelAdder for {} LayoutManager",
				getLayout().getClass());
		}
		_initButtonsCalled = true;
	}

	protected AcceptGutsAction validateAcceptAction(AcceptGutsAction accept)
	{
		return accept;
	}
	
	/**
	 * Creates a button for the given {@code gutsAction}.
	 * <p/>
	 * The method names the new button based on the given name and the unique
	 * id of this panel: "{@code id-name}" where {@code name} is 
	 * {@code gutsAction.name()}.
	 * 
	 * @param gutsAction the action for which to create a new button; if {@code null},
	 * the method returns {@code null}.
	 * @return the new button or {@code null} if {@code gutsAction} is {@code null}
	 */
	final protected JButton	createButton(GutsAction gutsAction)
	{
		Action action = gutsAction.action();
		if (action != null)
		{
			JButton button = new JButton(action);
			button.setName(getName() + "-" + gutsAction.name());
			return button;
		}
		else
		{
			return null;
		}
	}

	@Override final public void setParentDialog(ParentDialog parent)
	{
		_parent = parent;
		//TODO hook for subclasses to do something when parent dialog is known
	}

	final protected ParentDialog getParentDialog()
	{
		return _parent;
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

	protected void cancel()
	{
	}
	
	protected AcceptGutsAction acceptAction()
	{
		return _ok;
	}
	
	// Used for resource injection
	void setTitle(String title)
	{
		getParentDialog().setDialogTitle(String.format(title, getTitleFormatArgs()));
	}
	
	static final private long serialVersionUID = 9182634284361356808L;
	static final private Object[] EMPTY_ARGS = new Object[0];

	final private GutsAction _cancel = new GutsAction("cancel")
	{
		@Override protected void perform()
		{
			cancel();
			_parent.close(true);
		}
	};
	
	final protected Logger _logger = LoggerFactory.getLogger(getClass());

	private AcceptGutsAction _ok;
	private ParentDialog _parent;
	private boolean _initButtonsCalled = false;
}
