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

package net.guts.gui.dialog.impl;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import net.guts.gui.dialog.Closable;
import net.guts.gui.dialog.DefaultButtonHolder;
import net.guts.gui.dialog.ParentDialog;
import net.guts.gui.dialog.ParentDialogAware;
import net.guts.gui.dialog.TitleDialogProvider;

/**
 * All dialogs shown by {@link net.guts.gui.dialog.DialogFactory} are 
 * instances of this class.
 * <p/>
 * {@code GDialog} provides resource injection, manages dialog's close box,
 * dialog's default button, and tracks if dialog was cancelled.
 * 
 * @author Jean-Francois Poilpret
 */
final class GDialog extends JDialog implements ParentDialog
{
	public GDialog(JDialog parent, JComponent panel)
	{
		super(parent, true);
		_panel = panel;
	}
	
	public GDialog(JFrame parent, JComponent panel)
	{
		super(parent, true);
		_panel = panel;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.dialog.ParentDialog#close(boolean)
	 */
	public void close(boolean cancelled)
	{
		_cancelled = cancelled;
		setVisible(false);
		dispose();
	}

	// Used by DefaultDialogFactory.showDialog()
	public boolean wasCancelled()
	{
		return _cancelled;
	}
	
	/**
	 * Overridden to get title from embedded panel if it implements
	 * {@link TitleDialogProvider}.
	 */
	@Override public String getTitle()
	{
		if (_title == null)
		{
			if (_panel instanceof TitleDialogProvider)
			{
				_title = ((TitleDialogProvider) _panel).getDialogTitle();
			}
			else
			{
				_title = super.getTitle();
			}
			if (_title == null)
			{
				_title = "";
			}
		}
		return _title;
	}
	
	public void init()
	{
		String name = _panel.getName();
		setName(name + "-dialog");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new CloseListener());
		if (_panel instanceof ParentDialogAware)
		{
			((ParentDialogAware) _panel).setParent(this);
		}

		setContentPane(_panel);
		if (_panel instanceof DefaultButtonHolder)
		{
			JButton button = ((DefaultButtonHolder) _panel).getDefaultButton();
			if (button != null)
			{
				getRootPane().setDefaultButton(button);
			}
		}
	}

	private class CloseListener extends WindowAdapter
	{
		@Override public void windowClosing(WindowEvent event)
		{
			if (	!(_panel instanceof Closable)
				||	((Closable) _panel).canClose())
			{
				setVisible(false);
				dispose();
			}
		}
	}

	private static final long serialVersionUID = -5936651916244078811L;

	final private JComponent _panel;
	private boolean	_cancelled = true;
	private String _title = null;
}
