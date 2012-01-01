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

import java.util.EnumSet;

import javax.swing.JOptionPane;

/**
 * The choice selected by the user on a message box. This depends on the button
 * that the user has selected to close the message box.
 * <p/>
 * User choices can be one of the following:
 * <ul>
 * <li>{@link #YES}</li>
 * <li>{@link #NO}</li>
 * <li>{@link #CANCEL}</li>
 * </ul>
 * 
 * @author Jean-Francois Poilpret
 * @see MessageFactory
 */
public enum UserChoice
{
	/**
	 * User choice when the user has clicked the "OK" or "Yes" button.
	 */
	YES(JOptionPane.YES_OPTION),

	/**
	 * User choice when the user has clicked the "No" button.
	 */
	NO(JOptionPane.NO_OPTION),
	
	/**
	 * User choice when the user has clicked the "Cancel" button.
	 */
	CANCEL(JOptionPane.CANCEL_OPTION);
	
	private UserChoice(int value)
	{
		_value = value;
	}
	
	/**
	 * Returns the matching {@link JOptionPane} returned value (user selection)
	 * for the current user's choice.
	 * 
	 * @return one of {@link JOptionPane#YES_OPTION}, {@link JOptionPane#NO_OPTION}
	 * and {@link JOptionPane#CANCEL_OPTION}
	 */
	public int value()
	{
		return _value;
	}

	/**
	 * Returns the right {@code UserChoice}, given one of the possible user options
	 * returned by {@link JOptionPane#showConfirmDialog(java.awt.Component, Object)}.
	 * 
	 * @param value one of {@link JOptionPane#YES_OPTION}, {@link JOptionPane#NO_OPTION}
	 * and {@link JOptionPane#CANCEL_OPTION}
	 * @return the {@code UserChoice} matching the {@link JOptionPane} user option
	 */
	static public UserChoice get(int value)
	{
		for (UserChoice choice: EnumSet.allOf(UserChoice.class))
		{
			if (choice.value() == value)
			{
				return choice;
			}
		}
		return null;
	}
	
	private final int _value;
}
