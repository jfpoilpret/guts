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

public final class MnemonicInfo
{
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
	
	public String getText()
	{
		return _text;
	}
	
	public int getMnemonicIndex()
	{
		return _index;
	}
	
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
