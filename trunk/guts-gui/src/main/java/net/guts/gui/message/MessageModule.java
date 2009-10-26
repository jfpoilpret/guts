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

import net.guts.gui.resource.EnumConverter;
import net.guts.gui.resource.Resources;

import com.google.inject.AbstractModule;

public final class MessageModule extends AbstractModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		//TODO add explicit binding for MessageFactory (rather than implicit!)
		
		// Bind special ResourceConverter used by MessageFactoryImpl
		Resources.bindConverter(binder(), MessageType.class)
			.toInstance(new EnumConverter<MessageType>(MessageType.class));
		Resources.bindConverter(binder(), OptionType.class)
			.toInstance(new EnumConverter<OptionType>(OptionType.class));
	}
}
