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
 * This package contains classes to manage Dialogs used in GUI application based 
 * on the framework.
 * <p/>
 * The core of this package is the {@link net.guts.gui.dialog.DialogFactory} 
 * service which is in charge of constructing and displaying modal dialogs.
 * <p/>
 * Each dialog is only defined by its embedded panel, any 
 * {@link javax.swing.JComponent} is suitable, but deriving your dialog panels 
 * from {@link net.guts.gui.dialog.support.AbstractPanel} is the preferred 
 * -and easiest- way to take advantage of all 
 * {@link net.guts.gui.dialog.DialogFactory} features.
 * <p/>
 * Package {@link net.guts.gui.dialog.support} defines several classes and 
 * interfaces to ease actual dialog creation.
 */
package net.guts.gui.dialog;
