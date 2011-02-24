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

/**
 * This package contains the "Wizard" 
 * {@link net.guts.gui.template.TemplateDecorator template decorator} (and its 
 * extension API) that automatically adds "Previous", "Next", "Finish" and "Cancel"
 * buttons to your views inside {@link javax.swing.RootPaneContainer} when open by 
 * {@link net.guts.gui.window.WindowController#show}.
 * <p/>
 * Basic usage of the "Wizard" decorator is as follows:
 * <pre>
 * Wizard decorationConfig = Wizard.create()
 *     .mapNextStep(Step1View.class)
 *     .mapNextStep(Step2View.class)
 *     .mapNextStep(Step3View.class)
 *     .withCancel()
 *     .withFinish(finishAction);
 * 
 * RootPaneConfig&lt;JDialog&gt; config = JDialogConfig.create()
 *     .bounds(BoundsPolicy.PACK_AND_CENTER)
 *     .state(StatePolicy.RESTORE_IF_EXISTS)
 *     .merge(decorationConfig)
 *     .config();
 * 
 * JComponent mainWizardView = decorationConfig.mainView();
 * mainWizardView.setName("MyWizard");
 * _dialogFactory.showDialog(mainWizardView, config);
 * </pre>
 * In the snippet above, we first create {@code decorationConfig} by using
 * {@link net.guts.gui.template.wizard.Wizard} configuration builder's
 * fluent API; in there we indicate all views ({@link javax.swing.JComponent}s)
 * that will be steps in the wizard dialog: we also require a "Cancel" button
 * that will close the wizard without doing anything, and the "Finish" button
 * that will call {@code finishAction} and then will close the wizard dialog.
 * <p/>
 * Note that, in this simple snippet, 3 registered steps will be "played" in the
 * same order as calls to {@link net.guts.gui.template.wizard.Wizard#mapNextStep(Class)}.
 * There are ways to dynamically manage how steps follow each other in the wizard
 * dialog but they are not demonstrated here. Please see 
 * {@link net.guts.gui.template.wizard.WizardController} for more information.
 * <p/>
 * Then we create {@code config} that is needed to display a {@link javax.swing.JDialog}
 * and we merge {@code decorationConfig} into it; finally we can open a dialog with
 * our wizard thanks to {@link net.guts.gui.dialog.DialogFactory#showDialog}.
 * <p/>
 * Once the container of our view has been closed, it is possible, if needed, to find 
 * out which button has closed it ("Finish" or "Cancel") by calling 
 * {@link net.guts.gui.template.wizard.Wizard#result() decorationConfig.result()}.
 * <p/>
 * In the last part of the above snippet, the main wizard view is created by the
 * {@link net.guts.gui.template.wizard.Wizard decorationConfig} object itself.
 * We set its name so that resource injection can work throughout the wizard dialog
 * before showing it with {@link net.guts.gui.dialog.DialogFactory}.
 * <p/>
 * If {@link net.guts.gui.resource.ResourceModule} is used in the application, then
 * wizards created by {@link net.guts.gui.template.wizard.Wizard} configuration
 * object will have some specific resources injected:
 * <ul>
 * <li>Each step view will have its own resources injected based on its 
 * {@link javax.swing.JComponent#getName() name}, as usual</li>
 * <li>In the main view, there is a {@link javax.swing.JLabel} to display a 
 * description for each step, its content must be injected as a {@code Map}
 * where keys are names of each step view, and values are the description text
 * for the mapped step view:
 * <pre>
 * DemoWizard-wizard._stepDescriptions=[DemoView1:Step #1] [DemoView2:Second step]
 * </pre>
 * where {@code DemoWizard} is the name given to the main wizard view, {@code DemoView1}
 * the name of the first step view...
 * </li>
 * <li>Wizard buttons resources are already defined by Guts-GUI but can be overridden:
 * <pre>
 * cancel.text=Ca&ncel
 * finish.text=&Finish
 * previous.text=< &Back
 * next.text=&Next >
 * </pre>
 * </li>
 * </ul>
 * <p/>
 * Note that you must have added {@link net.guts.gui.template.wizard.WizardModule}
 * to the list of {@link com.google.inject.Module}s used when creating Guice
 * {@link com.google.inject.Injector}.
 */
package net.guts.gui.template.wizard;
