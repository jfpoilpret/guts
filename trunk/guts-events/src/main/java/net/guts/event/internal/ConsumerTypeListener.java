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

package net.guts.event.internal;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

// This class "listens" to Guice injectable types to detect types that include
// @Consumes annotations, for later automatic processing
public class ConsumerTypeListener implements TypeListener
{
	public ConsumerTypeListener(InjectionListener<Object> injectionListener)
	{
		_injectionListener = injectionListener;
	}
	
	public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter)
	{
		encounter.register(_injectionListener);
	}
	
	final private InjectionListener<Object> _injectionListener; 
}

