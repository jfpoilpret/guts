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

package net.guts.gui.template.wizard;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;

import net.guts.gui.template.TemplateDecorator;
import net.guts.gui.window.AbstractConfig;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Configuration object to create a wizard view ({@link javax.swing.JComponent}), 
 * made of several steps views, and decorated with "Previous", "Next", "Finish"
 * and "Cancel" buttons. This object must then be 
 * {@link AbstractConfig#merge(AbstractConfig) merged} to one of
 * {@link net.guts.gui.window.JDialogConfig}, {@link net.guts.gui.window.JFrameConfig},
 * {@link net.guts.gui.window.JInternalFrameConfig} or 
 * {@link net.guts.gui.window.JAppletConfig} before passing the latter to
 * {@link net.guts.gui.window.WindowController#show}.
 * <p/>
 * {@code Wizard} uses a fluent API to set configuration on how exactly to decorate
 * the wizard.
 * <p/>
 * Check <a href="net/guts/gui/template/wizard/package-summary.html">package
 * documentation</a> for a code example.
 * 
 * @author jfpoilpret
 */
public final class Wizard extends AbstractConfig<RootPaneContainer, Wizard>
{
	private Wizard()
	{
		set(TemplateDecorator.TEMPLATE_TYPE_KEY, WizardDecorator.class);
		set(WizardConfig.class, _config);
		_config._controller = _controller;
	}
	
	/**
	 * Create a new {@code OkCancel} default configuration object.
	 * 
	 * @return a new {@code OkCancel} configuration object
	 */
	static public Wizard create()
	{
		return new Wizard();
	}

	/**
	 * Add {@code view} to the sequence of steps for the wizard dialog that will be
	 * created by this {@code Wizard} configuration object.
	 * <p/>
	 * {@code view} must have a {@link JComponent#setName name} in order to inject
	 * resources for each step.
	 * <p/>
	 * Note: {@code view won't have any dependencies injected by Guice}. If you need
	 * Guice injection, then use {@link #mapNextStep(Class)}.
	 * 
	 * @param view the view to add as the next step in the wizard
	 * @return {@code this} builder so that calls can be chained until {@link #config()}
	 */
	public Wizard mapNextStep(JComponent view)
	{
		_controller.addStep(view.getName(), view, true);
		return this;
	}
	
	/**
	 * Add {@code view} to the list of steps to be used in the wizard dialog that 
	 * will be created by this {@code Wizard} configuration object.
	 * <p/>
	 * {@code view} must have a {@link JComponent#setName name} in order to inject
	 * resources for each step.
	 * <p/>
	 * {@code view} won't be automatically added to the steps sequence used in the 
	 * wizard dialog. Effectively adding {@code view} to the list of steps must be
	 * performed by {@link WizardController#setNextStepsSequence(String...)}.
	 * <p/>
	 * Note: {@code view won't have any dependencies injected by Guice}. If you need
	 * Guice injection, then use {@link #mapOneStep(Class)}.
	 * 
	 * @param view the view to add to the list of steps in the wizard
	 * @return {@code this} builder so that calls can be chained until {@link #config()}
	 */
	public Wizard mapOneStep(JComponent view)
	{
		_controller.addStep(view.getName(), view, false);
		return this;
	}
	
	/**
	 * Add a new view, of type {@code view} and created by Guice, to the sequence 
	 * of steps for the wizard dialog that will be created by this {@code Wizard} 
	 * configuration object.
	 * <p/>
	 * {@code view} must have a {@link JComponent#setName name} in order to inject
	 * resources for each step.
	 * 
	 * @param view the type of view to create and add as the next step in the wizard
	 * @return {@code this} builder so that calls can be chained until {@link #config()}
	 */
	public Wizard mapNextStep(Class<? extends JComponent> view)
	{
		return mapNextStep(_injector.getInstance(view));
	}
	
	/**
	 * Add a new view, of type {@code view} and created by Guice, to the list of 
	 * steps to be used in the wizard dialog that will be created by this 
	 * {@code Wizard} configuration object.
	 * <p/>
	 * {@code view} must have a {@link JComponent#setName name} in order to inject
	 * resources for each step.
	 * <p/>
	 * {@code view} won't be automatically added to the steps sequence used in the 
	 * wizard dialog. Effectively adding {@code view} to the list of steps must be
	 * performed by {@link WizardController#setNextStepsSequence(String...)}.
	 * 
	 * @param view the type of view to create and add to the list of steps in the wizard
	 * @return {@code this} builder so that calls can be chained until {@link #config()}
	 */
	public Wizard mapOneStep(Class<? extends JComponent> view)
	{
		return mapOneStep(_injector.getInstance(view));
	}

	/**
	 * Sets the view decorated through {@code this} configuration object to be added
	 * a "Finish" button that will:
	 * <ul>
	 * <li>call the {@code apply} action</li>
	 * <li>close the {@link RootPaneContainer} containing the view</li>
	 * <li>set {@link #result()} to {@link Result#FINISH}</li>
	 * </ul>
	 * <p/>
	 * Note that, even if you don't call this method, the created wizard dialog
	 * will still have a "Finish" button that will close the dialog, but no other
	 * {@code Action} associated to it.
	 * 
	 * @param apply the {@link Action} to be called by the "Finish" button
	 * @return {@code this} builder so that calls can be chained until {@link #config()}
	 */
	public Wizard withFinish(Action apply)
	{
		_config._apply = apply;
		return this;
	}

	/**
	 * Sets the view decorated through {@code this} configuration object to be added
	 * a "Cancel" button that will:
	 * <ul>
	 * <li>call the {@code cancel} action</li>
	 * <li>close the {@link RootPaneContainer} containing the view</li>
	 * <li>set {@link #result()} to {@link Result#CANCEL}</li>
	 * </ul>
	 * <p/>
	 * Note that, even if you don't call this method, the created wizard dialog
	 * will still have a "Cancel" button that will close the dialog, but no other
	 * {@code Action} associated to it.
	 * 
	 * @param cancel the {@link Action} to be called by the "Cancel" button
	 * @return {@code this} builder so that calls can be chained until {@link #config()}
	 */
	public Wizard withCancel(Action cancel)
	{
		_config._cancel =  cancel;
		return this;
	}

	/**
	 * Get the main wizard view (embedding all steps and all wizard buttons) to be
	 * {@link RootPaneContainer#setContentPane set as contentPane} to a 
	 * {@code RootPaneContainer}. You must {@link JComponent#setName assign a name} 
	 * to it for resource injection to work.
	 * 
	 * @return the main wizard view
	 */
	public JComponent mainView()
	{
		return _mainView;
	}
	
	/**
	 * Get the controller for the wizard created by this wizard configuration object.
	 * This controller gives some finer control over the order of wizard steps during
	 * user interaction.
	 * 
	 * @return the controller to have better -dynamic- control over the wizard 
	 * steps sequence.
	 */
	public WizardController controller()
	{
		return _controller;
	}
	
	/**
	 * The kind of button that was used to close a view decorated by {@code this}.
	 * This is returned by {@link Wizard#result()} after the view has been displayed
	 * then closed.
	 */
	static public enum Result
	{
		/**
		 * The view was closed by clicking the "Finish" button.
		 */
		FINISH,
		
		/**
		 * The view was closed by clicking the "Cancel" button.
		 */
		CANCEL
	}

	/**
	 * Tells the button by which the view, decorated by {@code this}, was closed.
	 * 
	 * @return {@link Result#FINISH} if the view was closed by the "OK" button, 
	 * {@link Result#CANCEL} if the view was closed by the "Cancel" button, or
	 * {@code null} if the view is not closed yet or if it was closed directly by
	 * its close box.
	 */
	public Result result()
	{
		return _config._result;
	}

	@Inject static void setInjector(Injector injector)
	{
		_injector = injector;
	}
	
	static private Injector _injector;
	
	final private WizardConfig _config = new WizardConfig();
	final private JComponent _mainView = new JPanel();
	final private WizardController _controller = new WizardController(_mainView);
}
