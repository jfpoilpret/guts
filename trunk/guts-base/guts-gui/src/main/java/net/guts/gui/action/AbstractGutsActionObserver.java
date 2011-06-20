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

public abstract class AbstractGutsActionObserver implements GutsActionObserver
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

	final protected GutsAction target()
	{
		return _target;
	}
	
	protected void beforeActionPerform()
	{
	}
	
	protected void afterActionPerform()
	{
	}
	
	protected void handleCaughtException(RuntimeException e)
	{
	}
	
	private GutsAction _target;
}
