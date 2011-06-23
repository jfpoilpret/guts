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

package net.guts.gui.action;

/**
 * Implementation of this interface can be {@link GutsAction#addActionObserver added} 
 * to a {@link GutsAction} in order to add some behavior to the observed {@code GutsAction}.
 * This is particularly useful for {@link net.guts.gui.template.TemplateDecorator}
 * implementations that need to add some behavior to a given action, provided by the
 * end-developer.
 * 
 * @see GutsActionAdapter
 * @author Jean-Francois Poilpret
 */
public interface GutsActionObserver
{	
	/**
	 * Called just before calling {@code target} {@link #perform()}, this method
	 * can be overridden to perform any necessary preliminary work.
	 * 
	 * @param target the {@link GutsAction} to which {@code this} observer has been added,
	 * and which has triggered this call
	 */
	public void beforeActionPerform(GutsAction target);
	
	/**
	 * Called just after calling {@code target} {@link #perform()}, this method
	 * can be implemented to perform any necessary post-performance work.
	 * 
	 * @param target the {@link GutsAction} to which {@code this} observer has been added,
	 * and which has triggered this call
	 */
	public void afterActionPerform(GutsAction target);
	
	/**
	 * Called if an exception was caught while {@code target} was performing its action.
	 * Default behavior is to re-throw the caught exception.
	 * <p/>
	 * Note that {@link #afterTargetPerform()} is not called when 
	 * {@code target.actionPerformed()} throws an exception.
	 * 
	 * @param target the {@link GutsAction} to which {@code this} observer has been added,
	 * and which has triggered this call
	 * @param e the exception caught by {@link #perform()} (thrown by 
	 * {@code target.actionPerformed()}).
	 */
	public void handleCaughtException(GutsAction target, RuntimeException e);
}
