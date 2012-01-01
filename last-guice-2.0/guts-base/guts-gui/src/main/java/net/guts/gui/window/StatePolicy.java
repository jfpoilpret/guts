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
 * Policy, passed to {@link WindowController#show} methods, to determine if session
 * state (bounds) should be restored or not.
 *
 * @author Jean-Francois Poilpret
 */
public enum StatePolicy
{
	/**
	 * Session state won't be restored, only {@link BoundsPolicy} will be used when
	 * showing a window.
	 */
	DONT_RESTORE,
	
	/**
	 * Session state will be restored if it was saved previously; if state was never
	 * saved before, then {@link BoundsPolicy} will be used instead.
	 */
	RESTORE_IF_EXISTS
}
