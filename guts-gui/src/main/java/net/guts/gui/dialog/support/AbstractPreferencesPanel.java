/*
 * Created on Aug 16, 2010
 * (c) 2010 Trumpet, Inc.
 *
 */
package net.guts.gui.dialog.support;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.guts.gui.action.GutsAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO need to release bindings when view is closed (this requires reliably close notifications from parent dialog)
 */
public class AbstractPreferencesPanel extends AbstractMultiPanel {

    static final private Logger log = LoggerFactory.getLogger(AbstractPreferencesPanel.class);

    
    private final JPanel viewContainer = new JPanel(new BorderLayout());
    private final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private final DefaultListModel listModel = new DefaultListModel();
    private final List<ViewRegistration> registrations = new ArrayList<ViewRegistration>();
    private final JList optionsList = new JList(listModel);
    
    public AbstractPreferencesPanel() {
        setLayout(new BorderLayout());
        bindListSelectionToViewContainer();
        
        split.setLeftComponent(new JScrollPane(optionsList));
        split.setRightComponent(viewContainer);
        add(split, BorderLayout.CENTER);
        
    }

    /**
     * Registers a preferences component to be displayed in the preferences list.  The name of the component
     * will be used as the list display (probably want to change this?? To what??).
     * @param component the preferences component to register
     * @param applyAction the action that will be called when the OK button is clicked (the OK button enablement state is bound to the enablement state of all registered apply actions)
     * @param revertAction the action that will be called when the Cancel button is clicked (the Cancel button is always enabled as it can be used to close the dialog)
     */
    protected void register(JComponent component, Action applyAction, Action revertAction){
        ViewRegistration registration = new ViewRegistration(component.getName(), component, applyAction, revertAction, new ActionBinder(applyAction)); 
        registrations.add(registration);
        listModel.addElement(registration);
    }

    /**
     * @see net.guts.gui.dialog.support.AbstractPanel#getAcceptAction()
     */
    protected GutsAction getAcceptAction() {
        return accept;
    }
    
    private final GutsAction accept = new GutsAction("ok"){
        protected void perform() {
            for (ViewRegistration registration : registrations) {
                Action action = registration.applyAction;
                if (action != null && action.isEnabled()){
                    action.actionPerformed(this.event());
                }
            }
        }  
    };
    
    protected GutsAction getCancelAction() {
        return revert;
    }
    
    private final GutsAction revert = new GutsAction("cancel"){
        protected void perform() {
            for (ViewRegistration registration : registrations) {
                Action action = registration.revertAction;
                if (action != null && action.isEnabled()){
                    action.actionPerformed(this.event());
                }
            }
            
            getParentDialog().close(true);
        }  
    };
   
    
    /**
     * Synchronizes global action enablement with the enablement state of actions from registered preferences panels
     * If one or more registered apply actions are enabled, then the global apply action is enabled
     */
    private void syncEnabled(){
        accept.action().setEnabled(atLeastOneApplyActionsAreEnabled());
    }
    
    private boolean atLeastOneApplyActionsAreEnabled(){
        for (ViewRegistration registration : registrations) {
            Action action = registration.applyAction;
            if (action != null && action.isEnabled()){
                return true;
            }
        }
        return false;
    }

    /**
     * @see net.guts.gui.dialog.support.AbstractMultiPanel#getSubComponents()
     */
    final protected Iterable<JComponent> getSubComponents() {
        List<JComponent> components = new ArrayList<JComponent>(registrations.size());
        for (ViewRegistration registration : registrations) {
            components.add(registration.view);
        }
        return components;
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
    
    /**
     * Allows top-level action enablement to be adjusted when the actions of registered preferences panels
     * are enabled/disabled.
     */
    private class ActionBinder implements PropertyChangeListener{
        private final Action source;
        public ActionBinder(Action source){
            this.source = source;
            if (source == null)
                return;
            
            sync();
            source.addPropertyChangeListener(this);
        }
        
        private void sync(){
            syncEnabled();
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if ("enabled".equals(evt.getPropertyName())){
                sync();
            }
        }
        
        public void unbind(){
            if (source != null)
                source.removePropertyChangeListener(this);
        }
    }
    


    /**
     * Represents a registered preferences view
     */
    private static class ViewRegistration{
        public final String name;
        public final JComponent view;
        public final Action applyAction;
        public final Action revertAction;
        public final ActionBinder actionBinder;
        
        public ViewRegistration(String name, JComponent view, Action applyAction, Action revertAction, ActionBinder actionBinder){
            this.name = name;
            this.view = view;
            this.applyAction = applyAction;
            this.revertAction = revertAction;
            this.actionBinder = actionBinder;
        }
        
        public String toString() {
            return name;
        }

    }
}
