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

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import com.google.inject.Inject;

/**
 * Special Swing {@link JComponent} used to inject special properties needed 
 * by some Guice-GUI classes like {@link net.sf.guice.gui.docking.DockingManager}
 * or {@link net.guts.gui.dialog.support.AbstractTabbedPanel}. This component
 * is never actually added to any container.
 * <p/>
 * This class is declared {@code public} because Swing Application Framework can
 * inject resources only into public classes. Although you may use it in your
 * application, it is currently <b>NOT</b> part of the official API of Guice-GUI 
 * framework, hence its API may evolve in future versions.
 * <p/>
 * This component allows injection of two new properties (additionally to all
 * existing {@link JComponent} properties): {@code title} and {@code icon}.
 * 
 * @author Jean-Francois Poilpret
 */
public class ResourceComponent extends JComponent
{
	/**
	 * Constructs a new {@code ResourceComponent} from another actual component.
	 * Component properties are injected during construction.
	 */
	public ResourceComponent(JComponent content, String nameSuffix)
	{
		setName(content.getName() + nameSuffix);
		ResourceMap map = _context.getResourceMap(content.getClass(), JComponent.class);
		map.injectComponent(this);
	}
	
	public Icon getIcon()
	{
		return _icon;
	}
	
	public void setIcon(Icon icon)
	{
		_icon = icon;
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	public void setTitle(String title)
	{
		_title = title;
	}

	/**
	 * Initialization method statically injected by Guice.
	 * 
	 * @param context the Swing Application Framework {@code ApplicationContext}
	 */
	@Inject static void setContext(ApplicationContext context)
	{
		_context = context;
	}

	private static final long serialVersionUID = 801316528895829773L;

	static private ApplicationContext _context;
	private String _title;
	private Icon _icon;
}
