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
 * This package contains the API and implementation of Guts-GUI "templating" system
 * for views inside {@link javax.swing.RootPaneContainer} when open by 
 * {@link net.guts.gui.window.WindowController#show}.
 * <p/>
 * Concrete templates are defined in their own packages:
 * <ul>
 * <li>{@link net.guts.gui.dialog2.template.okcancel.OkCancel}</li>
 * <li>{@link net.guts.gui.dialog2.template.wizard.Wizard}</li>
 * </ul>
 * But you can easily define your own templates by implementing the
 * {@link net.guts.gui.dialog2.template.TemplateDecorator} interface, and defining
 * a specific {@link net.guts.gui.window.AbstractConfig} for it.
 */
package net.guts.gui.dialog2.template;
