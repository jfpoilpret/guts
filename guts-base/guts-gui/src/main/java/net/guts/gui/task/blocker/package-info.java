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
 * This package contains the {@link net.guts.gui.task.blocker.InputBlocker} API, 
 * used by {@link net.guts.gui.task.TasksGroup} to allow blocking user input
 * during execution of long tasks in background.
 * <p/>
 * In addition, the package contains several {@link net.guts.gui.task.blocker.InputBlocker}
 * implementation classes:
 * <ul>
 * <li>{@link net.guts.gui.task.blocker.ComponentInputBlocker}: blocks one or more 
 * Swing components</li>
 * <li>{@link net.guts.gui.task.blocker.ActionInputBlocker}: blocks a 
 * {@link javax.swing.Action}</li>
 * <li>{@link net.guts.gui.task.blocker.GlassPaneInputBlocker}: blocks a 
 * {@link javax.swing.JRootPane} ({@code JFrame}, {@code JDialog} or {@code JInternalFrame})
 * with a semi-transparent glass pane showing a spinning animation</li>
 * <li>{@link net.guts.gui.task.blocker.ModalDialogInputBlocker}: blocks the current
 * frontmost window with a modal {@code JDialog} showing tasks progress and optionally
 * allowing cancellation of current {@link net.guts.gui.task.TasksGroup}.</li>
 * </ul>
 * Since it is not always straightforward to instantiate these classes, this package
 * also includes the {@link net.guts.gui.task.blocker.InputBlockers} static utility
 * class to simplify their creation.
 */
package net.guts.gui.task.blocker;
