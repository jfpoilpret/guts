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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

import net.guts.event.Consumes;
import net.guts.event.Filters;
import net.guts.event.internal.AnnotationProcessor;
import net.guts.event.internal.ChannelKey;
import net.guts.event.internal.ConsumerFilter;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

@Test(groups = "utest")
public class AnnotationProcessorTest
{
	@BeforeMethod public void setup()
	{
		Set<ChannelKey> channels = new HashSet<ChannelKey>();
		channels.add(key(Type1.class));
		channels.add(key(Number.class));
		channels.add(key(Integer.class));
		channels.add(key(int.class));
		channels.add(key(TypeLiteral.get(int[].class)));
		channels.add(key(Integer.class, TOPIC));
		channels.add(key(Boolean.class));
		channels.add(key(new TypeLiteral<List<Integer>>(){}));
		channels.add(key(new TypeLiteral<List<String>>(){}));
		Map<Class<? extends Annotation>, Provider<Executor>> executors =
			new HashMap<Class<? extends Annotation>, Provider<Executor>>();
		_processor = new AnnotationProcessor(channels, executors);
	}

	public void checkOneConsumesInteger()
	{
		List<ConsumerFilter> events = _processor.process(Consumer1.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(Integer.class), 0, "push", null);
	}
	static public class Consumer1
	{
		@Consumes public void push(Integer event) {}
	}

	public void checkTwoConsumesList()
	{
		List<ConsumerFilter> events = _processor.process(Consumer2.class);
		assertThat(events).as("events").hasSize(2);
		checkEvent(events, 0, key(new TypeLiteral<List<Integer>>(){}), 0, "push1", null);
		checkEvent(events, 1, key(new TypeLiteral<List<String>>(){}), 0, "push2", null);
	}
	static public class Consumer2
	{
		@Consumes public void push1(List<Integer> event) {}
		@Consumes public void push2(List<String> event) {}
	}

	public void checkConsumesFiltersListInteger()
	{
		List<ConsumerFilter> events = _processor.process(Consumer3.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(new TypeLiteral<List<Integer>>(){}), 0, "push", "filter");
	}
	static public class Consumer3
	{
		@Consumes public void push(List<Integer> event) {}
		@Filters public boolean filter(List<Integer> event) {return true;}
	}

	public void checkConsumesListIntegerFiltersListString()
	{
		List<ConsumerFilter> events = _processor.process(Consumer4.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(new TypeLiteral<List<Integer>>(){}), 0, "push", null);
	}
	static public class Consumer4
	{
		@Consumes public void push(List<Integer> event) {}
		@Filters public boolean filter(List<String> event) {return true;}
	}

	public void checkOneConsumesTwoMatchingFilters()
	{
		List<ConsumerFilter> events = _processor.process(Consumer5.class);
		assertThat(events).as("events").hasSize(2);
		checkEvent(events, 0, key(Integer.class), 0, "push", "filter1");
		checkEvent(events, 1, key(Integer.class), 0, "push", "filter2");
	}
	static public class Consumer5
	{
		@Consumes public void push(Integer event) {}
		@Filters public boolean filter1(Integer event) {return true;}
		@Filters public boolean filter2(Integer event) {return true;}
	}
	
	public void checkConsumesWithTopic()
	{
		List<ConsumerFilter> events = _processor.process(Consumer6.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(Integer.class, TOPIC), 10, "push", null);
	}
	static public class Consumer6
	{
		@Consumes(topic = TOPIC, priority = 10) 
		public void push(Integer event) {}
	}
	
	public void checkTwoConsumesWithWithoutTopic()
	{
		List<ConsumerFilter> events = _processor.process(Consumer7.class);
		assertThat(events).as("events").hasSize(2);
		checkEvent(events, 0, key(Integer.class, TOPIC), 0, "push1", null);
		checkEvent(events, 1, key(Integer.class), 0, "push2", null);
	}
	static public class Consumer7
	{
		@Consumes(topic = TOPIC) public void push1(Integer event) {}
		@Consumes public void push2(Integer event) {}
	}
	
	public void checkConsumesFiltersWithTopic()
	{
		List<ConsumerFilter> events = _processor.process(Consumer8.class);
		assertThat(events).as("events").hasSize(2);
		checkEvent(events, 0, key(Integer.class, TOPIC), 0, "push1", "filter1");
		checkEvent(events, 1, key(Integer.class), 0, "push2", null);
	}
	static public class Consumer8
	{
		@Consumes(topic = TOPIC) public void push1(Integer event) {}
		@Filters(topic = TOPIC) public boolean filter1(Integer event) {return true;}
		@Consumes public void push2(Integer event) {}
	}
	
