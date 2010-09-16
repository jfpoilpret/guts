/*
 * Created on Aug 17, 2010
 * (c) 2010 Trumpet, Inc.
 *
 */
package net.guts.gui.dialogalt;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.google.inject.Inject;

/**
 * @author kevin
 */
public class DialogTemplateImpl extends DialogTemplate {

    private final JPanel viewContainer = new JPanel(new BorderLayout());
    private final JButton okButton = new JButton();
    private final JButton cancelButton = new JButton();
    private final JButton revertButton = new JButton();
    private final JButton applyButton = new JButton();

    private final List<ActionBinder> actionBinders = new ArrayList<ActionBinder>();
    private final Action okAction = new OkAction();
    private final Action cancelAction = new CancelAction();
    private final Action applyAction = new ApplyAction();
    private final Action revertAction = new RevertAction();

    private ViewCloser viewCloser = new NoOpViewCloser();
    
    private Action externalApplyAction = new NoOpAction();
    private Action externalRevertAction = new NoOpAction();
    
    private boolean cancelled = false;
    
    @Inject
    public DialogTemplateImpl() {
        okButton.setAction(okAction);
        cancelButton.setAction(cancelAction);
        revertButton.setAction(revertAction);
        applyButton.setAction(applyAction);
        
        rebindActions();
        
        setLayout(new BorderLayout());
        add(viewContainer, BorderLayout.CENTER);
        add(buildButtonView(), BorderLayout.SOUTH);
        
//        getRootPane().setDefaultButton(okButton);
    }
    
    private void rebindActions(){
        for (ActionBinder binder : actionBinders) {
            binder.unbind();
        }
        actionBinders.clear();
        
        actionBinders.add(new ActionBinder(applyAction, okAction));
        actionBinders.add(new ActionBinder(externalApplyAction, applyAction));
        actionBinders.add(new ActionBinder(externalRevertAction, revertAction));
        
    }
    
    /** 
     * @see net.guts.gui.dialogalt.DialogTemplate#wasCancelled()
     */
    public boolean wasCancelled(){
        return cancelled;
    }
    
    /** 
     * @see net.guts.gui.dialogalt.DialogTemplate#setApplyAction(javax.swing.Action)
     */
    public void setApplyAction(Action applyAction){
        this.externalApplyAction = applyAction;
        rebindActions();
    }
    
    /** 
     * @see net.guts.gui.dialogalt.DialogTemplate#setRevertAction(javax.swing.Action)
     */
    public void setRevertAction(Action revertAction) {
        this.externalRevertAction = revertAction;
        rebindActions();
    }
    
    /** 
     * @see net.guts.gui.dialogalt.DialogTemplate#setView(javax.swing.JComponent)
     */
    public void setView(JComponent view){
        if (viewContainer.getComponentCount() > 0)
            viewContainer.remove(0);
        
        viewContainer.add(view, 0);
        validate();
        repaint();
    }
    
    /** 
     * @see net.guts.gui.dialogalt.DialogTemplate#setViewCloser(net.guts.gui.dialogalt.ViewCloser)
     */
    public void setViewCloser(ViewCloser viewCloser){
        this.viewCloser = viewCloser;
    }
    
    private JPanel buildButtonView(){
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.add(new JButton(applyAction));
        p.add(new JButton(revertAction));
        p.add(new JButton(okAction));
        p.add(new JButton(cancelAction));
        return p;
    }

    private class OkAction extends AbstractAction{
        { // ewww - figure out resource injection
            putValue(AbstractAction.NAME, "OK");
        }
        public void actionPerformed(ActionEvent e) {
            if (viewCloser.doClose()){
                applyAction.actionPerformed(e);
            }
        }
        
    }
    
    private class CancelAction extends AbstractAction{
        { // ewww - figure out resource injection
            putValue(AbstractAction.NAME, "Cancel");
        }
        public void actionPerformed(ActionEvent e) {
            if (viewCloser.doClose()){
                cancelled = true;
                revertAction.actionPerformed(e);
            }
        }
    }
    
    private class RevertAction extends AbstractAction{
        { // ewww - figure out resource injection
            putValue(AbstractAction.NAME, "Revert");
        }
        public void actionPerformed(ActionEvent e) {
            externalRevertAction.actionPerformed(e);
        }
    }

    private class ApplyAction extends AbstractAction{
        { // ewww - figure out resource injection
            putValue(AbstractAction.NAME, "Apply");
        }
        public void actionPerformed(ActionEvent e) {
            externalApplyAction.actionPerformed(e);
        }
    }

    
    private static class ActionBinder implements PropertyChangeListener{
        private final Action source;
        private final Action target;
        public ActionBinder(Action source, Action target){
            this.source = source;
            this.target = target;
            sync();
            source.addPropertyChangeListener(this);
        }
        
        private void sync(){
            target.setEnabled(source.isEnabled());
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if ("enabled".equals(evt.getPropertyName())){
                sync();
            }
            
        }
        
        public void unbind(){
            source.removePropertyChangeListener(this);
        }
    }
    
    private static class NoOpViewCloser implements ViewCloser{
        public boolean doClose() {
            // do nothing
            return false;
        }
    }
    
    private static class NoOpAction extends AbstractAction{

        public void actionPerformed(ActionEvent e) {
            // do nothing
        }
        
    }
}
