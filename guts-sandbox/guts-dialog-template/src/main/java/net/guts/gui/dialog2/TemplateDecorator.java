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

package net.guts.gui.dialog2;

import javax.swing.JComponent;
import javax.swing.JDialog;

import net.guts.gui.window.RootPaneConfig;

import com.google.inject.TypeLiteral;

//TODO rename into Decorator? or ViewDecorator?
public interface TemplateDecorator
{
	static final public TypeLiteral<Class<? extends TemplateDecorator>> TEMPLATE_TYPE_KEY =
		new TypeLiteral<Class<? extends TemplateDecorator>>() {};

	//TODO should we genericize this method based on view class?
	// But the idea would be to genericize DialogConfiguration also!
	// How would that be possible?
	//TODO should we be more generic than just <?,?> ??
	public void decorate(JDialog container, JComponent view, RootPaneConfig<JDialog> config);
}
