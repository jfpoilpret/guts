/*
 * Created on Aug 17, 2010
 * (c) 2010 Trumpet, Inc.
 *
 */
package net.guts.gui.dialogalt;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.google.inject.ImplementedBy;

/**
 * @author kevin
 */
@ImplementedBy(DialogTemplateImpl.class)
public abstract class DialogTemplate extends JPanel{

    abstract public boolean wasCancelled();

    abstract public void setApplyAction(Action applyAction);

    abstract public void setRevertAction(Action revertAction);

    abstract public void setView(JComponent view);

    abstract public void setViewCloser(ViewCloser viewCloser);

}