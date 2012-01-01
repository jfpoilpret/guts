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

package net.guts.gui.window;

/**
 * Policy, passed to {@link WindowController#show} methods, to determine how the
 * bounds (location and size) of the Window to be displayed shall be computed.
 *
 * @author Jean-Francois Poilpret
 */
public enum BoundsPolicy
{
	/**
	 * This policy determines the window size by calling {@code pack()} on the 
	 * window (which calculates the preferred size, based on the 
	 * {@link java.awt.LayoutManager}s used by the window), and its position is
	 * centered relatively to its parent window (or the screen if there is no parent
	 * window).
	 */
	PACK_AND_CENTER,

	/**
	 * This policy determines the window size by calling {@code pack()} on the 
	 * window (which calculates the preferred size, based on the 
	 * {@link java.awt.LayoutManager}s used by the window), and its position is
	 * not changed (as returned by {@link java.awt.Window#getLocation()}.
	 */
	PACK_ONLY,
	
	/**
	 * This policy centers the window relatively to its parent window (or the 
	 * screen if there is no parent window); the window size is not changed (as 
	 * returned by {@link java.awt.Window#getSize()}.
	 */
	CENTER_ONLY,

	/**
	 * This policy doesn't change the window currently set position and size.
	 */
	KEEP_ORIGINAL_BOUNDS;

	/**
	 * Indicates whether this policy requires {@code pack()}.
	 */
	public boolean mustPack()
	{
		return this == PACK_AND_CENTER || this == PACK_ONLY;
	}

	/**
	 * Indicates whether this policy requires centering the window over its parent.
	 */
	public boolean mustCenter()
	{
		return this == PACK_AND_CENTER || this == CENTER_ONLY;
	}
}
