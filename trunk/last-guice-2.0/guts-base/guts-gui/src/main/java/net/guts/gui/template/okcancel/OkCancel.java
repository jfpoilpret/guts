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

package net.guts.gui.template.okcancel;

import javax.swing.Action;
import javax.swing.RootPaneContainer;

import net.guts.gui.template.TemplateDecorator;
import net.guts.gui.window.AbstractConfig;

/**
 * Configuration object to decorate a view ({@link javax.swing.JComponent}) with
 * "OK", "Cancel", "Apply" buttons. This object must then be 
 * {@link AbstractConfig#merge(AbstractConfig) merged} to one of
 * {@link net.guts.gui.window.JDialogConfig}, {@link net.guts.gui.window.JFrameConfig},
 * {@link net.guts.gui.window.JInternalFrameConfig} or 
 * {@link net.guts.gui.window.JAppletConfig} before passing the latter to
 * {@link net.guts.gui.window.WindowController#show}.
 * <p/>
 * {@code OkCancel} uses a fluent API to set configuration on how exactly to decorate
 * a view, e.g. what buttons to add, what {@link Action}s to set on each button...
 * <p/>
 * Check <a href="net/guts/gui/template/okcancel/package-summary.html">package
 * documentation</a> for a code example.
 * 
 * @author jfpoilpret
 */
public final class OkCancel extends AbstractConfig<RootPaneContainer, OkCancel>
{
	private OkCancel()
	{
		set(TemplateDecorator.TEMPLATE_TYPE_KEY, OkCancelDecorator.class);
		set(OkCancelConfig.class, _config);
	}

	/**
	 * Create a new {@code OkCancel} default configuration object. Default configuration
	 * won't add any button to the view it is used with.
	 * 
	 * @return a new {@code OkCancel} configuration object
	 */
	static public OkCancel create()
	{
		return new OkCancel();
	}

	/**
	 * Sets the view decorated through {@code this} configuration object to be added
	 * an "OK" button that will:
	 * <ul>
	 * <li>call the {@code apply} action</li>
	 * <li>close the {@link RootPaneContainer} containing the view</li>
	 * <li>set {@link #result()} to {@link Result#OK}</li>
	 * </ul>
	 * 
	 * @param apply the {@link Action} to be called by the "OK" button; should not be 
	 * {@code null}, otherwise, no "OK" button will be added to the decorated view.
	 * @return {@code this} builder so that calls can be chained until {@link #config()}
	 */
	public OkCancel withOK(Action apply)
	{
		_config._apply = apply;
		_config._hasOK = true;
		return this;
	}
	
	/**
	 * Sets the view decorated through {@code this} configuration object to be added
	 * a "Cancel" button that will:
	 * <ul>
	 * <li>call the {@code cancel} action</li>
	 * <li>close the {@link RootPaneContainer} containing the view</li>
	 * <li>set {@link #result()} to {@link Result#CANCEL}</li>
	 * </ul>
	 * 
	 * @param cancel the {@link Action} to be called by the "Cancel" button; if
	 * {@code null}, no action, other than closing the {@code RootPaneContainer},
	 * will happen when clicking "Cancel".
	 * @return {@code this} builder so that calls can be chained until {@link #config()}
	 */
	public OkCancel withCancel(Action cancel)
	{
		_config._cancel =  cancel;
		_config._hasCancel = true;
		return this;
	}

	/**
	 * Sets the view decorated through {@code this} configuration object to be added
	 * a "Cancel" button that will:
	 * <ul>
	 * <li>call the {@code cancel} action</li>
	 * <li>set {@link #result()} to {@link Result#CANCEL}</li>
	 * </ul>
	 * 
	 * @return {@code this} builder so that calls can be chained until {@link #config()}
	 */
	public OkCancel withCancel()
	{
		return withCancel(null);
	}

	/**
	 * Sets the view decorated through {@code this} configuration object to be added
	 * an "Apply" button that will call the {@code apply} action (that must have been 
	 * added with {@link #withOK}).
	 * 
	 * @return {@code this} builder so that calls can be chained until {@link #config()}
	 */
	public OkCancel withApply()
	{
		_config._hasApply = true;
		return this;
	}

	/**
	 * Tells the view decorator to not change the view in {@code RootPaneContainer}'s 
	 * {@link RootPaneContainer#getContentPane() contentPane}, but rather embed it in a 
	 * new {@link java.awt.Container} that will contain all buttons.
	 * <p/>
	 * In this case, no specific {@link OkCancelLayoutAdder} will be used even if one
	 * was bound for the {@link java.awt.LayoutManager} used by the decorated view.
	 * 
	 * @return {@code this} builder so that calls can be chained until {@link #config()}
	 */
	public OkCancel dontChangeView()
	{
		_config._dontChangeView = true;
		return this;
	}

	/**
	 * The kind of button that was used to close a view decorated by {@code this}.
	 * This is returned by {@link OkCancel#result()} after the view has been displayed
	 * then closed.
	 */
	static public enum Result
	{
		/**
		 * The view was closed by clicking the "OK" button.
		 */
		OK,
		
		/**
		 * The view was closed by clicking the "Cancel" button.
		 */
		CANCEL
	}

	/**
	 * Tells the button by which the view, decorated by {@code this}, was closed.
	 * 
	 * @return {@link Result#OK} if the view was closed by the "OK" button, 
	 * {@link Result#CANCEL} if the view was closed by the "Cancel" button, or
	 * {@code null} if the view is not closed yet or if it was closed directly by
	 * its close box.
	 */
	public Result result()
	{
		return _config._result;
	}
	
	final private OkCancelConfig _config = new OkCancelConfig();
}