	public void checkConsumesTopicFiltersNoTopic()
	{
		List<ConsumerFilter> events = _processor.process(Consumer9.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(Integer.class, TOPIC), 0, "push", null);
	}
	static public class Consumer9
	{
		@Consumes(topic = TOPIC) public void push(Integer event) {}
		@Filters public boolean filter(Integer event) {return true;}
	}
	
	public void checkConsumesNoTopicFiltersTopic()
	{
		List<ConsumerFilter> events = _processor.process(Consumer10.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(Integer.class), 0, "push", null);
	}
	static public class Consumer10
	{
		@Consumes public void push(Integer event) {}
		@Filters(topic = TOPIC) public boolean filter(Integer event) {return true;}
	}
	
	public void checkInheritedConsumesNoTopicFiltersTopic()
	{
		List<ConsumerFilter> events = _processor.process(Consumer11.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(Integer.class), 0, "push", null);
	}
	static public class Consumer11 extends Consumer10
	{
	}
	
	public void checkOneConsumesInt()
	{
		List<ConsumerFilter> events = _processor.process(Consumer12.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(int.class), 0, "push", null);
	}
	static public class Consumer12
	{
		@Consumes public void push(int event) {}
	}

	public void checkOneConsumesIntFiltersInt()
	{
		List<ConsumerFilter> events = _processor.process(Consumer13.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(int.class), 0, "push", "filters");
	}
	static public class Consumer13
	{
		@Consumes public void push(int event) {}
		@Filters public boolean filters(int event) {return true;}
	}

	public void checkOneConsumesIntArray()
	{
		List<ConsumerFilter> events = _processor.process(Consumer14.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(TypeLiteral.get(int[].class)), 0, "push", null);
	}
	static public class Consumer14
	{
		@Consumes public void push(int[] event) {}
	}

	public void checkConsumesWithTypeSameAsArgType()
	{
		List<ConsumerFilter> events = _processor.process(Consumer15.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(Integer.class), 0, "push", null);
	}
	static public class Consumer15
	{
		@Consumes(type = Integer.class) public void push(Integer event) {}
	}
	
	public void checkConsumesWithTypeSameAsArgGenericType()
	{
		List<ConsumerFilter> events = _processor.process(Consumer16.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(new TypeLiteral<List<Integer>>(){}), 0, "push", null);
	}
	static public class Consumer16
	{
		@Consumes(type = List.class) public void push(List<Integer> event) {}
	}
	
	public void checkConsumesWithNumberTypeAndIntegerArgument()
	{
		List<ConsumerFilter> events = _processor.process(Consumer17.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(Number.class), 0, "push", null);
	}
	static public class Consumer17
	{
		@Consumes(type = Number.class) public void push(Integer event) {}
	}
	
	public void checkConsumesWithListTypeAndArrayListArgument()
	{
		List<ConsumerFilter> events = _processor.process(Consumer18.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(new TypeLiteral<List<Integer>>(){}), 0, "push", null);
	}
	static public class Consumer18
	{
		@Consumes(type = List.class) public void push(ArrayList<Integer> event) {}
	}
	
	public void checkConsumesWithListTypeAndMyListArgument()
	{
		List<ConsumerFilter> events = _processor.process(Consumer19.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(new TypeLiteral<List<Integer>>(){}), 0, "push", null);
	}
	static public class Consumer19
	{
		@Consumes(type = List.class) public void push(MyList event) {}
	}
	static public interface MyList extends List<Integer> {}
	
	public void checkConsumesWithType1AndType2Argument()
	{
		List<ConsumerFilter> events = _processor.process(Consumer20.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(Type1.class), 0, "push", null);
	}
	static public class Consumer20
	{
		@Consumes(type = Type1.class) public void push(Type2<Integer> event) {}
	}
	static public interface Type1 {}
	static public interface Type2<T> extends Type1 {}
	
	public void checkConsumesFiltersWithNumberTypeAndIntegerArgument()
	{
		List<ConsumerFilter> events = _processor.process(Consumer21.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(Number.class), 0, "push", "filters");
	}
	static public class Consumer21
	{
		@Consumes(type = Number.class) public void push(Integer event) {}
		@Filters(type = Number.class) public boolean filters(Integer event) {return true;}
	}
	
