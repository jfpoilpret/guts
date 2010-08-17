/*
 * Created on Aug 16, 2010
 * (c) 2010 Trumpet, Inc.
 *
 */
package net.guts.gui.dialog.support;

import java.awt.BorderLayout;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author kevin
 */
public class AbstractPreferencesPanel extends JPanel {

    private final JPanel viewContainer = new JPanel(new BorderLayout());
    private final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private final DefaultListModel listModel = new DefaultListModel();
    private final JList optionsList = new JList(listModel);
    
    public AbstractPreferencesPanel() {
        setLayout(new BorderLayout());
        bindListSelectionToViewContainer();
        
        split.setLeftComponent(new JScrollPane(optionsList));
        split.setRightComponent(viewContainer);
        add(split, BorderLayout.CENTER);
    }

    protected void register(JComponent component, Action applyAction, Action revertAction){
        listModel.addElement(new ViewRegistration(component.getName(), component, applyAction, revertAction));
    }

    private void bindListSelectionToViewContainer(){
        optionsList.addListSelectionListener(new ListSelectionListener(){

            public void valueChanged(ListSelectionEvent e) {
                ViewRegistration selectedRegistration = (ViewRegistration)optionsList.getSelectedValue();
                if (viewContainer.getComponentCount() > 0)
                    viewContainer.remove(0);
                
                viewContainer.add(selectedRegistration.view, 0);
                viewContainer.validate();
                viewContainer.repaint();
                
            }
            
        });
        
    }
    
    private static class ViewRegistration{
        public final String name;
        public final JComponent view;
        public final Action applyAction;
        public final Action revertAction;
        
        public ViewRegistration(String name, JComponent view, Action applyAction, Action revertAction){
            this.name = name;
            this.view = view;
            this.applyAction = applyAction;
            this.revertAction = revertAction;
        }
        
        public String toString() {
            return name;
        }
    }
}
