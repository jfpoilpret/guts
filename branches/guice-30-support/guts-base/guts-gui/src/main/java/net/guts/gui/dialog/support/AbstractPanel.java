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
 * If you use <a href="net/guts/gui/naming/package-summary.html">automatic 
 * components naming</a>, you don't even have to use {@code setName()} from
 * the above snippet.
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
	/**
	 * This method is an implementation detail but {@code public} due to
	 * it implementing {@link ParentDialogAware} interface. <b>Never</b> call
	 * this method directly; it will be automatically called, when needed, by
	 * {@link net.guts.gui.dialog.DialogFactory}.
	 */
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
				getActionMap().put("cancel", cancel);
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

	/**
	 * This method is called after {@code this} panel has been fully constructed
	 * and injected by Guice, including resource injection and automatic component
	 * naming (if enabled); it gives you a chance to complete panel initialization 
	 * that could not possibly performed earlier (ie in the constructor).
	 * <p/>
	 * When called, default buttons (OK, Cancel) have not yet been added to 
	 * {@code this} panel.
	 * <p/>
	 * It does nothing by default, but should be overridden, should further 
	 * initialization occur for your own subclasses.
	 */
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
			JButton button = new JButton(action);
			button.setName(getName() + "-" + action.name());
			return button;
		}
		else
		{
			return null;
		}
	}

	/**
	 * This method is automatically called during initialization of {@code this}
	 * panel. It must indicate which action is to be executed when user clicks
	 * the "OK" button.
	 * <p/>
	 * By default, it returns {@code null}, meaning that no "OK" button will be
	 * visible for {@code this} panel.
	 * <p/>
	 * If you want an "OK" button to appear, then override this method so that it 
	 * returns a non-{@code null} action.
	 * <p/>
	 * Note that this method, if called several times, must always return the
	 * same result, otherwise results are unpredictable.
	 * <p/>
	 * In general, the returned action, in addition to the expected work to be
	 * performed by the "OK" button in this panel, would normally close the parent
	 * dialog; this is shown in the following snippet:
	 * <pre>
	 *     &#64;Override protected GutsAction getAcceptAction() {
	 *         return _accept;
	 *     }
	 *     
	 *     final private GutsAction _accept = new GutsAction() {
	 *         &#64;Override protected void perform() {
	 *             // Something to be done when user clicks OK
	 *             ...
	 *             // Close the parent dialog
	 *             getParentDialog().close(false);
	 *         }
	 *     };
	 * </pre>
	 * <p/>
	 * Whatever the name of the returned {@link GutsAction}, Guts-GUI will
	 * override it to just {@code "ok"}, so that general resources can be used for
	 * all dialogs by default:
	 * <pre>
	 * ok.text = &amp;OK
	 * </pre>
	 * However, this can be changed for a specific dialog, by defining resources
	 * for the {@link JButton} that will be created by {@code AbstractPanel} for
	 * this action. That button is named {@code "panelName-ok"} where {@code panelName}
	 * is the name of {@code this} panel (as set by {@code setName()} or automatically
	 * by using {@link net.guts.gui.naming.ComponentNamingModule}.
	 * 
	 * @see #getParentDialog()
	 * @see ParentDialog#close(boolean)
	 */
	protected GutsAction getAcceptAction()
	{
		return null;
	}
	
	/**
	 * This method is automatically called during initialization of {@code this}
	 * panel. It should indicate which action is to be executed when user clicks
	 * the "Cancel" button.
	 * <p/>
	 * By default, it returns an action that will simply close the parent dialog
	 * of {@code this} panel.
	 * <p/>
	 * If you don't want a "Cancel" button to appear, then override this method 
	 * so that it returns {@code null}.
	 * <p/>
	 * Note that this method, if called several times, must always return the
	 * same result, otherwise results are unpredictable.
	 * <p/>
	 * In general, you won't need to override this method; if you do override it,
	 * then make sure to close the parent dialog of {@code this} panel.
	 * <p/>
	 * Whatever the name of the returned {@link GutsAction}, Guts-GUI will
	 * override it to just {@code "cancel"}, so that general resources can be 
	 * used for all dialogs by default:
	 * <pre>
	 * cancel.text = Ca&amp;ncel
	 * </pre>
	 * However, this can be changed for a specific dialog, by defining resources
	 * for the {@link JButton} that will be created by {@code AbstractPanel} for
	 * this action. That button is named {@code "panelName-cancel"} where {@code panelName}
	 * is the name of {@code this} panel (as set by {@code setName()} or automatically
	 * by using {@link net.guts.gui.naming.ComponentNamingModule}.
	 * 
	 * @see #getParentDialog()
	 * @see ParentDialog#close(boolean)
	 */
	protected GutsAction getCancelAction()
	{
		return _cancel;
	}

	/**
	 * This method is automatically called during initialization of {@code this}
	 * panel. It should indicate whether additional buttons should be added to the
	 * button bar at the bottom of the dialog.
	 * <p/>
	 * By default, this method does nothing, which means that the bottom button bar
	 * will contain at most 2 buttons, "OK" and "Cancel", based on actions returned
	 * by {@link #getAcceptAction()} and {@link #getCancelAction()}.
	 * <p/>
	 * You can override this method if you want additional buttons to appear there;
	 * in this case, you just have to add {@link GutsAction}s to {@code actions},
	 * in the order they must appear in the button bar.
	 * <p/>
	 * {@code AbstractPanel} will automatically add "OK" (leftmost) and "Cancel" 
	 * (rightmost) to the button bar. This order can be changed by simply adding
	 * the "OK" or "Cancel" actions (as returned by {@link AbstractPanel#getAcceptAction()}
	 * and {@link #getCancelAction()}) to {@code actions}, at the position you want
	 * them to appear.
	 * <p/>
	 * The following snippet is an excerpt of {@link AbstractWizardPanel#setupActions(List)},
	 * showing how to add new buttons and change the default positions of "OK" and "
	 * Cancel":
	 * <pre>
	 *     &#64;Override final protected void setupActions(List&lt;GutsAction&gt; actions)
	 *     {
	 *         actions.add(_previous);
	 *         actions.add(_next);
	 *         actions.add(getAcceptAction());
	 *         actions.add(getCancelAction());
	 *     }
	 * </pre>
	 * 
	 * @param actions list of actions to be added to the bottom button bar of {@code this}
	 * panel; it is initially empty, meaning that no action is to be added.
	 */
	protected void setupActions(List<GutsAction> actions)
	{
	}

	/**
	 * Return the actual parent dialog in which {@code this} panel is embedded. This 
	 * can be used for closing the dialog, or setting up the default button.
	 * <p/>
	 * This method can't be called from the constructor as no dialog is embedding the
	 * panel at that time; it can be called at any time, from 
	 * {@link #finishInitialization()} method or any method called after it.
	 * Of course, it can be called from any actions in the panel.
	 * 
	 * @return the parent dialog {@code this} panel is currently embedded in
	 */
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

	/**
	 * Name of "cancel" action, used for resource injection. You will normally
	 * not need to use this constant, as {@code AbstractPanel} always enforces
	 * the correct name of "cancel" action.
	 */
	static final protected String CANCEL_ACTION = "cancel";

	/**
	 * Name of "OK" action, used for resource injection. You will normally
	 * not need to use this constant, as {@code AbstractPanel} always enforces
	 * the correct name of "OK" action.
	 */
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

	/**
	 * Logger to use for any logging need from your {@code AbstractPanel} subclass.
	 */
	final protected Logger _logger = LoggerFactory.getLogger(getClass());

	private ParentDialog _parent;
	private boolean _buttonsBarAdded = false;
}