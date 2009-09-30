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

/**
 * TODO
 *
 * @author Jean-Francois Poilpret
 */
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
			return null;
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
		//FIXME convert to official KeyEvent constants!
		return _text.charAt(_index);
	}
	
	final private String _text;
	final private int _index;
}