	public void checkNamedFilterWithMatchingNamedConsumer()
	{
		List<ConsumerFilter> events = _processor.process(Consumer22.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(Integer.class), 0, "push", "filters");
	}
	static public class Consumer22
	{
		@Consumes(filterId = ID1) public void push(Integer event) {}
		@Filters(id = ID1) public boolean filters(Integer event) {return true;}
	}

	public void checkFilterWithNoMatchingNamedConsumer()
	{
		List<ConsumerFilter> events = _processor.process(Consumer23.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(Integer.class), 0, "push", null);
	}
	static public class Consumer23
	{
		@Consumes(filterId = ID1) public void push(Integer event) {}
		@Filters public boolean filters(Integer event) {return true;}
	}

	public void checkNamedFilterWithNoMatchingConsumer()
	{
		List<ConsumerFilter> events = _processor.process(Consumer24.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(Integer.class), 0, "push", null);
	}
	static public class Consumer24
	{
		@Consumes public void push(Integer event) {}
		@Filters(id = ID1) public boolean filters(Integer event) {return true;}
	}

	public void checkTwoFiltersWithTwoMatchingConsumers()
	{
		List<ConsumerFilter> events = _processor.process(Consumer25.class);
		assertThat(events).as("events").hasSize(2);
		checkEvent(events, 0, key(Integer.class), 0, "push1", "filters1");
		checkEvent(events, 1, key(Integer.class), 0, "push2", "filters2");
	}
	static public class Consumer25
	{
		@Consumes public void push1(Integer event) {}
		@Filters public boolean filters1(Integer event) {return true;}
		@Consumes(filterId = ID1) public void push2(Integer event) {}
		@Filters(id = ID1) public boolean filters2(Integer event) {return true;}
	}

	public void checkOneNamedFilterWithTwoMatchingConsumers()
	{
		List<ConsumerFilter> events = _processor.process(Consumer26.class);
		assertThat(events).as("events").hasSize(2);
		checkEvent(events, 0, key(Integer.class), 0, "push1", "filters");
		checkEvent(events, 1, key(Integer.class), 0, "push2", "filters");
	}
	static public class Consumer26
	{
		@Consumes(filterId = ID1) public void push1(Integer event) {}
		@Consumes(filterId = ID1) public void push2(Integer event) {}
		@Filters(id = ID1) public boolean filters(Integer event) {return true;}
	}

	public void checkTwoNamedFiltersWithTwoMatchingConsumers()
	{
		List<ConsumerFilter> events = _processor.process(Consumer27.class);
		assertThat(events).as("events").hasSize(2);
		checkEvent(events, 0, key(Integer.class), 0, "push1", "filters1");
		checkEvent(events, 1, key(Integer.class), 0, "push2", "filters2");
	}
	static public class Consumer27
	{
		@Consumes(filterId = ID2) public void push1(Integer event) {}
		@Filters(id = ID2) public boolean filters1(Integer event) {return true;}
		@Consumes(filterId = ID1) public void push2(Integer event) {}
		@Filters(id = ID1) public boolean filters2(Integer event) {return true;}
	}

	public void checkConsumesReturnInt()
	{
		List<ConsumerFilter> events = _processor.process(Consumer28.class);
		assertThat(events).as("events").hasSize(1);
		checkEvent(events, 0, key(Boolean.class), 0, "push", null);
	}
	static public class Consumer28
	{
		@Consumes public int push(Boolean event) {return 0;}
	}

	public void checkIllegalConsumesNoArgument()
	{
		List<ConsumerFilter> events = _processor.process(ErrorConsumer2.class);
		assertThat(events).as("events").hasSize(0);
	}
	static public class ErrorConsumer2
	{
		@Consumes public void push() {}
	}

	public void checkIllegalConsumesTwoArguments()
	{
		List<ConsumerFilter> events = _processor.process(ErrorConsumer3.class);
		assertThat(events).as("events").hasSize(0);
	}
	static public class ErrorConsumer3
	{
		@Consumes public void push(Integer a, Integer b) {}
	}
	
	public void checkIllegalFiltersNoArgument()
	{
		List<ConsumerFilter> events = _processor.process(ErrorConsumer4.class);
		assertThat(events).as("events").hasSize(1);
		assertThat(events.get(0).getFilter()).as("events[0].filter").isNull();
	}
	static public class ErrorConsumer4
	{
		@Consumes public void push(Integer event) {}
		@Filters public boolean filter() {return true;}
	}

