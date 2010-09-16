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

package net.guts.gui.util;

import java.awt.Cursor;

/**
 * A type of predefined {@link Cursor}. All available predefined types are
 * defined in {@link Cursor}.
 * <p/>
 * This enum is used to inject {@link Cursor}s from properties files, based on 
 * their logical name as defined by this enum.
 * 
 * @author Jean-Francois Poilpret
 */
public enum CursorType
{
	CROSSHAIR(Cursor.CROSSHAIR_CURSOR),
	HAND(Cursor.HAND_CURSOR),
	TEXT(Cursor.TEXT_CURSOR),
	WAIT(Cursor.WAIT_CURSOR),
	E_RESIZE(Cursor.E_RESIZE_CURSOR),
	W_RESIZE(Cursor.W_RESIZE_CURSOR),
	N_RESIZE(Cursor.N_RESIZE_CURSOR),
	S_RESIZE(Cursor.S_RESIZE_CURSOR),
	NE_RESIZE(Cursor.NE_RESIZE_CURSOR),
	SE_RESIZE(Cursor.SE_RESIZE_CURSOR),
	NW_RESIZE(Cursor.NW_RESIZE_CURSOR),
	SW_RESIZE(Cursor.SW_RESIZE_CURSOR),
	MOVE(Cursor.MOVE_CURSOR),
	DEFAULT(Cursor.DEFAULT_CURSOR);

	private CursorType(int id)
	{
		_id = id;
	}
	
	/**
	 * Builds and returns the {@link CursorInfo} for the given predefined cursor
	 * type.
	 * 
	 * @return the {@link CursorInfo} for this type
	 */
	public CursorInfo getCursorInfo()
	{
		if (_info == null)
		{
			_info = new CursorInfo(Cursor.getPredefinedCursor(_id));
		}
		return _info;
	}
	
	private final int _id;
	private CursorInfo _info = null;
}
