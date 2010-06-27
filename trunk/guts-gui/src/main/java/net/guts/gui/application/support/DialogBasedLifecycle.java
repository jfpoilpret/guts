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

package net.guts.gui.application.support;

import javax.swing.JComponent;

import net.guts.gui.application.AppLifecycleStarter;
import net.guts.gui.application.WindowController.BoundsPolicy;
import net.guts.gui.application.WindowController.StatePolicy;
import net.guts.gui.dialog.DialogFactory;
import net.guts.gui.exit.ExitController;

import com.google.inject.Inject;

/**
 * Abstract {@link AppLifecycleStarter} that you can subclass if you are
 * developing a dialog-based application.
 * <p/>
 * The following snippet shows an example:
 * <pre>
 * TODO
 * </pre>
 * You will have to make sure you bind you subclass to {@link AppLifecycleStarter}
 * from one of your {@link com.google.inject.Module}s:
 * <pre>
 *     &#64;Override protected void configure()
 *     {
 *         bind(AppLifecycleStarter.class)
 *             .to(MyAppLifecycleStarter.class).asEagerSingleton();
 *     }
 * </pre>
 *
 * @author Jean-Francois Poilpret
 */
//CSOFF: AbstractClassName
abstract public class DialogBasedLifecycle implements AppLifecycleStarter
{
	@Inject void init(
		DialogFactory dialogFactory, ExitController exitController)
	{
		_dialogFactory = dialogFactory;
		_exitController = exitController;
	}

	abstract protected Class<? extends JComponent> getDialogClass();
	
	/* (non-Javadoc)
	 * @see net.guts.gui.application.AppLifecycleStarter#ready()
	 */
	@Override final public void ready()
	{
		// This method will never be called, since startup(0 will force shutdown!
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.application.AppLifecycleStarter#startup(java.lang.String[])
	 */
	@Override final public void startup(String[] args)
	{
		Class<? extends JComponent> dialog = getDialogClass();
		_dialogFactory.showDialog(
			dialog, BoundsPolicy.PACK_AND_CENTER, StatePolicy.RESTORE_IF_EXISTS);
		// Shutdown when dialog has closed
		_exitController.shutdown();
	}

	private DialogFactory _dialogFactory;
	private ExitController _exitController;
}
//CSON: AbstractClassName
