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

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.guts.gui.application.WindowController;
import net.guts.gui.message.MessageFactory;
import net.guts.gui.message.MessageType;
import net.guts.gui.message.OptionType;
import net.guts.gui.message.UserChoice;
import net.guts.gui.resource.ResourceInjector;

import com.google.inject.Inject;
import com.google.inject.Singleton;

//TODO change package and make it package private!
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
	@Inject public DefaultMessageFactory(
		WindowController windowController, ResourceInjector injector)
	{
		_windowController = windowController;
		_injector = injector;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.message.MessageFactory#showMessage(java.lang.String, java.lang.Object[])
	 */
	public UserChoice showMessage(String id, Object... args)
	{
		// Inject specific information into private object
		MessageInfo info = new MessageInfo();
		_injector.injectInstance(info, id);
		String title = String.format(info.getTitle(), args);
		String message = String.format(info.getMessage(), args);
		
		JOptionPane pane = new JOptionPane(	message,
											info.getMessageType().value(),
											info.getOptionType().value());
		pane.setName("message-" + id);
		Window parent = _windowController.getActiveWindow();
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
	
	static final private class MessageInfo
	{
		public MessageType getMessageType()
		{
			return _messageType;
		}

		public void setMessageType(MessageType messageType)
		{
			_messageType = messageType;
		}
		
		public OptionType getOptionType()
		{
			return _optionType;
		}
		
		public void setOptionType(OptionType optionType)
		{
			_optionType = optionType;
		}
		
		public String getTitle()
		{
			return _title;
		}
		
		public void setTitle(String title)
		{
			_title = title;
		}
		
		public String getMessage()
		{
			return _message;
		}
		
		public void setMessage(String message)
		{
			_message = message;
		}
		
		private MessageType _messageType;
		private OptionType _optionType;
		private String _title;
		private String _message;
	}

	final private WindowController _windowController;
	final private ResourceInjector _injector;
}
