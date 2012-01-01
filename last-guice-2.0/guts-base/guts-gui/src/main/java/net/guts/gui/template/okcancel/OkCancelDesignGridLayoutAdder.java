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

package net.guts.gui.template.okcancel;

import java.awt.Container;

import javax.swing.JButton;

import net.java.dev.designgridlayout.DesignGridLayout;
import net.java.dev.designgridlayout.DesignGridLayoutManager;
import net.java.dev.designgridlayout.IBarRow;
import net.java.dev.designgridlayout.Tag;

class OkCancelDesignGridLayoutAdder implements OkCancelLayoutAdder
{
	@Override public Container layout(
		Container view, JButton ok, JButton cancel, JButton apply)
	{
		DesignGridLayoutManager manager = (DesignGridLayoutManager) view.getLayout();
		DesignGridLayout layout = manager.designGridLayout();
		layout.emptyRow();
		IBarRow bar = layout.row().bar();
		add(bar, ok, Tag.OK);
		add(bar, apply, Tag.APPLY);
		add(bar, cancel, Tag.CANCEL);
		return view;
	}
	
	static private void add(IBarRow bar, JButton button, Tag tag)
	{
		if (button != null)
		{
			bar.add(button, tag);
		}
	}
}
