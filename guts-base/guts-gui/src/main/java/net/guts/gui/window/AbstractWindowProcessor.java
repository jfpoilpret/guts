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

package net.guts.gui.window;

import javax.swing.RootPaneContainer;

/**
 * Abstract implementation of {@link WindowProcessor} that is useful for
 * {@code WindowProcessor}s that can support only a given subtype of
 * {@link RootPaneContainer}. For instance, when you can only support {@link java.awt.Window}
 * subclasses, you can define your processing class as follows:
 * <pre>
 * class MyAppletProcessor extends AbstractWindowProcessor&lt;JApplet, JApplet&gt;
 * {
 *     &#63;Override protected void processRoot(
 *         JApplet root, RootPaneConfig&lt;JApplet&gt; config)
 *     {
 *         ...
 *     }
 * }
 * </pre>
 * 
 * @param <U> The only type {@code this} {@link WindowProcessor} wants to deal with; this 
 * can be a {@link RootPaneContainer} subtype but not necessarily, eg it can be
 * {@link java.awt.Window}, meaning that {@code this} implementation deals with any
 * {@link RootPaneContainer} implementations that are subclasses of {@link java.awt.Window},
 * namely {@link javax.swing.JDialog}, {@link javax.swing.JFrame} and 
 * {@link javax.swing.JWindow}.
 * @param <V> A specific subtype of {@link javax.swing.RootPaneContainer} {@code this}
 * implementation wants to deal with; this can be {@link javax.swing.RootPaneContainer}
 * or any class implementing it.
 *
 * @author Jean-Francois Poilpret
 */
public abstract class AbstractWindowProcessor<U, V extends RootPaneContainer>
implements WindowProcessor
{
	protected AbstractWindowProcessor(Class<U> processedClass, Class<V> processedRoot)
	{
		_processedClass = processedClass;
		_processedRoot = processedRoot;
	}

	protected AbstractWindowProcessor(Class<U> processedClass)
	{
		_processedClass = processedClass;
		_processedRoot = RootPaneContainer.class;
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.window.WindowProcessor#process(javax.swing.RootPaneContainer, net.guts.gui.window.RootPaneConfig)
	 */
	@SuppressWarnings("unchecked") @Override 
	final public <T extends RootPaneContainer> void process(T root, RootPaneConfig<T> config)
	{
		if (_processedClass.isInstance(root) && _processedRoot.isInstance(root))
		{
			processRoot((U) root, (RootPaneConfig<V>) config);
		}
	}

	/**
	 * This method is called only when {@code root} is a subtype of both {@code U} and 
	 * {@code V}. It allows specific processing on {@code root} based on {@code config}.
	 * 
	 * @param root the window to process
	 * @param config the configuration for {@code root} according to its type {@code V}
	 */
	abstract protected void processRoot(U root, RootPaneConfig<V> config);

	final protected Class<U> _processedClass;
	final protected Class<? super V> _processedRoot;
}
