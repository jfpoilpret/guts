/*
 * Created on Aug 16, 2010
 * (c) 2010 Trumpet, Inc.
 *
 */
package net.guts.gui.examples.addressbook.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.guts.gui.action.GutsAction;
import net.guts.gui.dialog.Closable;
import net.guts.gui.dialog.support.AbstractPreferencesPanel;
import net.guts.gui.dialogalt.ViewContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author kevin
 */
public class PreferencesPanel extends AbstractPreferencesPanel {
    static final private Logger log = LoggerFactory.getLogger(PreferencesPanel.class);
    
    private final JCheckBox check;

    @Inject
    public PreferencesPanel() {
        check = createBoundCheckboxBehavior("check1");
        syncEnabled();

        register(check, apply.action(), null);
        register(namedComponent("test1"), null, null);
        register(namedComponent("test2"), null, null);
    }

    private JComponent namedComponent(String name){
        JButton b = new JButton(name);
        b.setName(name);
        return b;
    }
    
    private JCheckBox createBoundCheckboxBehavior(String name){
        JCheckBox check = new JCheckBox();
        check.setName(name);
        check.setText("Allow OK");
        check.setSelected(true);
        check.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                syncEnabled();
            }
            
        });
        return check;
    }
    
    private void syncEnabled(){
        apply.action().setEnabled(check.isSelected());
    }
    
    
    
    private final GutsAction apply = new GutsAction(){

        protected void perform() {
            log.debug("Apply");
        }
        
    };
}
