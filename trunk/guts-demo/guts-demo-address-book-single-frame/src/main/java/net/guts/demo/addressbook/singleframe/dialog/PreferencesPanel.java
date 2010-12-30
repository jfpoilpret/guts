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

/*
 * Created on Aug 16, 2010
 * (c) 2010 Trumpet, Inc.
 *
 */

package net.guts.demo.addressbook.singleframe.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.gui.action.GutsAction;
import net.guts.gui.dialog.support.AbstractPreferencesPanel;

import com.google.inject.Inject;

/**
 * @author kevin
 */
public class PreferencesPanel extends AbstractPreferencesPanel
{
	static final private Logger _log = LoggerFactory.getLogger(PreferencesPanel.class);

	private final ApplyControllingCheckbox _check1 = new ApplyControllingCheckbox();
	private final ApplyControllingCheckbox _check2 = new ApplyControllingCheckbox();
	private final ApplyControllingCheckbox _check3 = new ApplyControllingCheckbox();

	@Inject public PreferencesPanel()
	{
		register("Options 1", _check1, _check1.getApplyAction(), null);
		register("Options 2", _check2, _check2.getApplyAction(), null);
		register("Options 3", _check3, _check3.getApplyAction(), null);
		register("Button option 1", namedComponent("test1"), null, null);
		register("Button option 2", namedComponent("test2"), null, null);
	}

	private JComponent namedComponent(String name)
	{
		JButton b = new JButton(name);
		b.setName(name);
		return b;
	}

	private static class ApplyControllingCheckbox extends JCheckBox
	{
		public ApplyControllingCheckbox()
		{
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					syncApplyActionEnabled();
				}
			});
			syncApplyActionEnabled();
		}

		private void syncApplyActionEnabled()
		{
			_apply.setEnabled(isSelected());
		}

		public GutsAction getApplyAction()
		{
			return _apply;
		}

		private final GutsAction _apply = new GutsAction()
		{
			@Override protected void perform()
			{
				_log.debug("Apply - " + getName());
			}
		};
	}
}
