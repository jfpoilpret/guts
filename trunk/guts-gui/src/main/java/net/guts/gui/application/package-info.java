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
 * This package contains the main classes to be used to start a GUI application.
 * <p/>
 * It defines two abstract classes one of which must be derived by your main 
 * application class, and a few general GUI services interfaces for exception 
 * handling.
 * <p/>
 * If you need to write an application with a main frame but without any docking 
 * capability, then your application class should derive from 
 * {@link net.guts.gui.application.AbstractGuiceApplication}. If you want 
 * docking in your application, then your main class should derive from 
 * {@link net.sf.guice.gui.application.AbstractDockingApplication}. Refer to 
 * these classes to find out how to setup your own Guice 
 * {@link com.google.inject.Module}s.
 * <p/>
 * Exception handling is centralized by the 
 * {@link net.guts.gui.application.ExceptionHandlerManager} service which is 
 * injectable to any instance created by Guice. You can implement your own 
 * {@link net.guts.gui.application.ExceptionHandler}(s) and register it 
 * (them) with {@link net.guts.gui.application.ExceptionHandlerManager}.
 */
package net.guts.gui.application;
