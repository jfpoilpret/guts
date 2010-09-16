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
 * This package contains classes to create and show message boxes (ie 
 * {@link javax.swing.JOptionPane}s) to the user.
 * <p/>
 * The package just defines one single 
 * {@link net.guts.gui.message.MessageFactory} service and a few enum types 
 * used by this service. You can inject this service through Guice wherever you 
 * need by simply using Guice {@link com.google.inject.Inject} annotation:
 * <pre>
 * &#64;Inject private MessageFactory _messageFactory;
 * </pre>
 */
package net.guts.gui.message;
