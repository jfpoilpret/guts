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

import javax.swing.JOptionPane;

/**
 * A type of message box. This typically determines the type of icon that will
 * be displayed in the message box.
 * <p/>
 * Message boxes are of one of the following types:
 * <ul>
 * <li>{@link #Info}</li>
 * <li>{@link #Warning}</li>
 * <li>{@link #Question}</li>
 * <li>{@link #Error}</li>
 * </ul>
 * <p/>
 * Although not directly used in {@link MessageFactory} API, this enum is used
 * in properties files to define the type of message boxes.
 * 
 * @author Jean-Francois Poilpret
 * @see MessageFactory
 */
public enum MessageType
{
	/**
	 * Type for an information message box.
	 */
	Info(JOptionPane.INFORMATION_MESSAGE),

	/**
	 * Type for a warning message box.
	 */
	Warning(JOptionPane.WARNING_MESSAGE),

	/**
	 * Type for a question message box.
	 */
	Question(JOptionPane.QUESTION_MESSAGE),

	/**
	 * Type for an error message box.
	 */
	Error(JOptionPane.ERROR_MESSAGE);

	private MessageType(int value)
	{
		_value = value;
	}
	
	/**
	 * Returns the matching {@link JOptionPane} {@code messageType} for the
	 * current message box type.
	 * 
	 * @return one of {@link JOptionPane#INFORMATION_MESSAGE}, 
	 * {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE}
	 * and {@link JOptionPane#ERROR_MESSAGE}
	 */
	public int	value()
	{
		return _value;
	}
	
	private final int	_value;
}
