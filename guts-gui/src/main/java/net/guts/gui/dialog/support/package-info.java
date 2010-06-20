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
 * This package contains various useful classes, some can be used as templates 
 * for the main panels of all your dialogs.
 * <p/>
 * At the package core is the 
 * {@link net.guts.gui.dialog.support.AbstractPanel} abstract base class 
 * that you can use to build the main panels of all your general dialogs. This 
 * class takes care of adding the usual "OK" and "Cancel" buttons.
 * <p/>
 * When you use {@link net.guts.gui.dialog.support.AbstractPanel}, "OK" and 
 * "Cancel" buttons are automatically added thanks to a 
 * {@link net.guts.gui.dialog.layout.ButtonsPanelAdder} implementation 
 * that depends on the {@link java.awt.LayoutManager} used in the panel.
 * <p/>
 * This package also contains a few other abstract base classes for more specific 
 * dialogs such as wizards dialogs (see 
 * {@link net.guts.gui.dialog.support.AbstractWizardPanel}) or dialogs based 
 * on tabbed panels (see {@link net.guts.gui.dialog.support.AbstractTabbedPanel}).
 * 
 * @see net.guts.gui.dialog.layout
 */
package net.guts.gui.dialog.support;
