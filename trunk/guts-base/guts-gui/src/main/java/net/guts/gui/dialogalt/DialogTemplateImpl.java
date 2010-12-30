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

/*
 * Created on Aug 17, 2010
 * (c) 2010 Trumpet, Inc.
 *
 */
package net.guts.gui.dialogalt;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.google.inject.Inject;

/**
 * @author kevin
 */
public class DialogTemplateImpl extends DialogTemplate
{
	private final JPanel _viewContainer = new JPanel(new BorderLayout());
	private final JButton _okButton = new JButton();
	private final JButton _cancelButton = new JButton();
	private final JButton _revertButton = new JButton();
	private final JButton _applyButton = new JButton();

	private final List<ActionBinder> _actionBinders = new ArrayList<ActionBinder>();
	private final Action _okAction = new OkAction();
	private final Action _cancelAction = new CancelAction();
	private final Action _applyAction = new ApplyAction();
	private final Action _revertAction = new RevertAction();

	private ViewCloser _viewCloser = new NoOpViewCloser();

	private Action _externalApplyAction = new NoOpAction();
	private Action _externalRevertAction = new NoOpAction();

	private boolean _cancelled = false;

	@Inject public DialogTemplateImpl()
	{
		_okButton.setAction(_okAction);
		_cancelButton.setAction(_cancelAction);
		_revertButton.setAction(_revertAction);
		_applyButton.setAction(_applyAction);

		rebindActions();

		setLayout(new BorderLayout());
		add(_viewContainer, BorderLayout.CENTER);
		add(buildButtonView(), BorderLayout.SOUTH);

		// getRootPane().setDefaultButton(okButton);
	}

	private void rebindActions()
	{
		for (ActionBinder binder: _actionBinders)
		{
			binder.unbind();
		}
		_actionBinders.clear();

		_actionBinders.add(new ActionBinder(_applyAction, _okAction));
		_actionBinders.add(new ActionBinder(_externalApplyAction, _applyAction));
		_actionBinders.add(new ActionBinder(_externalRevertAction, _revertAction));
	}

	/**
	 * @see net.guts.gui.dialogalt.DialogTemplate#wasCancelled()
	 */
	@Override public boolean wasCancelled()
	{
		return _cancelled;
	}

	/**
	 * @see net.guts.gui.dialogalt.DialogTemplate#setApplyAction(javax.swing.Action)
	 */
	@Override public void setApplyAction(Action applyAction)
	{
		_externalApplyAction = applyAction;
		rebindActions();
	}

	/**
	 * @see net.guts.gui.dialogalt.DialogTemplate#setRevertAction(javax.swing.Action)
	 */
	@Override public void setRevertAction(Action revertAction)
	{
		_externalRevertAction = revertAction;
		rebindActions();
	}

	/**
	 * @see net.guts.gui.dialogalt.DialogTemplate#setView(javax.swing.JComponent)
	 */
	@Override public void setView(JComponent view)
	{
		if (_viewContainer.getComponentCount() > 0)
		{
			_viewContainer.remove(0);
		}

		_viewContainer.add(view, 0);
		validate();
		repaint();
	}

	/**
	 * @see net.guts.gui.dialogalt.DialogTemplate#setViewCloser(net.guts.gui.dialogalt.ViewCloser)
	 */
	@Override public void setViewCloser(ViewCloser viewCloser)
	{
		_viewCloser = viewCloser;
	}

	private JPanel buildButtonView()
	{
		JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		p.add(new JButton(_applyAction));
		p.add(new JButton(_revertAction));
		p.add(new JButton(_okAction));
		p.add(new JButton(_cancelAction));
		return p;
	}

	private class OkAction extends AbstractAction
	{
		{
			// ewww - figure out resource injection
			putValue(AbstractAction.NAME, "OK");
		}

		public void actionPerformed(ActionEvent e)
		{
			if (_viewCloser.doClose())
			{
				_applyAction.actionPerformed(e);
			}
		}
	}

	private class CancelAction extends AbstractAction
	{
		{
			// ewww - figure out resource injection
			putValue(AbstractAction.NAME, "Cancel");
		}

		public void actionPerformed(ActionEvent e)
		{
			if (_viewCloser.doClose())
			{
				_cancelled = true;
				_revertAction.actionPerformed(e);
			}
		}
	}

	private class RevertAction extends AbstractAction
	{
		{
			// ewww - figure out resource injection
			putValue(AbstractAction.NAME, "Revert");
		}

		public void actionPerformed(ActionEvent e)
		{
			_externalRevertAction.actionPerformed(e);
		}
	}

	private class ApplyAction extends AbstractAction
	{
		{
			// ewww - figure out resource injection
			putValue(AbstractAction.NAME, "Apply");
		}

		public void actionPerformed(ActionEvent e)
		{
			_externalApplyAction.actionPerformed(e);
		}
	}

	private static class ActionBinder implements PropertyChangeListener
	{
		private final Action _source;
		private final Action _target;

		public ActionBinder(Action source, Action target)
		{
			_source = source;
			_target = target;
			sync();
			source.addPropertyChangeListener(this);
		}

		private void sync()
		{
			_target.setEnabled(_source.isEnabled());
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			if ("enabled".equals(evt.getPropertyName()))
			{
				sync();
			}
		}

		public void unbind()
		{
			_source.removePropertyChangeListener(this);
		}
	}

	private static class NoOpViewCloser implements ViewCloser
	{
		public boolean doClose()
		{
			// do nothing
			return false;
		}
	}

	private static class NoOpAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			// do nothing
		}
	}
}
