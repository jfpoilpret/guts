/*
 * Created on Aug 16, 2010
 * (c) 2010 Trumpet, Inc.
 *
 */
package net.guts.gui.examples.addressbook.dialog;

import javax.swing.JButton;
import javax.swing.JComponent;

import net.guts.gui.dialog.support.AbstractPreferencesPanel;

import com.google.inject.Inject;

/**
 * @author kevin
 */
public class PreferencesPanel extends AbstractPreferencesPanel {

    @Inject
    public PreferencesPanel() {
        register(namedComponent("test1"), null, null);
        register(namedComponent("test2"), null, null);
    }

    private JComponent namedComponent(String name){
        JButton b = new JButton(name);
        b.setName(name);
        return b;
    }
}
