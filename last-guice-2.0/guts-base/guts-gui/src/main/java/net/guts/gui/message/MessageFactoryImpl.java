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

package net.guts.gui.message;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.guts.gui.resource.ResourceInjector;
import net.guts.gui.window.ActiveWindow;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class MessageFactoryImpl implements MessageFactory
{
	@Inject MessageFactoryImpl(ActiveWindow activeWindow, ResourceInjector injector)
	{
		_activeWindow = activeWindow;
		_injector = injector;
	}
	
	@Override public UserChoice showMessage(String id, Object... args)
	{
		Window parent = _activeWindow.get();
		return showMessage(parent, id, args);
	}
	
	@Override public UserChoice showMessage(Component parent, String id, Object... args)
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
		JDialog box = pane.createDialog(parent, title);
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
	
	@SuppressWarnings("unused")
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

	final private ActiveWindow _activeWindow;
	final private ResourceInjector _injector;
}
