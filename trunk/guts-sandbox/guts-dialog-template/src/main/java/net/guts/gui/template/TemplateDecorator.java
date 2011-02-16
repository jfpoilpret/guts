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

package net.guts.gui.template;

import java.awt.Container;

import javax.swing.RootPaneContainer;

import net.guts.gui.window.RootPaneConfig;

import com.google.inject.TypeLiteral;

/**
 * This interface is the core of the Guts-GUI "templating system". It allows 
 * decoration of views to be displayed (by 
 * {@link net.guts.gui.window.WindowController#show}) into a {@link RootPaneContainer}.
 * <p/>
 * A {@code TemplateDecorator} instance is called early in the process of displaying
 * a {@code RootPaneContainer}, thanks to a specific 
 * {@link net.guts.gui.window.WindowProcessor}.
 * <p/>
 * Implementations can use the passed {@code config} to determine how to decorate
 * a view before it is added to {@link RootPaneContainer}.
 * <p/>
 * For concrete implementation examples, you can check the source code of existing
 * Guts-GUI implementations of configuration objects and their associated 
 * {@code TemplateDecorator} implementations:
 * <ul>
 * <li>{@link net.guts.gui.template.okcancel.OkCancel}</li>
 * <li>{@link net.guts.gui.template.wizard.Wizard}</li>
 * </ul>
 * 
 * @author jfpoilpret
 */
public interface TemplateDecorator
{
	/**
	 * This key name must be used by {@link net.guts.gui.window.AbstractConfig} subclass to be
	 * associated with your own implementation of {@code TemplateDecorator}; in the
	 * {@code AbstractConfig} subclass, you need to have the following code:
	 * <pre>
	 *     set(TemplateDecorator.TEMPLATE_TYPE_KEY, MyTemplateDecorator.class);
	 * </pre>
	 * This instructs Guts-GUI templating system to create an instance of 
	 * {@code MyTemplateDecorator} (by Guice, so it can use {@code inject}) and use it
	 * to decorate the {@link RootPaneContainer#getContentPane() contentPane} of the
	 * {@code RootPaneContainer} passed to {@link net.guts.gui.window.WindowController#show}.
	 */
	static final public TypeLiteral<Class<? extends TemplateDecorator>> TEMPLATE_TYPE_KEY =
		new TypeLiteral<Class<? extends TemplateDecorator>>() {};

	/**
	 * Automatically called by {@link net.guts.gui.window.WindowController#show}, before 
	 * resources are injected and session restored, this method can decorate {@code view} 
	 * in any way it likes; it can even embed {@code view} in a new 
	 * {@link javax.swing.JPanel} and make it the new 
	 * {@link RootPaneContainer#setContentPane contentPane} of {@code container}.
	 * <p/>
	 * Decoration may use {@code config} to drive its behavior.
	 * 
	 * @param <T> the type of {@code container} (can be any of {@code JDialog},
	 * {@code JFrame}, {@code JInternalFrame}, {@code JApplet} or {@code JWindow})
	 * @param container the {@link RootPaneContainer} of which {@code view} is the
	 * current {@code contentPane} 
	 * @param view the view inside {@code container}
	 * @param config the config that was passed to 
	 * {@link net.guts.gui.window.WindowController#show} and extracted from it
	 * (with {@link net.guts.gui.window.AbstractConfig#config()})
	 */
	public <T extends RootPaneContainer> void decorate(
		T container, Container view, RootPaneConfig<T> config);
}
