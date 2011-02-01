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

package net.guts.gui.dialog2.template;

import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.Window;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.RootPaneContainer;

import net.guts.gui.action.ActionRegistrationManager;
import net.guts.gui.action.GutsAction;
import net.guts.gui.action.GutsActionDecorator;
import net.guts.gui.window.AbstractConfig;
import net.guts.gui.window.RootPaneConfig;

import com.google.inject.Inject;
import com.google.inject.Singleton;

//TODO?
// define AbstractTemplateConfig<T, V> extends AbstractConfig<V extends RootPaneContainer, T>
public final class OkCancel extends AbstractConfig<RootPaneContainer, OkCancel>
{
	private OkCancel()
	{
		set(TemplateDecorator.TEMPLATE_TYPE_KEY, OkCancelDecorator.class);
		set(Config.class, _config);
	}
	
	static public OkCancel create()
	{
		return new OkCancel();
	}
	
	public OkCancel withOK(Action apply)
	{
		_config._apply = apply;
		_config._hasOK = true;
		return this;
	}
	
	public OkCancel withCancel(Action cancel)
	{
		_config._cancel =  cancel;
		_config._hasCancel = true;
		return this;
	}

	public OkCancel withCancel()
	{
		return withCancel(null);
	}

	public OkCancel withApply()
	{
		_config._hasApply = true;
		return this;
	}
	
	public OkCancel dontChangeView()
	{
		_config._dontChangeView = true;
		return this;
	}

	static public enum Result
	{
		OK,
		CANCEL
	}

	public Result result()
	{
		return _config._result;
	}
	
	//CSOFF: VisibilityModifier
	static class Config
	{
		Action _apply = null;
		Action _cancel = null;
		boolean _hasApply = false;
		boolean _hasCancel = false;
		boolean _hasOK = false;
		boolean _dontChangeView = false;
		Result _result = null;
	}
	//CSON: VisibilityModifier

	final private Config _config = new Config();
}

@Singleton
class OkCancelDecorator implements TemplateDecorator
{
	@Inject OkCancelDecorator(ActionRegistrationManager actionRegistry,
		Map<Class<? extends LayoutManager>, OkCancelLayoutAdder> layouts)
	{
		_actionRegistry = actionRegistry;
		_layouts = layouts;
	}

	@Override public <T extends RootPaneContainer> void decorate(
		T container, Container view, RootPaneConfig<T> configuration)
	{
		// Get config passed by the initial caller
		OkCancel.Config config = configuration.get(OkCancel.Config.class);

		// Create necessary Actions, lay them out
		Container fullView = installActions(container, view, config);
		
		// Add view to container
		container.setContentPane(fullView);
	}
	
	private Container installActions(
		RootPaneContainer container, Container view, OkCancel.Config config)
	{
		// Create the right actions when needed
		GutsAction ok = setupAction(createOkAction(container, config));
		GutsAction apply = setupAction(createApplyAction(config));
		GutsAction cancel = setupAction(createCancelAction(container, config));

		// Check that if container was already injected, it has the exact same buttons
		// otherwise throw an exception immediately
		if (checkCompatibleView(view, ok, apply, cancel))
		{
			return view;
		}

		// We need to modify the view and add the right buttons to it
		JButton okBtn = createButton(ok, view);
		JButton applyBtn = createButton(apply, view);
		JButton cancelBtn = createButton(cancel, view);

		if (okBtn != null || applyBtn != null || cancelBtn != null)
		{
			// Add them to the view with the right layout-optimized adder
			Class<? extends LayoutManager> layout = view.getLayout().getClass();
			OkCancelLayoutAdder adder = _layouts.get(layout);
			if (config._dontChangeView || adder == null)
			{
				adder = _layouts.get(LayoutManager.class);
			}
			Container fullView = adder.layout(view, okBtn, cancelBtn, applyBtn);
			if (fullView == view)
			{
				setViewModified(fullView);
			}
			return fullView;
		}
		else
		{
			return view;
		}
	}

