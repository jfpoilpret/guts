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

package net.guts.event;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.createMock;
import static org.fest.assertions.Assertions.assertThat;

import net.guts.event.Channel;
import net.guts.event.Consumes;
import net.guts.event.EventModule;
import net.guts.event.Events;
import net.guts.event.Filters;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

@Test(groups = "itest")
public class RealUseCasesTest
{
	public void checkInjectPublishFilterConsume()
	{
		final ConsumerExceptionHandler handler = createMock(ConsumerExceptionHandler.class);
		// Create Guice injector
		Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
		{
			@Override protected void configure()
			{
				bind(ConsumerExceptionHandler.class).toInstance(handler);
				Events.bindChannel(binder(), new TypeLiteral<List<Integer>>(){});
			}
		});
		EasyMock.replay(handler);
		// Get Consumer & Suppliers
		Consumer1 consumer1 = injector.getInstance(Consumer1.class);
		Consumer2 consumer2 = injector.getInstance(Consumer2.class);
		Supplier1 supplier = injector.getInstance(Supplier1.class);
		// Check that initial list is null
		assertThat(consumer1.selection()).as("consumer1 initial selection").isNull();
		assertThat(consumer2.selection()).as("consumer2 initial selection").isNull();
		// Send several events and check they are received
		supplier.generate(1, 2, 3);
		assertThat(consumer1.selection()).as("consumer1 pushed selection(1, 2, 3)").containsSequence(1, 2, 3);
		assertThat(consumer2.selection()).as("consumer2 pushed selection(1, 2, 3)").containsSequence(1, 2, 3);
		supplier.generate(4, 5);
		assertThat(consumer1.selection()).as("consumer1 pushed selection(4, 5)").containsSequence(4, 5);
		assertThat(consumer2.selection()).as("consumer2 pushed selection(4, 5)").containsSequence(4, 5);
		supplier.generate();
		assertThat(consumer1.selection()).as("consumer1 filtered selection() => retained selection(4, 5)").containsSequence(4, 5);
		assertThat(consumer2.selection()).as("consumer2 pushed selection()").isEmpty();
		supplier.generate(9);
		assertThat(consumer1.selection()).as("consumer1 pushed selection(9)").containsSequence(9);
		assertThat(consumer2.selection()).as("consumer2 pushed selection(9)").containsSequence(9);
		EasyMock.verify(handler);
	}

	static public class Consumer1
	{
		@Consumes public void push(List<Integer> selection)
		{
			_selection = selection;
		}
		
		@Filters public boolean filter(List<Integer> selection)
		{
			return !selection.isEmpty();
		}
		
		public List<Integer> selection()
		{
			return _selection;
		}
		
		private List<Integer> _selection;
	}
	
	static public class Consumer2
	{
		@Consumes public void push(List<Integer> selection)
		{
			_selection = selection;
		}
		
		public List<Integer> selection()
		{
			return _selection;
		}
		
		private List<Integer> _selection;
	}
	
	static public class Supplier1
	{
		@Inject public Supplier1(Channel<List<Integer>> channel)
		{
			_channel = channel;
		}
		
		public void generate(Integer... numbers)
		{
			_channel.publish(Arrays.asList(numbers));
		}
		
		final private Channel<List<Integer>> _channel;
	}
}
