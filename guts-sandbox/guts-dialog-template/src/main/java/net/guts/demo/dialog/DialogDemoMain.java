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

package net.guts.demo.dialog;

import java.util.List;

import net.guts.gui.action.ActionNamePolicy;
import net.guts.gui.action.DefaultActionNamePolicy;
import net.guts.gui.application.AbstractApplication;
import net.guts.gui.application.AppLifecycleStarter;
import net.guts.gui.dialog2.template.OkCancelModule;
import net.guts.gui.dialog2.template.TemplateModule;
import net.guts.gui.message.MessageModule;
import net.guts.gui.naming.ComponentNamePolicy;
import net.guts.gui.naming.ComponentNamingModule;
import net.guts.gui.naming.DefaultComponentNamePolicy;
import net.guts.gui.resource.Resources;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class DialogDemoMain extends AbstractApplication
{
	public static void main(String[] args)
	{
		new DialogDemoMain().launch(args);
	}

	@Override protected void initModules(String[] args, List<Module> modules)
	{
		modules.add(new ComponentNamingModule());
		modules.add(new TemplateModule());
		modules.add(new OkCancelModule());
		modules.add(new MessageModule());
		modules.add(new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(AppLifecycleStarter.class)
					.to(DialogDemoLifecycle.class)
					.in(Scopes.SINGLETON);
				Resources.bindRootBundle(binder(), getClass(), "resources");
				// Set our own component naming policy
				bind(ComponentNamePolicy.class)
					.toInstance(new NoSeparatorComponentNamePolicy());
				bind(ActionNamePolicy.class).toInstance(new NoSeparatorActionNamePolicy());
			}
		});
	}
}

// Special policy for automatically naming Swing components:
// Don't use "-" as a name separator, since all fields names already start with "_"
class NoSeparatorComponentNamePolicy extends DefaultComponentNamePolicy
{
	@Override protected String separator()
	{
		return "";
	}
}

class NoSeparatorActionNamePolicy extends DefaultActionNamePolicy
{
	@Override protected String separator()
	{
		return "";
	}
}
