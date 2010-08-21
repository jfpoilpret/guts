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

 import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

import net.guts.gui.action.GutsAction;
import net.guts.gui.exit.ExitController;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This class holds all {@code GutsAction}s generally used in an application:
 * <ul>
 * <li>{@code quit}</li>
 * <li>{@code cut}</li>
 * <li>{@code copy}</li>
 * <li>{@code paste}</li>
 * </ul>
 * {@code GutsApplicationActions} automatically handles enabling of {@code cut},
 * {@code copy} and {@code paste} actions based on existing text selection in the
 * currently focused component, and on the content of the clipboard.
 * <p/>
 * This class is actually a singleton that is initialized by Guice and is thus
 * injectable where you need it.
 * <p/>
 * To add actions from {@code GutsApplicationActions} into a menu item, you can
 * proceed as in the following snippet:
 * <pre>
 * &#64;Inject void init(MenuFactory factory, GutsApplicationAction actions)
 * {
 *     JMenuBar menuBar = new JMenuBar();
 *     ...
 *     menuBar.add(factory.createMenu(
 *         "editMenu", actions.cut(), actions.copy(), actions.paste()));
 *     ...
 * }
 * </pre>
 *
 * @author Jean-Francois Poilpret
 */
@Singleton
public class GutsApplicationActions
{
	@Inject GutsApplicationActions(ExitController controller)
	{
		_controller = controller;
		_clipboard.addFlavorListener(new FlavorListener()
		{
			@Override public void flavorsChanged(FlavorEvent e)
			{
				clipboardChanged();
			}
		});
		_focusManager.addPropertyChangeListener(FOCUS_OWNER_KEY, new PropertyChangeListener()
		{
			@Override public void propertyChange(PropertyChangeEvent evt)
			{
				focusOwnerChanged();
			}
		});
	}

	/**
	 * Returns the {@code "quit"} action which, when triggered, initiates an
	 * application shutdown through {@link ExitController#shutdown()}.
	 */
	public GutsAction quit()
	{
		return _quit;
	}

	/**
	 * Returns the {@code "cut"} action which, when triggered, cuts the selected 
	 * text of the currently focused component. If there's no current text selection
	 * or focused component, then the action is automatically disabled.
	 */
	public GutsAction cut()
	{
		return _cut;
	}
	
	/**
	 * Returns the {@code "copy"} action which, when triggered, copies the selected 
	 * text of the currently focused component. If there's no current text selection
	 * or focused component, then the action is automatically disabled.
	 */
	public GutsAction copy()
	{
		return _copy;
	}
	
	/**
	 * Returns the {@code "paste"} action which, when triggered, pastes the text from
	 * the clipboard into the currently focused text component. If there's no text in
	 * the clipboard, or no focused text component, then the action is automatically 
	 * disabled.
	 */
	public GutsAction paste()
	{
		return _paste;
	}

	private void focusOwnerChanged()
	{
		if (_focus != null)
		{
			_focus.removeCaretListener(_selectionListener);
		}
		_focus = null;
		Component focus = _focusManager.getPermanentFocusOwner();
		if (focus instanceof JTextComponent)
		{
			_focus = (JTextComponent) focus;
			_focus.addCaretListener(_selectionListener);
			selectionChanged();
		}
		updateState();
	}
	
	private void clipboardChanged()
	{
		_clipboardHasText = _clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);
		updateState();
	}
	
	private void selectionChanged()
	{
		_hasSelection = (_focus.getSelectedText() != null);
		updateState();
	}

	private void updateState()
	{
		if (_focus == null)
		{
			_cut.setEnabled(false);
			_copy.setEnabled(false);
			_paste.setEnabled(false);
		}
		else
		{
			boolean enabled = _focus.isEnabled();
			boolean editable = enabled && _focus.isEditable();
			_cut.setEnabled(_hasSelection && editable);
			_copy.setEnabled(_hasSelection && enabled);
			_paste.setEnabled(_clipboardHasText && editable);
		}
	}
	
	final private GutsAction _quit = new GutsAction("quit")
	{
		@Override protected void perform()
		{
			_controller.shutdown();
		}
	};
	
	final private GutsAction _cut = new GutsAction("cut")
	{
		@Override protected void perform()
		{
			_focus.cut();
		}
	};

	final private GutsAction _copy = new GutsAction("copy")
	{
		@Override protected void perform()
		{
			_focus.copy();
		}
	};

	final private GutsAction _paste = new GutsAction("paste")
	{
		@Override protected void perform()
		{
			_focus.paste();
		}
	};
	
	static final private String FOCUS_OWNER_KEY = "permanentFocusOwner";
	
	final private KeyboardFocusManager _focusManager = 
		KeyboardFocusManager.getCurrentKeyboardFocusManager();
	final private Clipboard _clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	final private CaretListener _selectionListener = new CaretListener()
	{
		@Override public void caretUpdate(CaretEvent e)
		{
			selectionChanged();
		}
	};
	final private ExitController _controller;
	private JTextComponent _focus = null;
	private boolean _hasSelection = false;
	private boolean _clipboardHasText = false;
}
