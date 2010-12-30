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
//CSOFF: AbstractClassName
@ImplementedBy(DialogTemplateImpl.class)
public abstract class DialogTemplate extends JPanel
{
    abstract public boolean wasCancelled();

    abstract public void setApplyAction(Action applyAction);

    abstract public void setRevertAction(Action revertAction);

    abstract public void setView(JComponent view);

    abstract public void setViewCloser(ViewCloser viewCloser);
}
//CSON: AbstractClassName
