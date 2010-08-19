/*
 * Created on Aug 16, 2010
 * (c) 2010 Trumpet, Inc.
 *
 */
package net.guts.gui.dialog.support;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

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
    private final JList optionsList = new JList(listModel);
    
    public AbstractPreferencesPanel() {
        setLayout(new BorderLayout());
        bindListSelectionToViewContainer();
        
        split.setLeftComponent(new JScrollPane(optionsList));
        split.setRightComponent(viewContainer);
        add(split, BorderLayout.CENTER);
        
    }

//    protected void setupActions(List<GutsAction> actions) {
//        actions.add(getAcceptAction());
//        actions.add(getCancelAction());
//    }
    
    protected void register(JComponent component, Action applyAction, Action revertAction){
        ViewRegistration registration = new ViewRegistration(component.getName(), component, applyAction, revertAction, new ActionBinder(applyAction)); 
        listModel.addElement(registration);
    }

    protected GutsAction getAcceptAction() {
        return accept;
    }
    
    private final GutsAction accept = new GutsAction("ok"){
        protected void perform() {
            
            for (Action action : new MyIterable<ViewRegistration, Action>(listModel, ViewRegistration.applyActionFunction)) {
                if (action != null){
                    if (!action.isEnabled())
                        throw new IllegalStateException("Action " + action + " is not enabled");
                    action.actionPerformed(this.event());
                }
                    
            }
            
        }  
    };
    
    private void syncEnabled(){
        accept.action().setEnabled(allApplyActionsAreEnabled());
    }
    
    private boolean allApplyActionsAreEnabled(){
        for (Action action : new MyIterable<ViewRegistration, Action>(listModel, ViewRegistration.applyActionFunction)) {
            if (action != null && !action.isEnabled()){
                return false;
            }
        }
        return true;
    }
    
    final protected Iterable<JComponent> getSubComponents() {
        return new MyIterable<ViewRegistration, JComponent>(listModel, ViewRegistration.componentFunction);
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
    
    private static class MyIterable<E, V> implements Iterable<V>, Iterator<V>{

        private final ListModel model;
        private final Function<E, V> function;
        
        private int index = 0;
        
        public MyIterable(ListModel model, Function<E, V> function) {
            this.model = model;
            this.function = function;
        }
        
        public Iterator<V> iterator() {
            return this;
        }

        public boolean hasNext() {
            return index < model.getSize();
        }

        public V next() {
            return function.evaluate(((E)model.getElementAt(index++)));
        }

        public void remove() {
            throw new UnsupportedOperationException("remove() not supported");
        }
        
    }
    
    private static interface Function<A, B>{
        public B evaluate(A in);
    }
    
    private static class ViewRegistration{
        public final String name;
        public final JComponent view;
        private final Action applyAction;
        private final Action revertAction;
        private final ActionBinder actionBinder;
        
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
        
        public static Function<ViewRegistration, JComponent> componentFunction = new Function<ViewRegistration, JComponent>(){
            public JComponent evaluate(ViewRegistration reg) {
                return reg.view;
            }
        };
        
        public static Function<ViewRegistration, Action> applyActionFunction = new Function<ViewRegistration, Action>(){
            public Action evaluate(ViewRegistration reg) {
                return reg.applyAction;
            }
        };

        public static Function<ViewRegistration, Action> revertActionFunction = new Function<ViewRegistration, Action>(){
            public Action evaluate(ViewRegistration reg) {
                return reg.applyAction;
            }
        };

        public static Function<ViewRegistration, ActionBinder> actionBinderFunction = new Function<ViewRegistration, ActionBinder>(){
            public ActionBinder evaluate(ViewRegistration reg) {
                return reg.actionBinder;
            }
        };

    }
}
