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

import static org.fest.assertions.Assertions.assertThat;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups = "utest")
public class MnemonicInfoTest
{
	@Test(dataProvider = "mnemonics")
	public void checkMnemonicInfo(String originalText, String expectedText, int mnemonic, int index)
	{
		MnemonicInfo info = MnemonicInfo.extract(originalText);
		assertThat(info.getText()).as("Formatted text for `" + originalText + "`").isEqualTo(expectedText);
		assertThat(info.getMnemonic()).as("Mnemonic keycode for `" + originalText + "`").isEqualTo(mnemonic);
		assertThat(info.getMnemonicIndex()).as("Mnemonic index for `" + originalText + "`").isEqualTo(index);
	}
	
	@DataProvider(name = "mnemonics")
	public Object[][] getMnemonicTestData()
	{
		return new Object[][]
		{
			{"Save", "Save", KeyEvent.VK_UNDEFINED, -1},
			{"&Save", "Save", KeyEvent.VK_S, 0},
			{"Save &As...", "Save As...", KeyEvent.VK_A, 5},
			{"Apples && Oranges", "Apples & Oranges", KeyEvent.VK_AMPERSAND, 7},
			{"Help&?", "Help?", KeyEvent.VK_HELP, 4},
		};
	}
}
