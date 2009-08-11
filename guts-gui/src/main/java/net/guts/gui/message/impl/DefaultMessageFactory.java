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

package net.guts.gui.message.impl;

import java.awt.Window;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceConverter;
import org.jdesktop.application.ResourceMap;

import net.guts.gui.application.ActiveWindowHolder;
import net.guts.gui.message.MessageFactory;
import net.guts.gui.message.MessageType;
import net.guts.gui.message.OptionType;
import net.guts.gui.message.UserChoice;
import net.guts.gui.util.EnumConverter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Default implementation of {@link MessageFactory} service.
 * <p/>
 * Normally, there is no need to override it or change it for another
 * implementation.
 * 
 * @author Jean-Francois Poilpret
 */
@Singleton
public class DefaultMessageFactory implements MessageFactory
{
	@Inject public DefaultMessageFactory(Logger logger, 
		ActiveWindowHolder activeWindow, ApplicationContext context)
	{
		_logger = logger;
		_activeWindow = activeWindow;
		_map = context.getResourceMap(getClass());
		ResourceConverter.register(new EnumConverter<MessageType>(MessageType.class));
		ResourceConverter.register(new EnumConverter<OptionType>(OptionType.class));
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.message.MessageFactory#showMessage(java.lang.String, java.lang.Object[])
	 */
	public UserChoice showMessage(String id, Object... args)
	{
		try
		{
			_map.injectFields(this, id);
			String title = String.format(_title, args);
			String message = String.format(_message, args);
			
			JOptionPane pane = new JOptionPane(	message,
												_messageType.value(),
												_optionType.value());
			pane.setName("message-" + id);
			Window parent = _activeWindow.getActiveWindow();
			JDialog box;
			if (parent instanceof JFrame)
			{
				box = pane.createDialog(parent, title);
			}
			else if (parent instanceof JDialog)
			{
				box = pane.createDialog(parent, title);
			}
			else
			{
				box = pane.createDialog((JFrame) null, title);
			}
			box.setLocationRelativeTo(parent);
			box.setVisible(true);
			Integer result = (Integer) pane.getValue();
			if (result == null)
			{
				return UserChoice.CANCEL;
			}
			else
			{
				return UserChoice.get(result.intValue());
			}
		}
		catch (ResourceMap.InjectFieldException e)
		{
			_logger.log(Level.SEVERE, "message() unexisting id <" + id + ">", e);
			return null;
		}
	}

	final private Logger _logger;
	final private ActiveWindowHolder _activeWindow;
	final private ResourceMap _map;
	@Resource(key = "messageType") private MessageType _messageType;
	@Resource(key = "optionType") private OptionType _optionType;
	@Resource(key = "title") private String _title;
	@Resource(key = "message") private String _message;
}
