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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.same;
import static org.easymock.classextension.EasyMock.verify;
import static org.fest.assertions.Assertions.assertThat;

import net.guts.event.Channel;
import net.guts.event.ConsumerExceptionHandler;
import net.guts.event.Consumes;
import net.guts.event.ErrorHandler;
import net.guts.event.EventService;
import net.guts.event.Filters;
import net.guts.event.internal.AnnotationProcessor;
import net.guts.event.internal.AnnotationProcessorFactory;
import net.guts.event.internal.ChannelKey;
import net.guts.event.internal.Cleaner;
import net.guts.event.internal.EventServiceImpl;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

@Test(groups = "utest")
public class EventServiceTest
{
	@BeforeMethod public void setup()
	{
		_errorHandler = createMock(ErrorHandler.class);
		_exceptionHandler = createMock(ConsumerExceptionHandler.class);
		_cleaner = createNiceMock(Cleaner.class);
		final Map<Class<? extends Annotation>, Provider<Executor>> executors =
			new HashMap<Class<? extends Annotation>, Provider<Executor>>();
		AnnotationProcessorFactory factory = new AnnotationProcessorFactory()
		{
			public AnnotationProcessor create(Set<ChannelKey> channels)
			{
				return new AnnotationProcessor(_errorHandler, channels, executors);
			}
		};
		_service = new EventServiceImpl(factory, _exceptionHandler, _cleaner);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void checkGetUnregisteredChannel()
	{
		_service.getChannel(TypeLiteral.get(Integer.class), null);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void checkGetUnregisteredTopicChannel()
	{
		_service.registerChannel(TypeLiteral.get(Integer.class), null);
		_service.registerChannel(TypeLiteral.get(Integer.class), TOPIC);
		_service.getChannel(TypeLiteral.get(Integer.class), BAD_TOPIC);
	}
	
	public void checkRegisterAndGetChannel()
	{
		_service.registerChannel(TypeLiteral.get(Integer.class), null);
		Channel<Integer> channel = 
			_service.getChannel(TypeLiteral.get(Integer.class), null);
		assertThat(channel).as("getChannel()").isNotNull();
	}
	
	public void checkRegisterSameChannelTwice()
	{
		_service.registerChannel(TypeLiteral.get(Integer.class), null);
		_service.registerChannel(TypeLiteral.get(Integer.class), null);
		Channel<Integer> channel = 
			_service.getChannel(TypeLiteral.get(Integer.class), null);
		assertThat(channel).as("getChannel()").isNotNull();
	}
	
	public void checkGetSameChannelTwiceIsUnique()
	{
		_service.registerChannel(TypeLiteral.get(Integer.class), null);
		Channel<Integer> channel1 = 
			_service.getChannel(TypeLiteral.get(Integer.class), null);
		assertThat(channel1).as("first call to getChannel()").isNotNull();
		Channel<Integer> channel2 = 
			_service.getChannel(TypeLiteral.get(Integer.class), null);
		assertThat(channel2).as("second call to getChannel()").isNotNull();
		assertThat(channel1).as("first call to getChannel()").isSameAs(channel2);
	}
	
	public void checkGenericChannelRetrieval()
	{
		_service.registerChannel(new TypeLiteral<List<Integer>>(){}, null);
		_service.registerChannel(new TypeLiteral<List<String>>(){}, null);
		Channel<List<Integer>> channel1 = 
			_service.getChannel(new TypeLiteral<List<Integer>>(){}, null);
		Channel<List<String>> channel2 = 
			_service.getChannel(new TypeLiteral<List<String>>(){}, null);
		assertThat(channel1).as("getChannel(List<Integer>)").isNotNull();
		assertThat(channel2).as("getChannel(List<String>)").isNotNull();
		assertThat(channel1).as("getChannel(List<Integer>)").isNotSameAs(channel2);
	}
	
	public void checkNullAndEmptyTopicsEquivalent()
	{
		_service.registerChannel(TypeLiteral.get(Integer.class), null);
		Channel<Integer> channel = 
			_service.getChannel(TypeLiteral.get(Integer.class), "");
		assertThat(channel).as("getChannel()").isNotNull();
	}
	
	public void checkEmptyAndNullTopicsEquivalent()
	{
		_service.registerChannel(TypeLiteral.get(Integer.class), "");
		Channel<Integer> channel = 
			_service.getChannel(TypeLiteral.get(Integer.class), null);
		assertThat(channel).as("getChannel()").isNotNull();
	}
	
	public void checkNonEmptyAndNullTopicsDifferent()
	{
		_service.registerChannel(TypeLiteral.get(Integer.class), null);
		_service.registerChannel(TypeLiteral.get(Integer.class), TOPIC);
		Channel<Integer> channel1 = 
			_service.getChannel(TypeLiteral.get(Integer.class), null);
		assertThat(channel1).as("getChannel() for null topic").isNotNull();
		Channel<Integer> channel2 = 
			_service.getChannel(TypeLiteral.get(Integer.class), TOPIC);
		assertThat(channel2).as("getChannel() for non empty topic").isNotNull();
		assertThat(channel1).as("getChannel() for null topic").isNotSameAs(channel2);
	}
	
	public void checkRegisterPrimitiveTypeEvent()
	{
		_service.registerChannel(TypeLiteral.get(int.class), null);
		Channel<Integer> channel = 
			_service.getChannel(TypeLiteral.get(int.class), null);
		assertThat(channel).as("getChannel()").isNotNull();
	}
	
	public void checkIntegerEventChannelToConsumer()
	{
		Integer event1 = new Integer(3);
		Integer event2 = new Integer(10);
		_service.registerChannel(TypeLiteral.get(Integer.class), null);
		Channel<Integer> channel = 
			_service.getChannel(TypeLiteral.get(Integer.class), null);

		Consumer1 mock = createStrictMock(Consumer1.class);
		mock.push(same(event1));
		mock.push(same(event2));
		Consumer2 consumer = new Consumer2(mock);
		
		replay(mock, _errorHandler, _exceptionHandler);
		_service.registerConsumers(consumer);
		channel.publish(event1);
		channel.publish(event2);
		verify(mock, _errorHandler, _exceptionHandler);
	}
	
	static public interface Consumer1
	{
		public void push(Integer event);
	}
	static public class Consumer2
	{
		public Consumer2(Consumer1 mock)
		{
			_mock = mock;
		}
		@Consumes public void push(Integer event)
		{
			_mock.push(event);
		}
		private final Consumer1 _mock;
	}
	
	public void checkIntegerEventChannelToConsumerWithFilter()
	{
		Integer event1 = new Integer(3);
		Integer event2 = new Integer(11);
		Integer event3 = new Integer(15);
		Integer event4 = new Integer(0);
		_service.registerChannel(TypeLiteral.get(Integer.class), null);
		Channel<Integer> channel = 
			_service.getChannel(TypeLiteral.get(Integer.class), null);

		Consumer1 mock = createStrictMock(Consumer1.class);
		mock.push(same(event2));
		mock.push(same(event3));
		Consumer2 consumer = new Consumer3(mock);
		
		replay(mock, _errorHandler, _exceptionHandler);
		_service.registerConsumers(consumer);
		channel.publish(event1);
		channel.publish(event2);
		channel.publish(event3);
		channel.publish(event4);
		verify(mock, _errorHandler, _exceptionHandler);
	}
	
	static public class Consumer3 extends Consumer2
	{
		public Consumer3(Consumer1 mock)
		{
			super(mock);
		}
		@Filters public boolean filter(Integer event)
		{
			return event > 10;
		}
	}

	static private final String TOPIC = "dummy";
	static private final String BAD_TOPIC = "unexisting";
	private EventService _service;
	private ErrorHandler _errorHandler;
	private ConsumerExceptionHandler _exceptionHandler;
	private Cleaner _cleaner;
}
