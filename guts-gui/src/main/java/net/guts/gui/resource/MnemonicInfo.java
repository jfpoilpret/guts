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

package net.guts.gui.resource;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class, used by {@link InstanceInjector} implementations to deal with
 * mnemonics to inject into GUI components.
 * <p/>
 * Guts-GUI allows to define, in a resource bundle, a property that is a pure
 * {@code String}, but that has a mnemonic character, marked by an '&amp;' before
 * this mnemonic character, as in: {@code "Save &As..."}.
 * <p/>
 * {@code MnemonicInfo} will extract all necessary information from such text values.
 *
 * @author Jean-Francois Poilpret
 */
public final class MnemonicInfo
{
	/**
	 * Creates a {@code MnemonicInfo} instance that embeds the result of parsing 
	 * the given {@code text} to find a mnemonic.
	 * 
	 * @param text the text supposed to contain a mnemonic, marked by '&amp;'
	 * @return a {@code MenmonicInfo} instance that holds the result of parsing
	 * {@code text}
	 */
	static public MnemonicInfo extract(String text)
	{
		// Find the first & and check that there is one letter after it
		int ampersand = text.indexOf('&');
		if (ampersand >= 0 && ampersand + 1 < text.length())
		{
			// Remove this & from the text
			String displayText = text.substring(0, ampersand) + text.substring(ampersand + 1);
			return new MnemonicInfo(displayText, ampersand);
		}
		else
		{
			// Return a "default" instance that is directly usable by the caller!
			return new MnemonicInfo(text, -1);
		}
	}
	
	private MnemonicInfo(String text, int index)
	{
		_text = text;
		_index = index;
	}

	/**
	 * Returns the text from which the '&amp;' marker has been removed. E.g. for 
	 * {@code "Save &As..."}, the result would be {@code "Save As..."}.
	 * 
	 * @return the original text without '&amp;'
	 */
	public String getText()
	{
		return _text;
	}

	/**
	 * Returns the position in the text returned by {@link #getText()} of the 
	 * mnemonic character. E.g. for {@code "Save &As..."},  the returned index 
	 * would be {@code 5}.
	 * 
	 * @return the position index of the mnemonic character
	 */
	public int getMnemonicIndex()
	{
		return _index;
	}

	/**
	 * Returns the key code of the mnemonic character in the text returned by 
	 * {@link #getText()}. E.g. for {@code "Save &As..."}, the key code returned 
	 * would be {@link java.awt.event.KeyEvent#VK_A}.
	 * 
	 * @see java.awt.event.KeyEvent
	 * @return the key code of the mnemonic character
	 */
	public int getMnemonic()
	{
		if (_index < 0)
		{
			return KeyEvent.VK_UNDEFINED;
		}
		else
		{
			char text = Character.toUpperCase(_text.charAt(_index));
			Integer mnemonic = _keyCodes.get(text);
			return (mnemonic != null ? mnemonic : KeyEvent.VK_UNDEFINED);
		}
	}

	static final private Map<Character, Integer> _keyCodes = 
		new HashMap<Character, Integer>();
	// CSOFF: ExecutableStatementCountCheck
	static
	{
		for (char c = 'a'; c <= 'z'; c++)
		{
			_keyCodes.put(c, KeyEvent.VK_A + c - 'a');
		}
		for (char c = 'A'; c <= 'Z'; c++)
		{
			_keyCodes.put(c, KeyEvent.VK_A + c - 'A');
		}
		for (char c = '0'; c <= '9'; c++)
		{
			_keyCodes.put(c, KeyEvent.VK_0 + c - '0');
		}
		_keyCodes.put(',', KeyEvent.VK_COMMA);
		_keyCodes.put('<', KeyEvent.VK_LESS);

		_keyCodes.put('.', KeyEvent.VK_PERIOD);
		_keyCodes.put('>', KeyEvent.VK_GREATER);
		
		_keyCodes.put('?', KeyEvent.VK_HELP);
		_keyCodes.put('/', KeyEvent.VK_SLASH);
		
		_keyCodes.put(';', KeyEvent.VK_SEMICOLON);
		_keyCodes.put(':', KeyEvent.VK_COLON);
		
		_keyCodes.put('\'', KeyEvent.VK_QUOTE);
		_keyCodes.put('"', KeyEvent.VK_QUOTEDBL);
		
		_keyCodes.put('[', KeyEvent.VK_OPEN_BRACKET);
		_keyCodes.put('{', KeyEvent.VK_BRACELEFT);
		
		_keyCodes.put(']', KeyEvent.VK_CLOSE_BRACKET);
		_keyCodes.put('}', KeyEvent.VK_BRACERIGHT);
	
		// No VK for "|"?
//		_keyCodes.put('|', KeyEvent.VK_);
		_keyCodes.put('\\', KeyEvent.VK_BACK_SLASH);
		
		_keyCodes.put('+', KeyEvent.VK_PLUS);
		_keyCodes.put('=', KeyEvent.VK_EQUALS);

		_keyCodes.put('_', KeyEvent.VK_UNDERSCORE);
		_keyCodes.put('-', KeyEvent.VK_MINUS);

		_keyCodes.put('~', KeyEvent.VK_DEAD_TILDE);
		_keyCodes.put('`', KeyEvent.VK_BACK_QUOTE);

		_keyCodes.put('!', KeyEvent.VK_EXCLAMATION_MARK);
		_keyCodes.put('@', KeyEvent.VK_AT);
		_keyCodes.put('#', KeyEvent.VK_NUMBER_SIGN);
		_keyCodes.put('$', KeyEvent.VK_DOLLAR);
//		_keyCodes.put('%', KeyEvent.VK_);
		_keyCodes.put('^', KeyEvent.VK_CIRCUMFLEX);
		_keyCodes.put('&', KeyEvent.VK_AMPERSAND);
		_keyCodes.put('*', KeyEvent.VK_ASTERISK);
		_keyCodes.put('(', KeyEvent.VK_LEFT_PARENTHESIS);
		_keyCodes.put(')', KeyEvent.VK_RIGHT_PARENTHESIS);

		_keyCodes.put(' ', KeyEvent.VK_SPACE);
		_keyCodes.put('€', KeyEvent.VK_EURO_SIGN);
		_keyCodes.put('¡', KeyEvent.VK_INVERTED_EXCLAMATION_MARK);
	}
	// CSON: ExecutableStatementCountCheck
	
	final private String _text;
	final private int _index;
}
