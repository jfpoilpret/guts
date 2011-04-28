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

import static org.fest.assertions.Assertions.assertThat;
import org.testng.annotations.Test;

import net.guts.common.injection.InjectionListeners;

import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "itest")
public class ActionNamingTest
{
	public void checkActionProperlyNamedWithInterceptedClass()
	{
		ActionsContainer actions = getInstance(ActionsContainer.class);
		assertThat(actions.fixedAction.name()).as("fixedAction.name()").isNotNull().isEqualTo("ActionNamingTest$ActionsContainer-fixedAction");
	}
	static public class ActionsContainer
	{
		public GutsAction dynamicAction()
		{
			return null;
		}

		final public GutsAction fixedAction = new GutsAction()
		{
			@Override protected void perform()
			{
			}
		};
	}
	
	static private <T> T getInstance(Class<T> clazz)
	{
		Injector injector = Guice.createInjector(new ActionModule());
		InjectionListeners.injectListeners(injector);
		return injector.getInstance(clazz);
	}
}
