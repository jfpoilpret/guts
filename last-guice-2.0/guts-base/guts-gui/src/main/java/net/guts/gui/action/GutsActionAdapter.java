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
 * An abstract adapter class for listening to performance of a {@link GutsAction}. 
 * The methods in this class are empty. This class exists as a convenience for 
 * creating listener objects.
 * <p/>
 * Extend this class to create a {@code GutsActionObserver} and override only the 
 * methods of interest (if you implement the {@code GutsActionObserver}
 * interface, you have to define all its methods; this abstract class
 * defines null methods for them all, so you can only have to define methods 
 * you care about).
 * <p/>
 * Create a listener object using the extended class and then register it with a 
 * {@code GutsAction} using {@link GutsAction#addActionObserver}.
 * 
 * @author Jean-Francois Poilpret
 */
//CSOFF: AbstractClassName
public abstract class GutsActionAdapter implements GutsActionObserver
{
	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.action.GutsActionObserver#beforeActionPerform(net.guts.gui.action.GutsAction)
	 */
	@Override final public void beforeActionPerform(GutsAction target)
	{
		_target = target;
		beforeActionPerform();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.action.GutsActionObserver#afterActionPerform(net.guts.gui.action.GutsAction)
	 */
	@Override final public void afterActionPerform(GutsAction target)
	{
		_target = target;
		afterActionPerform();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.action.GutsActionObserver#handleCaughtException(net.guts.gui.action.GutsAction, java.lang.RuntimeException)
	 */
	@Override final public void handleCaughtException(GutsAction target, RuntimeException e)
	{
		_target = target;
		handleCaughtException(e);
	}

	/**
	 * Return the {@link GutsAction} for which a listener method is being called.
	 */
	final protected GutsAction target()
	{
		return _target;
	}
	
	/**
	 * Called just before calling {@link #target()} {@code perform()}, this method
	 * can be overridden to perform any necessary preliminary work.
	 */
	protected void beforeActionPerform()
	{
	}
	
	/**
	 * Called just after calling {@link #target()} {@code perform()}, this method
	 * can be implemented to perform any necessary post-performance work.
	 */
	protected void afterActionPerform()
	{
	}
	
	/**
	 * Called if an exception was caught while {@link #target()} was performing its action.
	 * <p/>
	 * Note that {@link #afterTargetPerform()} is not called when 
	 * {@code target.actionPerformed()} throws an exception.
	 * 
	 * @param e the exception caught by {@link GutsAction#perform()} (thrown by 
	 * {@code target().actionPerformed()}).
	 */
	protected void handleCaughtException(RuntimeException e)
	{
	}
	
	private GutsAction _target;
}
//CSON: AbstractClassName