	// returns true if view was compatible and has been directly modified
	// returns false if view was compatible and needs to be modified from scratch
	// throws exception is view was incompatible
	private boolean checkCompatibleView(
		Container view, GutsAction ok, GutsAction apply, GutsAction cancel)
	{
		if (isViewModified(view))
		{
			JButton okButton = null;
			JButton applyButton = null;
			JButton cancelButton = null;
			// Search for buttons
			for (Component child: view.getComponents())
			{
				if (child instanceof JButton)
				{
					JButton button = (JButton) child;
					if (isActionButton(button, "ok"))
					{
						okButton = button;
					}
					else if (isActionButton(button, "apply"))
					{
						applyButton = button;
					}
					else if (isActionButton(button, "cancel"))
					{
						cancelButton = button;
					}
				}
			}
			checkCompatibleActionButton(view, okButton, ok);
			checkCompatibleActionButton(view, applyButton, apply);
			checkCompatibleActionButton(view, cancelButton, cancel);
			
			// OK, all buttons are compatible, now replace actions where needed
			replaceAction(okButton, ok);
			replaceAction(applyButton, apply);
			replaceAction(cancelButton, cancel);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	static private void replaceAction(JButton button, GutsAction action)
	{
		if (button != null)
		{
			button.setAction(action);
		}
	}
	
	static private void checkCompatibleActionButton(
		Container view, JButton button, GutsAction action)
	{
		if ((button == null) != (action == null))
		{
			String msg = String.format("Button %s isn't compatible with action %s in view %s",
				button, action, view);
			throw new IllegalArgumentException(msg);
		}
	}
	
	static private boolean isActionButton(JButton button, String name)
	{
		Action action = button.getAction();
		if (action instanceof GutsAction)
		{
			return name.equals(((GutsAction) action).name());
		}
		else
		{
			return false;
		}
	}
	
	static private boolean isViewModified(Container view)
	{
		if (view instanceof JComponent)
		{
			JComponent jview = (JComponent) view;
			return jview.getClientProperty(MODIFIED_VIEW_MARKER) == Boolean.TRUE;
		}
		else
		{
			return false;
		}
	}
	
	static private void setViewModified(Container view)
	{
		if (view instanceof JComponent)
		{
			// Mark the view as already modified
			((JComponent) view).putClientProperty(MODIFIED_VIEW_MARKER, Boolean.TRUE);
		}
	}
	
	static private GutsAction createOkAction(
		final RootPaneContainer container, final OkCancel.Config config)
	{
		if (config._hasOK && config._apply != null)
		{
			return new GutsActionDecorator("ok", config._apply)
			{
				@Override protected void afterTargetPerform()
				{
					config._result = OkCancel.Result.OK;
					close(container);
				}
			};
		}
		else
		{
			return null;
		}
	}
	
	static private GutsAction createApplyAction(OkCancel.Config config)
	{
		if (config._hasApply && config._apply != null)
		{
			return new GutsActionDecorator("apply", config._apply);
		}
		else
		{
			return null;
		}
	}
	
	static private GutsAction createCancelAction(
		final RootPaneContainer container, final OkCancel.Config config)
	{
		GutsAction cancel = null;
		if (config._hasCancel)
		{
			if (config._cancel != null)
			{
				cancel = new GutsActionDecorator("cancel", config._cancel)
				{
					@Override protected void afterTargetPerform()
					{
						config._result = OkCancel.Result.CANCEL;
						close(container);
					}
				};
			}
			else
			{
				cancel = new GutsAction("cancel")
				{
					@Override protected void perform()
					{
						config._result = OkCancel.Result.CANCEL;
						close(container);
					}
				};
			}
		}
		return cancel;
	}
	
	static private void close(RootPaneContainer container)
	{
		if (container instanceof Window)
		{
			((Window) container).dispose();
		}
		else if (container instanceof JInternalFrame)
		{
			((JInternalFrame) container).dispose();
		}
		else
		{
			// For applets, what can we possibly do, besides hide it?
			container.getRootPane().getParent().setVisible(false);
		}
	}
	
	private GutsAction setupAction(GutsAction action)
	{
		if (action != null)
		{
			_actionRegistry.registerAction(action);
		}
		return action;
	}
	
	static private JButton createButton(GutsAction action, Container view)
	{
		if (action != null)
		{
			JButton button = new JButton(action);
			button.setName(view.getName() + "-" + action.name());
			return button;
		}
		else
		{
			return null;
		}
	}
	
	static final private String MODIFIED_VIEW_MARKER = OkCancel.class.getName();
	
	final private ActionRegistrationManager _actionRegistry;
	final private Map<Class<? extends LayoutManager>, OkCancelLayoutAdder> _layouts;
}
