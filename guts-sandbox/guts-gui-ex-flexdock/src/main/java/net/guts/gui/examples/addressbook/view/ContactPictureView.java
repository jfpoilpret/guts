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

package net.guts.gui.examples.addressbook.view;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JLabel;

public class ContactPictureView extends JLabel
{
	static final private long serialVersionUID = 7627517518039773295L;
	static final private int IMG_SIZE = 200;
	
	public ContactPictureView(Icon picture)
	{
		setHorizontalAlignment(JLabel.CENTER);
		setMinimumSize(new Dimension(IMG_SIZE, IMG_SIZE));
		setIcon(picture);
		if (picture != null)
		{
			setText("");
		}
		else
		{
			setText("No picture for this contact");
		}
	}
}
