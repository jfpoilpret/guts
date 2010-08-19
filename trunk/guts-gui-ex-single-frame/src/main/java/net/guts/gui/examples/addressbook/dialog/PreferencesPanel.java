/*
 * Created on Aug 16, 2010
 * (c) 2010 Trumpet, Inc.
 *
 */
package net.guts.gui.examples.addressbook.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import net.guts.gui.action.GutsAction;
import net.guts.gui.dialog.support.AbstractPreferencesPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author kevin
 */
public class PreferencesPanel extends AbstractPreferencesPanel {
    static final private Logger log = LoggerFactory.getLogger(PreferencesPanel.class);
    
    private final ApplyControllingCheckbox _check1 = new ApplyControllingCheckbox();
    private final ApplyControllingCheckbox _check2 = new ApplyControllingCheckbox();
    private final ApplyControllingCheckbox _check3 = new ApplyControllingCheckbox();

    @Inject
    public PreferencesPanel() {

        
        register("Options 1", _check1, _check1.getApplyAction().action(), null);
        register("Options 2", _check2, _check2.getApplyAction().action(), null);
        register("Options 3", _check3, _check3.getApplyAction().action(), null);
        register("Button option 1", namedComponent("test1"), null, null);
        register("Button option 2", namedComponent("test2"), null, null);
    }

    private JComponent namedComponent(String name){
        JButton b = new JButton(name);
        b.setName(name);
        return b;
    }
    
    private static class ApplyControllingCheckbox extends JCheckBox{
        public ApplyControllingCheckbox() {
            addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent e) {
                    syncApplyActionEnabled();
                }
                
            });
            syncApplyActionEnabled();
        }
        
        private void syncApplyActionEnabled(){
            apply.action().setEnabled(isSelected());
        }
        
        public GutsAction getApplyAction(){
            return apply;
        }
        
        private final GutsAction apply = new GutsAction(){

            protected void perform() {
                log.debug("Apply - " + getName());
            }
            
        };
    }
}
