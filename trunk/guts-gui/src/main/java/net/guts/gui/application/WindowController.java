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

package net.guts.gui.application;

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.google.inject.ImplementedBy;

/**
 * Main service API that deals with windows display. Any {@link javax.swing.JFrame}
 * or {@link javax.swing.JDialog} should be made visible through this service (rather
 * than directly calling {@code setVisible(true)} on these.
 * <p/>
 * Guts-GUI implementation of {@code WindowController} manages:
 * <ul>
 * <li>Automatic resource injection through 
 * {@link net.guts.gui.resource.ResourceInjector}</li>
 * <li>Automatic resource re-injection when {@link java.util.Locale} changes</li>
 * <li>Automatic GUI session state persistence through 
 * {@link net.guts.gui.session.SessionManager}</li>
 * <li>Location and size computation of displayed windows according to caller's 
 * request</li>
 * <li>Correct "stacking" of dialogs on windows</li>
 * </ul>
 * Whenever you need to display a window, you should {@code @Inject} 
 * {@code WindowController} and use its {@code show} methods.
 *
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(WindowControllerImpl.class)
public interface WindowController
{
	/**
	 * Policy, passed to {@link WindowController#show} methods, to determine how the
	 * bounds (location and size) of the Window to be displayed shall be computed.
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
		KEEP_ORIGINAL_BOUNDS
	};

	/**
	 * Show the given {@code frame} after setting its location and size according
	 * to {@code policy}. {@code frame} will have its resources automatically 
	 * injected (according to {@link net.guts.gui.resource.ResourceInjector} 
	 * principles). Depending on {@code restoreState} value, {@code frame} GUI 
	 * session state will be restored if it was previously persisted; in this case, 
	 * {@code policy} has no effect.
	 * 
	 * @param frame the frame to be displayed
	 * @param policy the policy to use for determining {@code frame}'s size and 
	 * location
	 * @param restoreState if {@code true}, state (bounds) will be restored if it
	 * was previously persisted; if {@code false}, state is not restored, hence
	 * only {@code policy} is used to set the bounds of {@code frame}.
	 */
	public void show(JFrame frame, BoundsPolicy policy, boolean restoreState);
	
	/**
	 * Show the given {@code dialog} after setting its location and size according
	 * to {@code policy}. {@code dialog} will have its resources automatically 
	 * injected (according to {@link net.guts.gui.resource.ResourceInjector} 
	 * principles). Depending on {@code restoreState} value, {@code dialog} GUI 
	 * session state will be restored if it was previously persisted; in this case, 
	 * {@code policy} has no effect.
	 * 
	 * @param dialog the dialog to be displayed
	 * @param policy the policy to use for determining {@code dialog}'s size and 
	 * location
	 * @param restoreState if {@code true}, state (bounds) will be restored if it
	 * was previously persisted; if {@code false}, state is not restored, hence
	 * only {@code policy} is used to set the bounds of {@code dialog}.
	 */
	public void show(JDialog dialog, BoundsPolicy policy, boolean restoreState);
	
	//TODO javadoc
	public void showApplet(boolean restoreState);

	/**
	 * Get the current active foreground window in the application.
	 * 
	 * @return the current active foreground window
	 */
	public Window getActiveWindow();
}
