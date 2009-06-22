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
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.eq;
import static org.easymock.classextension.EasyMock.isA;
import static org.easymock.classextension.EasyMock.isNull;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.fest.assertions.Assertions.assertThat;

import net.guts.event.ConsumerClassError;
import net.guts.event.Consumes;
import net.guts.event.ErrorHandler;
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
		channels.add(key(Integer.class));
		channels.add(key(Integer.class, TOPIC));
		channels.add(key(Boolean.class));
		channels.add(key(new TypeLiteral<List<Integer>>(){}));
		channels.add(key(new TypeLiteral<List<String>>(){}));
		Map<Class<? extends Annotation>, Provider<Executor>> executors =
			new HashMap<Class<? extends Annotation>, Provider<Executor>>();
		_handler = createMock(ErrorHandler.class);
		_processor = new AnnotationProcessor(_handler, channels, executors);
	}

	public void checkOneConsumesInteger()
	{
		List<ConsumerFilter> events = _processor.process(Consumer1.class);

		assertThat(events).as("events").hasSize(1);
		assertThat(events.get(0)).as("events[0]").isNotNull();
		assertThat(events.get(0).getKey()).as("events[0].getKey()").isNotNull()
			.isEqualTo(key(Integer.class));
		assertThat(events.get(0).getFilter()).as("events[0].getFilter()").isNull();
		assertThat(events.get(0).getPriority()).as("events[0].getPriority()").isEqualTo(0);
		assertThat(events.get(0).getConsumer()).as("events[0].getConsumer()").isNotNull();
		assertThat(events.get(0).getConsumer().getName()).as("events[0].getConsumer()")
			.isEqualTo("push");
	}
	
	static public class Consumer1
	{
		@Consumes public void push(Integer event) {}
	}

	public void checkTwoConsumesList()
	{
		List<ConsumerFilter> events = _processor.process(Consumer2.class);

		assertThat(events).as("events").hasSize(2);
		assertThat(events.get(0)).as("events[0]").isNotNull();
		assertThat(events.get(0).getKey()).as("events[0].getKey()").isNotNull()
			.isEqualTo(key(new TypeLiteral<List<Integer>>(){}));
		assertThat(events.get(0).getFilter()).as("events[0].getFilter()").isNull();
		assertThat(events.get(0).getPriority()).as("events[0].getPriority()").isEqualTo(0);
		assertThat(events.get(0).getConsumer()).as("events[0].getConsumer()").isNotNull();
		assertThat(events.get(0).getConsumer().getName()).as("events[0].getConsumer()")
			.isEqualTo("push1");

		assertThat(events.get(1)).as("events[1]").isNotNull();
		assertThat(events.get(1).getKey()).as("events[1].getKey()").isNotNull()
			.isEqualTo(key(new TypeLiteral<List<String>>(){}));
		assertThat(events.get(1).getFilter()).as("events[1].getFilter()").isNull();
		assertThat(events.get(1).getPriority()).as("events[1].getPriority()").isEqualTo(0);
		assertThat(events.get(1).getConsumer()).as("events[1].getConsumer()").isNotNull();
		assertThat(events.get(1).getConsumer().getName()).as("events[1].getConsumer()")
			.isEqualTo("push2");
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
		assertThat(events.get(0)).as("events[0]").isNotNull();
		assertThat(events.get(0).getKey()).as("events[0].getKey()").isNotNull()
			.isEqualTo(key(new TypeLiteral<List<Integer>>(){}));
		assertThat(events.get(0).getFilter()).as("events[0].getFilter()").isNotNull();
		assertThat(events.get(0).getFilter().getName()).as("events[0].getFilter()")
			.isEqualTo("filter");
		assertThat(events.get(0).getPriority()).as("events[0].getPriority()").isEqualTo(0);
		assertThat(events.get(0).getConsumer()).as("events[0].getConsumer()").isNotNull();
		assertThat(events.get(0).getConsumer().getName()).as("events[0].getConsumer()")
			.isEqualTo("push");
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
		assertThat(events.get(0)).as("events[0]").isNotNull();
		assertThat(events.get(0).getKey()).as("events[0].getKey()").isNotNull()
			.isEqualTo(key(new TypeLiteral<List<Integer>>(){}));
		assertThat(events.get(0).getFilter()).as("events[0].getFilter()").isNull();
		assertThat(events.get(0).getPriority()).as("events[0].getPriority()").isEqualTo(0);
		assertThat(events.get(0).getConsumer()).as("events[0].getConsumer()").isNotNull();
		assertThat(events.get(0).getConsumer().getName()).as("events[0].getConsumer()")
			.isEqualTo("push");
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
		assertThat(events.get(0)).as("events[0]").isNotNull();
		assertThat(events.get(0).getKey()).as("events[0].getKey()").isNotNull()
			.isEqualTo(key(Integer.class));
		assertThat(events.get(0).getFilter()).as("events[0].getFilter()").isNotNull();
		assertThat(events.get(0).getFilter().getName()).as("events[0].getFilter()")
			.isEqualTo("filter1");
		assertThat(events.get(0).getPriority()).as("events[0].getPriority()").isEqualTo(0);
		assertThat(events.get(0).getConsumer()).as("events[0].getConsumer()").isNotNull();
		assertThat(events.get(0).getConsumer().getName()).as("events[0].getConsumer()")
			.isEqualTo("push");

		assertThat(events.get(1)).as("events[1]").isNotNull();
		assertThat(events.get(1).getKey()).as("events[1].getKey()").isNotNull()
			.isEqualTo(key(Integer.class));
		assertThat(events.get(1).getFilter()).as("events[1].getFilter()").isNotNull();
		assertThat(events.get(1).getFilter().getName()).as("events[1].getFilter()")
			.isEqualTo("filter2");
		assertThat(events.get(1).getPriority()).as("events[1].getPriority()").isEqualTo(0);
		assertThat(events.get(1).getConsumer()).as("events[1].getConsumer()").isNotNull();
		assertThat(events.get(1).getConsumer().getName()).as("events[1].getConsumer()")
			.isEqualTo("push");
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
		assertThat(events.get(0)).as("events[0]").isNotNull();
		assertThat(events.get(0).getKey()).as("events[0].getKey()").isNotNull()
			.isEqualTo(key(Integer.class, TOPIC));
		assertThat(events.get(0).getFilter()).as("events[0].getFilter()").isNull();
		assertThat(events.get(0).getPriority()).as("events[0].getPriority()").isEqualTo(10);
		assertThat(events.get(0).getConsumer()).as("events[0].getConsumer()").isNotNull();
		assertThat(events.get(0).getConsumer().getName()).as("events[0].getConsumer()")
			.isEqualTo("push");
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
		assertThat(events.get(0)).as("events[0]").isNotNull();
		assertThat(events.get(0).getKey()).as("events[0].getKey()").isNotNull()
			.isEqualTo(key(Integer.class, TOPIC));
		assertThat(events.get(0).getFilter()).as("events[0].getFilter()").isNull();
		assertThat(events.get(0).getPriority()).as("events[0].getPriority()").isEqualTo(0);
		assertThat(events.get(0).getConsumer()).as("events[0].getConsumer()").isNotNull();
		assertThat(events.get(0).getConsumer().getName()).as("events[0].getConsumer()")
			.isEqualTo("push1");

		assertThat(events.get(1)).as("events[1]").isNotNull();
		assertThat(events.get(1).getKey()).as("events[1].getKey()").isNotNull()
			.isEqualTo(key(Integer.class));
		assertThat(events.get(1).getFilter()).as("events[1].getFilter()").isNull();
		assertThat(events.get(1).getPriority()).as("events[1].getPriority()").isEqualTo(0);
		assertThat(events.get(1).getConsumer()).as("events[1].getConsumer()").isNotNull();
		assertThat(events.get(1).getConsumer().getName()).as("events[1].getConsumer()")
			.isEqualTo("push2");
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
		assertThat(events.get(0)).as("events[0]").isNotNull();
		assertThat(events.get(0).getKey()).as("events[0].getKey()").isNotNull()
			.isEqualTo(key(Integer.class, TOPIC));
		assertThat(events.get(0).getFilter()).as("events[0].getFilter()").isNotNull();
		assertThat(events.get(0).getFilter().getName()).as("events[0].getFilter()")
			.isEqualTo("filter1");
		assertThat(events.get(0).getPriority()).as("events[0].getPriority()").isEqualTo(0);
		assertThat(events.get(0).getConsumer()).as("events[0].getConsumer()").isNotNull();
		assertThat(events.get(0).getConsumer().getName()).as("events[0].getConsumer()")
			.isEqualTo("push1");

		assertThat(events.get(1)).as("events[1]").isNotNull();
		assertThat(events.get(1).getKey()).as("events[1].getKey()").isNotNull()
			.isEqualTo(key(Integer.class));
		assertThat(events.get(1).getFilter()).as("events[1].getFilter()").isNull();
		assertThat(events.get(1).getPriority()).as("events[1].getPriority()").isEqualTo(0);
		assertThat(events.get(1).getConsumer()).as("events[1].getConsumer()").isNotNull();
		assertThat(events.get(1).getConsumer().getName()).as("events[1].getConsumer()")
			.isEqualTo("push2");
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
		assertThat(events.get(0)).as("events[0]").isNotNull();
		assertThat(events.get(0).getKey()).as("events[0].getKey()").isNotNull()
			.isEqualTo(key(Integer.class, TOPIC));
		assertThat(events.get(0).getFilter()).as("events[0].getFilter()").isNull();
		assertThat(events.get(0).getPriority()).as("events[0].getPriority()").isEqualTo(0);
		assertThat(events.get(0).getConsumer()).as("events[0].getConsumer()").isNotNull();
		assertThat(events.get(0).getConsumer().getName()).as("events[0].getConsumer()")
			.isEqualTo("push");
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
		assertThat(events.get(0)).as("events[0]").isNotNull();
		assertThat(events.get(0).getKey()).as("events[0].getKey()").isNotNull()
			.isEqualTo(key(Integer.class));
		assertThat(events.get(0).getFilter()).as("events[0].getFilter()").isNull();
		assertThat(events.get(0).getPriority()).as("events[0].getPriority()").isEqualTo(0);
		assertThat(events.get(0).getConsumer()).as("events[0].getConsumer()").isNotNull();
		assertThat(events.get(0).getConsumer().getName()).as("events[0].getConsumer()")
			.isEqualTo("push");
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
		assertThat(events.get(0)).as("events[0]").isNotNull();
		assertThat(events.get(0).getKey()).as("events[0].getKey()").isNotNull()
			.isEqualTo(key(Integer.class));
		assertThat(events.get(0).getFilter()).as("events[0].getFilter()").isNull();
		assertThat(events.get(0).getPriority()).as("events[0].getPriority()").isEqualTo(0);
		assertThat(events.get(0).getConsumer()).as("events[0].getConsumer()").isNotNull();
		assertThat(events.get(0).getConsumer().getName()).as("events[0].getConsumer()")
			.isEqualTo("push");
	}

	static public class Consumer11 extends Consumer10
	{
	}
	
	public void checkIllegalConsumesReturnInt()
	{
		_handler.handleError(eq(ConsumerClassError.CONSUMES_MUST_RETURN_VOID), 
			isA(Method.class), (Type) isNull(), (String) isNull());
		replay(_handler);
		_processor.process(ErrorConsumer1.class);
		verify(_handler);
	}
	
	static public class ErrorConsumer1
	{
		@Consumes public int push(Boolean event) {return 0;}
	}

	public void checkIllegalConsumesNoArgument()
	{
		_handler.handleError(eq(ConsumerClassError.CONSUMES_MUST_HAVE_ONE_ARG), 
			isA(Method.class), (Type) isNull(), (String) isNull());
		replay(_handler);
		_processor.process(ErrorConsumer2.class);
		verify(_handler);
	}
	
	static public class ErrorConsumer2
	{
		@Consumes public void push() {}
	}

	public void checkIllegalConsumesTwoArguments()
	{
		_handler.handleError(eq(ConsumerClassError.CONSUMES_MUST_HAVE_ONE_ARG), 
			isA(Method.class), (Type) isNull(), (String) isNull());
		replay(_handler);
		_processor.process(ErrorConsumer3.class);
		verify(_handler);
	}
	
	static public class ErrorConsumer3
	{
		@Consumes public void push(Integer a, Integer b) {}
	}
	
	public void checkIllegalFiltersNoArgument()
	{
		_handler.handleError(eq(ConsumerClassError.FILTERS_MUST_HAVE_ONE_ARG), 
			isA(Method.class), (Type) isNull(), (String) isNull());
		replay(_handler);
		_processor.process(ErrorConsumer4.class);
		verify(_handler);
	}
	
	static public class ErrorConsumer4
	{
		@Consumes public void push(Integer event) {}
		@Filters public boolean filter() {return true;}
	}

	public void checkIllegalFiltersNoReturn()
	{
		_handler.handleError(eq(ConsumerClassError.FILTERS_MUST_RETURN_BOOLEAN), 
			isA(Method.class), (Type) isNull(), (String) isNull());
		replay(_handler);
		_processor.process(ErrorConsumer5.class);
		verify(_handler);
	}
	
	static public class ErrorConsumer5
	{
		@Consumes public void push(Integer event) {}
		@Filters public void filter(Integer event) {}
	}

	public void checkIllegalConsumesForUnregisteredTypeChannel()
	{
		_handler.handleError(eq(ConsumerClassError.CONSUMES_EVENT_MUST_BE_REGISTERED), 
			isA(Method.class), eq(Long.class), eq(""));
		replay(_handler);
		_processor.process(ErrorConsumer6.class);
		verify(_handler);
	}
	
	// Error because no registered channel for Long type
	static public class ErrorConsumer6
	{
		@Consumes public void push(Long event) {}
	}

	//FIXME this TC is incorrect (although the tested code is fine!!!)
	public void checkIllegalConsumesForUnregisteredTopicChannel()
	{
		_handler.handleError(eq(ConsumerClassError.CONSUMES_EVENT_MUST_BE_REGISTERED), 
			isA(Method.class), eq(Integer.class), eq(BAD_TOPIC));
		replay(_handler);
		_processor.process(ErrorConsumer7.class);
		verify(_handler);
	}
	
	// Error because no registered channel for topic
	static public class ErrorConsumer7
	{
		@Consumes(topic = BAD_TOPIC) public void push(Integer event) {}
	}

	public void checkIllegalFiltersReturnInt()
	{
		_handler.handleError(eq(ConsumerClassError.FILTERS_MUST_RETURN_BOOLEAN), 
			isA(Method.class), (Type) isNull(), (String) isNull());
		replay(_handler);
		_processor.process(ErrorConsumer8.class);
		verify(_handler);
	}
	
	static public class ErrorConsumer8
	{
		@Consumes public void push(Integer event) {}
		@Filters public int filter(Integer event) {return 0;}
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
	private AnnotationProcessor _processor;
	private ErrorHandler _handler;
}
