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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.gui.action.GutsAction;
import net.guts.gui.action.GutsActionDecorator;
import net.guts.gui.dialog.ParentDialog;
import net.guts.gui.dialog.ParentDialogAware;
import net.guts.gui.dialog.layout.ButtonsPanelAdder;
import net.guts.gui.dialog.layout.ButtonsPanelAdderFactory;

/**
 * Abstract Panel that you can subclass for all your "main" panels,
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
 *         setName(ID);
 *         _label.setName(ID + "-label");
 *         _field.setName(ID + "-field");
 *         ...
 *         setLayout(new SomeLayout());
 *         add(_label);
 *         add(_field);
 *         ...
 *         // Register listeners
 *     }
 *     
 *     &#64;Override protected GutsAction getAcceptAction() {
 *         return _accept;
 *     }
 *     
 *     final private GutsAction _accept = new GutsAction() {
 *         &#64;Override protected void perform() {
 *             // Something to be done when user clicks OK
 *             ...
 *         }
 *     };
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
	@Override final public void init(ParentDialog parent)
	{
		_parent = parent;
		if (!_buttonsBarAdded)
		{
			finishInitialization();
			// Get list of actions, create buttons out of these, lay them out
			GutsAction accept = getAcceptAction();
			GutsAction cancel = getCancelAction();
			List<GutsAction> actions = new ArrayList<GutsAction>();
			setupActions(actions);
			if (cancel != null && !actions.contains(cancel))
			{
				actions.add(cancel);
			}
			if (accept != null && !actions.contains(accept))
			{
				actions.add(0, accept);
			}
	
			// Set escape button
			if (cancel != null)
			{
				getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
							KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
				getActionMap().put("cancel", cancel.action());
				// Force action name to "cancel"
				if (!CANCEL_ACTION.equals(cancel.name()))
				{
					int index = actions.indexOf(cancel);
					actions.set(index, new GutsActionDecorator(CANCEL_ACTION, accept));
				}
			}
	
			if (accept != null && !ACCEPT_ACTION.equals(accept.name()))
			{
				// Force action name to "ok"
				int index = actions.indexOf(accept);
				actions.set(index, new GutsActionDecorator(ACCEPT_ACTION, accept));
			}
			
			initButtons(actions);
			_buttonsBarAdded = true;
		}
	}
	
	protected void finishInitialization()
	{
	}

	private void initButtons(List<GutsAction> actions)
	{
		// Create all buttons that will be added at the bottom of the dialog
		List<JButton> buttons = new ArrayList<JButton>(actions.size());
		for (GutsAction action: actions)
		{
			if (action != null)
			{
				buttons.add(createButton(action));
			}
		}

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
	}

	private JButton createButton(GutsAction action)
	{
		if (action != null)
		{
			JButton button = new JButton(action.action());
			button.setName(getName() + "-" + action.name());
			return button;
		}
		else
		{
			return null;
		}
	}

	//TODO javadoc for all API methods!!!
	
	protected GutsAction getAcceptAction()
	{
		return null;
	}
	
	protected GutsAction getCancelAction()
	{
		return _cancel;
	}
	
	protected void setupActions(List<GutsAction> actions)
	{
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

	// Used for resource injection
	void setTitle(String title)
	{
		_parent.setDialogTitle(String.format(title, getTitleFormatArgs()));
	}

	//TODO javadoc
	static final protected String CANCEL_ACTION = "cancel";
	static final protected String ACCEPT_ACTION = "ok";
	
	static final private long serialVersionUID = 9182634284361356808L;
	static final private Object[] EMPTY_ARGS = new Object[0];

	final private GutsAction _cancel = new GutsAction(CANCEL_ACTION)
	{
		@Override protected void perform()
		{
			_parent.close(true);
		}
	};
	
	//TODO javadoc
	final protected Logger _logger = LoggerFactory.getLogger(getClass());

	private ParentDialog _parent;
	private boolean _buttonsBarAdded = false;
}
