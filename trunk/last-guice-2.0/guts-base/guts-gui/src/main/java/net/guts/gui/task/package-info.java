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
 * This package contains the API (and implementation) of Guts-GUI tasks management.
 * In Guts-GUI, tasks are executed from background threads so that GUI actions may
 * not block the EDT if they take long to execute.
 * <p/>
 * This package defines {@link net.guts.gui.task.Task} interface which your actual 
 * tasks must implement. In Guts-GUI, several {@code Task}s can be grouped together 
 * in a {@link net.guts.gui.task.TasksGroup} and executed as one global task; 
 * {@code TasksGroup}s are created by a {@link net.guts.gui.task.TasksGroupFactory}, 
 * which is bound in Guice and is thus injectable anywhere in your classes.
 * <p/>
 * A {@code TasksGroup} uses a unique {@link java.util.concurrent.ExecutorService} to
 * execute all its tasks; A {@code TasksGroup} calls an 
 * {@link net.guts.gui.task.blocker.InputBlocker} to block the UI before starting its
 * {@code Task}s execution, and unblock the UI immediately after all its {@code Task}s
 * have finished their execution.
 * <p/>
 * Several {@link net.guts.gui.task.blocker.InputBlocker}s implementations are provided
 * by Guts-GUI in the {@link net.guts.gui.task.blocker} package.
 * <p/>
 * In a {@code TasksGroup}, you can add {@code Task}s and also 
 * {@link net.guts.gui.task.TaskListener}s that get notified of the progress of a 
 * {@code Task}.
 * <p/>
 * Each {@code Task}, in its {@link net.guts.gui.task.Task#execute(FeedbackController)}, 
 * can give feedback about its progress through the 
 * {@link net.guts.gui.task.FeedbackController} passed as an argument.
 * Feedback is notified to registered {@code TaskListener}s.
 * <p/>
 * For Guts-GUI task management system to be operational, 
 * {@link net.guts.gui.task.TasksModule} must be part of the list of 
 * {@link com.google.inject.Module}s used to create the Guice
 * {@link com.google.inject.Injector}. If you use Guts-GUI as a whole, i.e. you use
 * {@link net.guts.gui.application.AbstractApplication} to launch your application,
 * then {@code TasksModule} is automatically added to the {@code Module}s list.
 * <p/>
 * You can also listen to all notifications for any tasks inside a {@code TaskGroup}
 * with {@link net.guts.gui.task.TasksGroupListener}; you also get notified whenever
 * a new {@code Task} has been dynamically added to the {@code TasksGroup}, or when
 * all {@code Task}s have finished execution.
 */
package net.guts.gui.task;
