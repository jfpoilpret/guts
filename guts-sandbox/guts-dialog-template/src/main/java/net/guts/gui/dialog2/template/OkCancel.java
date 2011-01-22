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

import java.awt.LayoutManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;

import net.guts.gui.action.ActionRegistrationManager;
import net.guts.gui.action.GutsAction;
import net.guts.gui.action.GutsActionDecorator;
import net.guts.gui.dialog2.CloseChecker;
import net.guts.gui.dialog2.TemplateDecorator;
import net.guts.gui.window.AbstractConfig;
import net.guts.gui.window.RootPaneConfig;

import com.google.inject.Inject;
import com.google.inject.Singleton;

//TODO???? define AbstractTemplateConfig<T> extends AbstractConfig<JDialog, T>
// TODO replace JDialog with RootPaneContainer
public final class OkCancel extends AbstractConfig<JDialog, OkCancel>
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
	
	public OkCancel withCloseChecker(CloseChecker closeChecker)
	{
		_config._closeChecker = closeChecker;
		return this;
	}

	public OkCancel withOK(Action apply)
	{
		_config._apply = apply;
		_config._hasOK = true;
		return this;
	}
	
	public OkCancel withOK()
	{
		return withOK(null);
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
		CloseChecker _closeChecker = null;
		Action _apply = null;
		Action _cancel = null;
		boolean _hasApply = false;
		boolean _hasCancel = false;
		boolean _hasOK = false;
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

	@Override public void decorate(
		JDialog container, JComponent view, RootPaneConfig<JDialog> configuration)
	{
		// Get config passed by the initial caller
		OkCancel.Config config = configuration.get(OkCancel.Config.class);

		// Handle listeners on container close
		setupCloseChecker(container, config);
		
		// Create necessary Actions, lay them out
		JComponent fullView = installActions(container, view, config);
		
		// Add view to container
		container.setContentPane(fullView);
	}
	
	private JComponent installActions(
		JDialog container, JComponent view, OkCancel.Config config)
	{
		// Create the right actions when needed
		GutsAction ok = setupAction(createOkAction(container, config));
		GutsAction apply = setupAction(createApplyAction(config));
		GutsAction cancel = setupAction(createCancelAction(container, config));

		// Create buttons for each action
		JButton okBtn = createButton(ok, view);
		JButton applyBtn = createButton(apply, view);
		JButton cancelBtn = createButton(cancel, view);

		if (okBtn != null || applyBtn != null || cancelBtn != null)
		{
			// Add them to the view with the right layout-optimized adder
			Class<? extends LayoutManager> layout = view.getLayout().getClass();
			OkCancelLayoutAdder adder = _layouts.get(layout);
			if (adder == null)
			{
				adder = _layouts.get(LayoutManager.class);
			}
			return adder.layout(view, okBtn, cancelBtn, applyBtn);
		}
		else
		{
			return view;
		}
	}

	static private GutsAction createOkAction(
		final JDialog container, final OkCancel.Config config)
	{
		if (config._hasOK)
		{
			return new GutsActionDecorator("ok", config._apply)
			{
				@Override protected void afterTargetPerform()
				{
					config._result = OkCancel.Result.OK;
					container.setVisible(false);
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
		if (config._hasApply)
		{
			return new GutsActionDecorator("apply", config._apply);
		}
		else
		{
			return null;
		}
	}
	
	static private GutsAction createCancelAction(
		final JDialog container, final OkCancel.Config config)
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
						container.setVisible(false);
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
						container.setVisible(false);
					}
				};
			}
		}
		return cancel;
	}
	
	private GutsAction setupAction(GutsAction action)
	{
		if (action != null)
		{
			_actionRegistry.registerAction(action);
		}
		return action;
	}
	
	static private JButton createButton(GutsAction action, JComponent view)
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
	
	private void setupCloseChecker(final JDialog container, final OkCancel.Config config)
	{
		if (config._closeChecker != null)
		{
			container.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			container.addWindowListener(new WindowAdapter()
			{
				@Override public void windowClosing(WindowEvent e)
				{
					if (config._closeChecker.canClose())
					{
						container.dispose();
					}
				}
			});
		}
		else
		{
			container.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		}
	}
	
	final private ActionRegistrationManager _actionRegistry;
	final private Map<Class<? extends LayoutManager>, OkCancelLayoutAdder> _layouts;
}
