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
 * This package contains various useful classes related to layout management in
 * dialogs created by {@link net.guts.gui.dialog.DefaultDialogFactory}.
 * <p/>
 * The core of the package is the interface 
 * {@link net.guts.gui.dialog.layout.ButtonsPanelAdder} which is used
 * by {@link net.guts.gui.dialog.support.AbstractPanel}.
 * <p/>
 * When you use {@link net.guts.gui.dialog.support.AbstractPanel}, "OK" and 
 * "Cancel" buttons are automatically added thanks to a 
 * {@link net.guts.gui.dialog.layout.ButtonsPanelAdder} implementation 
 * that depends on the {@link java.awt.LayoutManager} used in the panel.
 * <p/>
 * You may need to implement your own 
 * {@link net.guts.gui.dialog.layout.ButtonsPanelAdder} to fit the 
 * {@link java.awt.LayoutManager} of your choice. In this case, you would need 
 * to register your own implementation with 
 * {@link net.guts.gui.dialog.layout.ButtonsPanelAdderFactory}. You may 
 * take a look, in this package, at various concrete implementations handling a 
 * few common {@code LayoutManager}s.
 */
package net.guts.gui.dialog.layout;