	public void checkIllegalFiltersNoReturn()
	{
		List<ConsumerFilter> events = _processor.process(ErrorConsumer5.class);
		assertThat(events).as("events").hasSize(1);
		assertThat(events.get(0).getFilter()).as("events[0].filter").isNull();
	}
	static public class ErrorConsumer5
	{
		@Consumes public void push(Integer event) {}
		@Filters public void filter(Integer event) {}
	}

	public void checkIllegalConsumesForUnregisteredTypeChannel()
	{
		List<ConsumerFilter> events = _processor.process(ErrorConsumer6.class);
		assertThat(events).as("events").hasSize(0);
	}
	static public class ErrorConsumer6
	{
		@Consumes public void push(Long event) {}
	}

	public void checkIllegalConsumesForUnregisteredTopicChannel()
	{
		List<ConsumerFilter> events = _processor.process(ErrorConsumer7.class);
		assertThat(events).as("events").hasSize(0);
	}
	static public class ErrorConsumer7
	{
		@Consumes(topic = BAD_TOPIC) public void push(Integer event) {}
	}

	public void checkIllegalFiltersReturnInt()
	{
		List<ConsumerFilter> events = _processor.process(ErrorConsumer8.class);
		assertThat(events).as("events").hasSize(1);
		assertThat(events.get(0).getFilter()).as("event[0].filter").isNull();
	}
	static public class ErrorConsumer8
	{
		@Consumes public void push(Integer event) {}
		@Filters public int filter(Integer event) {return 0;}
	}

	public void checkIllegalConsumesWithTypeIntegerAndLongArgument()
	{
		List<ConsumerFilter> events = _processor.process(ErrorConsumer9.class);
		assertThat(events).as("events").hasSize(0);
	}
	static public class ErrorConsumer9
	{
		@Consumes(type = Integer.class) public void push(Long event) {}
	}

	public void checkIllegalConsumesWithTypeIntegerAndNumberArgument()
	{
		List<ConsumerFilter> events = _processor.process(ErrorConsumer10.class);
		assertThat(events).as("events").hasSize(0);
	}
	static public class ErrorConsumer10
	{
		@Consumes(type = Integer.class) public void push(Number event) {}
	}

	public void checkIllegalFiltersWithTypeIntegerAndNumberArgument()
	{
		List<ConsumerFilter> events = _processor.process(ErrorConsumer11.class);
		assertThat(events).as("events").hasSize(1);
		assertThat(events.get(0).getFilter()).as("events[0].filter").isNull();
	}
	static public class ErrorConsumer11
	{
		@Consumes public void push(Number event) {}
		@Filters(type = Integer.class) public boolean filters(Number event) {return true;}
	}

	static private void checkEvent(List<ConsumerFilter> events, int index, ChannelKey key, 
		int priority, String consumer, String filter)
	{
		ConsumerFilter event = events.get(index);
		String label = "events[" + index + "]";
		assertThat(event).as(label).isNotNull();
		assertThat(event.getKey()).as(label + ".getKey()").isNotNull().isEqualTo(key);
		assertThat(event.getPriority()).as(label + ".getPriority()").isEqualTo(priority);
		assertThat(event.getConsumer()).as(label + ".getConsumer()").isNotNull();
		assertThat(event.getConsumer().getName()).as(label + ".getConsumer()").isEqualTo(consumer);
		if (filter == null)
		{
			assertThat(event.getFilter()).as(label + ".getFilter()").isNull();
		}
		else
		{
			assertThat(event.getFilter()).as(label + ".getFilter()").isNotNull();
			assertThat(event.getFilter().getName()).as(label + ".getFilter()").isEqualTo(filter);
		}
	}
		
	static private <T> ChannelKey key(Class<T> type)
	{
		return key(type, null);
	}
	
	static private <T> ChannelKey key(Class<T> type, String topic)
	{
		return new ChannelKey(type, topic);
	}
	
	static private <T> ChannelKey key(TypeLiteral<T> type)
	{
		return key(type, null);
	}
	
	static private <T> ChannelKey key(TypeLiteral<T> type, String topic)
	{
		return new ChannelKey(type.getType(), topic);
	}

	static private final String TOPIC = "dummy";
	static private final String BAD_TOPIC = "unexisting";
	static private final String ID1 = "FILTER1";
	static private final String ID2 = "FILTER2";
	private AnnotationProcessor _processor;
}
