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

package net.guts.gui.dialog;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

final class GDialog extends JDialog implements ParentDialog
{
	GDialog(JDialog parent, JComponent panel)
	{
		super(parent, true);
		_panel = panel;
	}
	
	GDialog(JFrame parent, JComponent panel)
	{
		super(parent, true);
		_panel = panel;
	}
	
	public void close(boolean cancelled)
	{
		_cancelled = cancelled;
		setVisible(false);
		dispose();
	}

	public void setDefaultButton(JButton button)
	{
		if (button != null && _panel.isAncestorOf(button))
		{
			getRootPane().setDefaultButton(button);
		}
	}

	public void setDialogTitle(String title)
	{
		setTitle(title);
	}

	// Used by DefaultDialogFactory.showDialog()
	boolean wasCancelled()
	{
		return _cancelled;
	}
	
	void init()
	{
		String name = _panel.getName();
		setName(name + "-dialog");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(_listener);
		if (_panel instanceof ParentDialogAware)
		{
			((ParentDialogAware) _panel).setParentDialog(this);
		}
		setContentPane(_panel);
	}

	static private class CloseListener extends WindowAdapter
	{
		@Override public void windowClosing(WindowEvent event)
		{
			GDialog dialog = (GDialog) event.getComponent();
			Component panel = dialog.getContentPane();
			if (	!(panel instanceof Closable)
				||	((Closable) panel).canClose())
			{
				dialog.setVisible(false);
				dialog.dispose();
			}
		}
	}

	private static final long serialVersionUID = -5936651916244078811L;

	static final private CloseListener _listener = new CloseListener();
	final private JComponent _panel;
	private boolean	_cancelled = true;
}
