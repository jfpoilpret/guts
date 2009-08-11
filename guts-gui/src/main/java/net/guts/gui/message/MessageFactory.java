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

package net.guts.gui.message;

import net.guts.gui.message.impl.DefaultMessageFactory;

import com.google.inject.ImplementedBy;

/**
 * Interface defining the factory to create and show message boxes in the
 * application.
 * <p/>
 * Each message box must be uniquely identified by an {@code id} and its 
 * properties defined in the main application properties file. E.g. a message
 * box, named "{@code confirm-delete}", would be defined as follows:
 * <pre>
 * confirm-delete.messageType=Question
 * confirm-delete.optionType=OkCancel
 * confirm-delete.title=Confirm delete
 * confirm-delete.message=Are you sure you want to delete contact "%1$s %2$s"?
 * </pre>
 * where:
 * <ul>
 * <li>{@code messageType} must be one of the enum values of {@link MessageType}</li>
 * <li>{@code optionType} must be one of the enum values of {@link OptionType}</li>
 * <li>{@code title} is a text that will be formatted by {@link java.util.Formatter}
 * to set the message box title; it may accept arguments that must be passed to
 * {@link #showMessage(String, Object[]) showMessage}</li>
 * <li>{@code message} is a text that will be formatted by {@link java.util.Formatter}
 * to set the message box content; it may accept arguments that must be passed to
 * {@link #showMessage(String, Object[]) showMessage}</li>
 * </ul>
 * Displaying such a message box would be done this way:
 * <pre>
 * UserChoice choice = _messageFactory.showMessage(
 *     "confirm-delete", firstName, lastName);
 * </pre>
 * <p/>
 * This service is directly injectable through Guice facilities.
 * 
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(DefaultMessageFactory.class)
public interface MessageFactory
{
	/**
	 * Displays the message box uniquely identified by {@code id} and defined
	 * in the resources properties file used by the application.
	 * @param id message box unique identifier
	 * @param args optional arguments used to format the message box title and
	 * content
	 * @return the user's choice (according to the clicked button)
	 */
	public UserChoice showMessage(String id, Object... args);
}
