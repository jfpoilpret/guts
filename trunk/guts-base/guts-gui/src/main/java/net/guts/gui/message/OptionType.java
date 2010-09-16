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
 * A type of options (users possible choices) for a message box. This determines
 * the buttons that will be displayed in the message box and their labels.
 * <p/>
 * Message boxes can use one of the following option types:
 * <ul>
 * <li>{@link #Ok}</li>
 * <li>{@link #YesNo}</li>
 * <li>{@link #YesNoCancel}</li>
 * <li>{@link #OkCancel}</li>
 * </ul>
 * <p/>
 * Although not directly used in {@link MessageFactory} API, this enum is used
 * in properties files to define the option type of message boxes.
 * 
 * @author Jean-Francois Poilpret
 * @see MessageFactory
 */
public enum OptionType
{
	/**
	 * Option that displays just the "OK" button in the message box. Typically
	 * used in information and error message boxes.
	 */
	Ok(JOptionPane.DEFAULT_OPTION),

	/**
	 * Option that displays "Yes" and "No" buttons in the message box. Typically
	 * used in question message boxes.
	 */
	YesNo(JOptionPane.YES_NO_OPTION),
	
	/**
	 * Option that displays "Yes", "No" and "Cancel" buttons in the message box.
	 */
	YesNoCancel(JOptionPane.YES_NO_CANCEL_OPTION),
	
	/**
	 * Option that displays "OK" and "Cancel" buttons in the message box.
	 * Typically used in warning message boxes.
	 */
	OkCancel(JOptionPane.OK_CANCEL_OPTION);
	
	private OptionType(int value)
	{
		_value = value;
	}
	
	/**
	 * Returns the matching {@link JOptionPane} {@code optionType} for the
	 * current option type.
	 * 
	 * @return one of {@link JOptionPane#OK_OPTION}, {@link JOptionPane#YES_NO_OPTION}, 
	 * {@link JOptionPane#YES_NO_CANCEL_OPTION} and {@link JOptionPane#OK_CANCEL_OPTION}
	 */
	public int	value()
	{
		return _value;
	}
	
	private final int	_value;
}
