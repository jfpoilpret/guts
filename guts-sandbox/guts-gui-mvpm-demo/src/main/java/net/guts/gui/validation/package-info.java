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
 * This package brings 
 * {@linkplain http://www.jgoodies.com/freeware/validationdemo/index.html JGoodies validation}
 * support to windows that are managed by {@link net.guts.gui.window.WindowController}.
 * <p/>
 * To get such support, just add {@link net.guts.gui.validation.ValidationModule} to the
 * list of {@link com.google.inject.Module}s used to create the Guice 
 * {@link com.google.inject.Injector} used in your application.
 * <p/>
 * Concretely, validation supports simply consists in wrapping the {@code view}, passed
 * to {@link net.guts.gui.window.WindowController#show} (or more precisely, the
 * {@code contentPane} of the passed {@link javax.swing.RootPaneContainer}), with an
 * {@link net.guts.gui.validation.IconFeedbackPanel} that shows icons and tooltips on
 * all components having validation problems.
 * <p/>
 * Basic usage of Validation feature is as follows:
 * <pre>
 * MyModel model = new MyModel(...);
 * MyView view = new MyView(model);
 * 
 * OkCancel decorationConfig = OkCancel.create()
 *     .withCancel().withOK(model.getApplyAction());
 * 
 * Validation validationConfig = Validation.create()
 *     .withModel(model.getValidationModel()):
 * 
 * RootPaneConfig&lt;JDialog&gt; config = JDialogConfig.create()
 *     .merge(decorationConfig)
 *     .merge(validationConfig)
 *     .config();
 * 
 * _dialogFactory.showDialog(view, config);
 * </pre>
 * <p/>
 * This package also includes a few useful classes when using JGoodies validation:
 * <ul>
 * <li>{@link net.guts.gui.validation.ValidationMessageFactory}: a factory that creates
 * {@link com.jgoodies.validation.ValidationMessage} which messages are internationalized.</li>
 * <li>{@link net.guts.gui.validation.ValidationHelper}: a few utility methods that can help
 * writing validation code.</li>
 * </ul>
 * <p/>
 * <b>Important!</b> Your project must declare JGoodies Validation library as a dependency,
 * otherwise, this package won't do anything.
 * 
 * @see net.guts.gui.validation.Validation
 */
package net.guts.gui.validation;
