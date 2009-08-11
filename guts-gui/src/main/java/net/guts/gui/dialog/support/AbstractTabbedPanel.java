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

package net.guts.gui.dialog.support;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.util.AbstractList;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import org.jdesktop.application.Task;

import net.guts.gui.dialog.Closable;
import net.guts.gui.dialog.ParentDialog;
import net.guts.gui.util.ResourceComponent;

/**
 * Abstract panel to be used in dialogs containing several tabs managed in a
 * single {@link JTabbedPane}.
 * <p/>
 * This class manages most repetitive stuff for you: creating OK/Cancel buttons
 * and associated Actions, inject resources into components from properties
 * files...
 * <p/>
 * Typically, your panels would look like:
 * <pre>
 * public class MyTabbedPanel extends AbstractTabbedPanel {
 *     static final private String ID = "my-tabbed-panel-id";
 *     
 *     final private JComponent _tab1 = new Tab1Panel();
 *     final private JComponent _tab2 = new Tab2Panel();
 *     ...
 *     &#64;Inject private SomeService _service;
 *     ...
 *     
 *     public MyTabbedPanel() {
 *         super(ID);
 *         _tabbedPane.add(_tab1);
 *         _tabbedPane.add(_tab2);
 *         ...
 *         // Register listeners
 *     }
 *     
 *     &#64;Override public Task accept(ParentDialog parent) {
 *         // super method will call accept on each individual tab
 *         super.accept();
 *         // Something to be done when user clicks OK
 *         ...
 *     }
 * }
 * 
 * class Tab1Panel extends JPanel implements TabPanelAcceptor {
 *     static final private String ID = "tab1-panel-id";
 *     
 *     final private JLabel _label = new JLabel();
 *     final private JTextField _field = new JTextField();
 *     ...
 *     
 *     public MyPanel() {
 *         super(ID);
 *         _label.setName(ID + "-label");
 *         _field.setName(ID + "-field");
 *         ...
 *         // Layout components
 *         // Register listeners
 *     }
 *     
 *     public void accept() {
 *         // Something to be done when user clicks OK
 *         ...
 *     }
 * }
 * </pre>
 * <p/>
 * Tab panels can be instances of any class as long as it is a subclass (direct
 * or not) of {@link JComponent}. Similarly, tab panels may or may not implement
 * {@link TabPanelAcceptor}, {@link net.guts.gui.dialog.Resettable} or 
 * {@link Closable}. If they do, {@code AbstractTabbedPanel} will delegate to 
 * them the corresponding calls.
 * 
 * @author Jean-Francois Poilpret
 * @see AbstractPanel
 * @see AbstractMultiPanel
 */
public abstract class AbstractTabbedPanel extends AbstractMultiPanel 
	implements Acceptor, Closable
{
	/**
	 * Constructs a new abstract tabbed panel, with a unique identifier, used 
	 * for resources internationalization.
	 * 
	 * @param id unique identifier for this dialog panel
	 */
	protected AbstractTabbedPanel(String id)
	{
		super(id);
		_tabbedPane.setName(id + TABPANE_NAME_SUFFIX);
		_tabbedPane.addContainerListener(_listener);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.dialog.support.AbstractPanel#initLayout()
	 */
	@Override final protected void initLayout()
    {
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(INSETS, INSETS, INSETS, INSETS);
		add(_tabbedPane, constraints);
    }

	/**
	 * Accepts all individual tab panes added to this panel (if they implement
	 * {@link Acceptor}). You will generally override this method to perform
	 * your own specific acceptance code. When overriding this method, don't
	 * forget to call {@code super.accept()} before any other code.
	 */
	public Task<Void, Void> accept(ParentDialog parent)
    {
		for (Component subpane: getSubComponents())
		{
			if (subpane instanceof TabPanelAcceptor)
			{
				((TabPanelAcceptor) subpane).accept();
			}
		}
		return null;
    }

	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.dialog.Closable#canClose()
	 */
	public boolean canClose()
    {
		for (Component subpane: getSubComponents())
		{
			if (	(subpane instanceof Closable)
				&&	!((Closable) subpane).canClose())
			{
				return false;
			}
		}
	    return true;
    }

	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.dialog.support.AbstractMultiPanel#getMainComponent()
	 */
	@Override final protected Iterable<JComponent> getSubComponents()
    {
		return new AbstractList<JComponent>()
		{
			@Override public JComponent get(int index)
			{
				return (JComponent) _tabbedPane.getComponent(index);
			}

			@Override public int size()
			{
				return _tabbedPane.getComponentCount();
			}
			
		};
    }

	static private class TabsListListener extends ContainerAdapter
	{
		@Override public void componentAdded(ContainerEvent e)
        {
			if (	e.getChild() instanceof JComponent
				&&	e.getContainer() instanceof JTabbedPane)
			{
				JComponent child = (JComponent) e.getChild();
				JTabbedPane parent = (JTabbedPane) e.getContainer();
				// Find the added tab in the parent
				int index = parent.indexOfComponent(child);
				if (index >= 0)
				{
					//FIXME mnemonics are not converted! (SAF issue)
					// Inject wrapper with tab additional info
					ResourceComponent wrapper = new ResourceComponent(
						child, TAB_NAME_SUFFIX);
					String title = wrapper.getTitle();
					Icon icon = wrapper.getIcon();
					String tip = wrapper.getToolTipText();
					// Sets extra info for the new tab
					parent.setTitleAt(index, title);
					parent.setIconAt(index, icon);
					parent.setToolTipTextAt(index, tip);
				}
			}
        }
	}

	private static final long serialVersionUID = -4667291820304038305L;

	static final private int INSETS = 4;
	static final private String TABPANE_NAME_SUFFIX = "-tabs";
	static final private String TAB_NAME_SUFFIX = "-TAB";
	
	static final private TabsListListener _listener = new TabsListListener();

	/**
	 * The actual {@link JTabbedPane} used by this panel. You will use it to
	 * add individual tabs.
	 */
	final protected JTabbedPane _tabbedPane = new JTabbedPane();
}
