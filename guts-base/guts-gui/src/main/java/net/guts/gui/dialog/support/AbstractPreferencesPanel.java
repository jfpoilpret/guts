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
 * Created on Aug 16, 2010
 * (c) 2010 Trumpet, Inc.
 *
 */
package net.guts.gui.dialog.support;

import java.awt.BorderLayout;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.guts.gui.action.GutsAction;
import net.guts.gui.resource.ResourceInjector;

import com.google.inject.Inject;

/**
 * TODO need to release bindings when view is closed (this requires reliably close notifications
 * from parent dialog)
 */
abstract public class AbstractPreferencesPanel extends AbstractMultiPanel
{
	@Inject private ResourceInjector _resourceInjector;

	private final JPanel _viewContainer = new JPanel(new BorderLayout());
	private final JSplitPane _split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private final DefaultListModel _listModel = new DefaultListModel();
	private final List<ViewRegistration> _registrations = new ArrayList<ViewRegistration>();
	private final JList _optionsList = new JList(_listModel);

	public AbstractPreferencesPanel()
	{
		_optionsList.setSelectionModel(new ExactlyOneListSelectionModel());
		_optionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayout(new BorderLayout());
		bindListSelectionToViewContainer();

		_split.setLeftComponent(new JScrollPane(_optionsList));
		_split.setRightComponent(_viewContainer);
		add(_split, BorderLayout.CENTER);

		addHierarchyListener(new HierarchyListener()
		{
			public void hierarchyChanged(HierarchyEvent e)
			{
				if (	(e.getID() == HierarchyEvent.HIERARCHY_CHANGED)
					&&	((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0)
					&&	AbstractPreferencesPanel.this.isShowing())
				{
					if (	_optionsList.getSelectedIndex() == -1
						&&	_optionsList.getModel().getSize() > 0)
					{
						// probably should have some
						// sort of persistent state
						// here to remember last
						// selected preference pane
						_optionsList.setSelectedIndex(0);
					}
				}
			}
		});
	}

	/**
	 * Registers a preferences component to be displayed in the preferences list. The name of
	 * the component will be used as the list display (probably want to change this?? To
	 * what??).
	 * 
	 * @param title the title for the preferences component
	 * @param component the preferences component to register
	 * @param applyAction the action that will be called when the OK button is clicked (the OK
	 * button enablement state is bound to the enablement state of all registered apply actions)
	 * @param revertAction the action that will be called when the Cancel button is clicked (the
	 * Cancel button is always enabled as it can be used to close the dialog)
	 */
	protected void register(String title, JComponent component, Action applyAction,
		Action revertAction)
	{
		ViewRegistration registration =
			new ViewRegistration(title, component, applyAction, revertAction, new ActionBinder(
				applyAction));
		_registrations.add(registration);
		_listModel.addElement(registration);
	}

	/**
	 * @see net.guts.gui.dialog.support.AbstractPanel#getAcceptAction()
	 */
	@Override protected GutsAction getAcceptAction()
	{
		return _accept;
	}

	private final GutsAction _accept = new GutsAction("ok")
	{
		@Override protected void perform()
		{
			for (ViewRegistration registration: _registrations)
			{
				Action action = registration._applyAction;
				if (action != null && action.isEnabled())
				{
					action.actionPerformed(event());
				}
			}
			unbindActions();
		}
	};

	private void unbindActions()
	{
		for (ViewRegistration registration: _registrations)
		{
			registration._actionBinder.unbind();
		}
	}

	@Override protected GutsAction getCancelAction()
	{
		return _revert;
	}

	private final GutsAction _revert = new GutsAction("cancel")
	{
		@Override protected void perform()
		{
			for (ViewRegistration registration: _registrations)
			{
				Action action = registration._revertAction;
				if (action != null && action.isEnabled())
				{
					action.actionPerformed(event());
				}
			}
			getParentDialog().close(true);
			unbindActions();
		}
	};

	/**
	 * Synchronizes global action enablement with the enablement state of actions from
	 * registered preferences panels If one or more registered apply actions are enabled, then
	 * the global apply action is enabled
	 */
	private void syncEnabled()
	{
		_accept.setEnabled(atLeastOneApplyActionsAreEnabled());
	}

	private boolean atLeastOneApplyActionsAreEnabled()
	{
		boolean allNull = true;

		for (ViewRegistration registration: _registrations)
		{
			Action action = registration._applyAction;
			if (action != null)
			{
				if (action.isEnabled())
				{
					return true;
				}
				allNull = false;
			}
		}
		return allNull;
	}

	/**
	 * @see net.guts.gui.dialog.support.AbstractMultiPanel#getSubComponents()
	 */
	@Override final protected Iterable<JComponent> getSubComponents()
	{
		List<JComponent> components = new ArrayList<JComponent>(_registrations.size());
		for (ViewRegistration registration: _registrations)
		{
			components.add(registration._view);
		}
		return components;
	}

	private void bindListSelectionToViewContainer()
	{
		_optionsList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				ViewRegistration selectedRegistration =
					(ViewRegistration) _optionsList.getSelectedValue();
				if (_viewContainer.getComponentCount() > 0)
				{
					_viewContainer.remove(0);
				}
				_resourceInjector.injectComponent(selectedRegistration._view);
				_viewContainer.add(selectedRegistration._view, 0);
				_viewContainer.validate();
				_viewContainer.repaint();
			}
		});
	}

	private void setCurrent(ViewRegistration registration)
	{
		_optionsList.setSelectedValue(registration, true);
	}

	/**
	 * Allows top-level action enablement to be adjusted when the actions of registered
	 * preferences panels are enabled/disabled.
	 */
	private class ActionBinder implements PropertyChangeListener
	{
		private final Action _source;

		public ActionBinder(Action source)
		{
			_source = source;
			sync();
			if (source != null)
			{
				source.addPropertyChangeListener(this);
			}
		}

		private void sync()
		{
			syncEnabled();
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
			if (_source != null)
			{
				_source.removePropertyChangeListener(this);
			}
		}
	}

	/**
	 * Represents a registered preferences view
	 */
	private static class ViewRegistration
	{
		private final String _name;
		private final JComponent _view;
		private final Action _applyAction;
		private final Action _revertAction;
		private final ActionBinder _actionBinder;

		public ViewRegistration(String name, JComponent view, Action applyAction,
			Action revertAction, ActionBinder actionBinder)
		{
			_name = name;
			_view = view;
			_applyAction = applyAction;
			_revertAction = revertAction;
			_actionBinder = actionBinder;
		}

		@Override public String toString()
		{
			return _name;
		}
	}

	/**
	 * A {@link ListSelectionModel} that forces the selection to always have one item selected
	 */
	// TODO probably should move this out to a utilities package
	private static class ExactlyOneListSelectionModel extends DefaultListSelectionModel
	{
		private boolean _clearing;

		@Override public void removeSelectionInterval(int index0, int index1)
		{
			if (_clearing)
			{
				super.removeSelectionInterval(index0, index1);
			}
			else
			{
				// do nothing -- we don't want the control deselect stuff
			}
		}

		@Override public void clearSelection()
		{
			_clearing = true;
			super.clearSelection();
			_clearing = false;
		}
	}
}
