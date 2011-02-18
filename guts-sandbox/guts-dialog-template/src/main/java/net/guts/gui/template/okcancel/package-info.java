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
 * This package contains the "OkCancel" 
 * {@link net.guts.gui.template.TemplateDecorator template decorator} (and its 
 * extension API) that automatically adds "OK", "Cancel" and "Apply" buttons to 
 * your views inside {@link javax.swing.RootPaneContainer} when open by 
 * {@link net.guts.gui.window.WindowController#show}.
 * <p/>
 * Basic usage of the "OkCancel" decorator is as follows:
 * <pre>
 * OkCancel decorationConfig = OkCancel.create()
 *     .withCancel()
 *     .withOK(applyAction)
 *     .withApply();
 * 
 * RootPaneConfig&lt;JDialog&gt; config = JDialogConfig.create()
 *     .bounds(BoundsPolicy.PACK_AND_CENTER)
 *     .state(StatePolicy.RESTORE_IF_EXISTS)
 *     .merge(decorationConfig)
 *     .config();
 * 
 * _dialogFactory.showDialog(MyView.class, config);
 * </pre>
 * In the snippet above, we first create {@code decorationConfig} by using
 * {@link net.guts.gui.template.okcancel.OkCancel} configuration builder's
 * fluent API; in there we indicate we want all 3 buttons ("OK", "Cancel" and
 * "Apply") added to the view. "Cancel" will simply close the container of our
 * view; both "OK" and "Apply" will call {@code applyAction}, but "OK" will also
 * close the container of our view once {@code applyAction} is performed.
 * <p/>
 * Then we create {@code config} that is needed to display a {@link javax.swing.JDialog}
 * and we merge {@code decorationConfig} into it; finally we can open a dialog with
 * our view (a {@code MyView} instance) thanks to 
 * {@link net.guts.gui.dialog2.DialogFactory#showDialog}.
 * <p/>
 * Once the container of our view has been closed, it is possible, if needed, to find 
 * out which button has closed it ("OK" or "Cancel") by calling 
 * {@link net.guts.gui.template.okcancel.OkCancel#result() decorationConfig.result()}.
 * <p/>
 * Note that you must have added {@link net.guts.gui.template.okcancel.OkCancelModule}
 * to the list of {@link com.google.inject.Module}s used when creating Guice
 * {@link com.google.inject.Injector}.
 * <p/>
 * In order to "add" buttons to a view, the "OkCancel" decorator either:
 * <ul>
 * <li>embeds the given view into another view that contains the new buttons</li>
 * <li>or tries to add buttons directly to the given view (based on its current 
 * {@link java.awt.LayoutManager})</li>
 * </ul>
 * In the second case, only {@code DesignGridLayout} is supported out of the box,
 * but it is pretty easy to add support for your preferred {@code LayoutManager},
 * by implementing {@link net.guts.gui.template.okcancel.OkCancelLayoutAdder}
 * interface and bind your implementation in one of your {@link com.google.inject.Module}s,
 * by using {@link net.guts.gui.template.okcancel.OkCancelLayouts#bindLayout}.
 * <p/>
 * TODO explain why it is important to add buttons directly to the view
 */
package net.guts.gui.template.okcancel;
