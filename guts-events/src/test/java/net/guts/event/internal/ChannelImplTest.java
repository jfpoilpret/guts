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

import java.lang.reflect.Method;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executor;

import org.easymock.Capture;
import org.easymock.classextension.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.same;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;
import static org.easymock.classextension.EasyMock.verify;

import net.guts.event.ConsumerExceptionHandler;
import net.guts.event.internal.ChannelImpl;
import net.guts.event.internal.Cleaner;

@Test(groups = "utest")
public class ChannelImplTest
{
	@BeforeMethod public void setup()
	{
		_exceptionHandler = createMock(ConsumerExceptionHandler.class);
		_cleanup = createNiceMock(Cleaner.class);
		_channel = new ChannelImpl<Integer>(
			Integer.class, "", _exceptionHandler, _cleanup);
	}
	
	public void checkTwoExecutors() throws Exception
	{
		Method method = Consumer2.class.getDeclaredMethod("push", Integer.class);

		Consumer1 mock1 = createMock(Consumer1.class);
		Consumer1 mock2 = createMock(Consumer1.class);
		Consumer2 consumer1 = new Consumer2(mock1);
		Consumer2 consumer2 = new Consumer2(mock2);

		Executor exec1 = createMock(Executor.class);
		Executor exec2 = createMock(Executor.class);
		_channel.addConsumer(consumer1, method, 0, null, exec1);
		_channel.addConsumer(consumer2, method, 0, null, exec1);
		_channel.addConsumer(consumer1, method, 0, null, exec2);
		_channel.addConsumer(consumer2, method, 0, null, exec2);

		Capture<Runnable> capture1 = new Capture<Runnable>();
		exec1.execute(EasyMock.capture(capture1));
		Capture<Runnable> capture2 = new Capture<Runnable>();
		exec2.execute(EasyMock.capture(capture2));
		
		// Check that both executors were called during publish()
		replay(exec1, exec2);
		Integer event = 1;
		_channel.publish(event);
		verify(exec1, exec2);

		// Then call each executor's runnable to check that each consumer get called
		mock1.push(same(event));
		mock2.push(same(event));
		replay(mock1, mock2);
		capture1.getValue().run();
		verify(mock1, mock2);
			
		reset(mock1, mock2);
		mock1.push(same(event));
		mock2.push(same(event));
		replay(mock1, mock2);
		capture2.getValue().run();
		verify(mock1, mock2);
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
		public void push(Integer event)
		{
			_mock.push(event);
		}
		private final Consumer1 _mock;
	}
	
	public void checkPriorities() throws Exception
	{
		Method method = Consumer4.class.getDeclaredMethod("push", Integer.class);

		Consumer3 mock = createStrictMock(Consumer3.class);
		Executor exec = new Executor()
		{
			public void execute(Runnable command)
			{
				command.run();
			}
		};
		
		Random random = new Random();
		SortedMap<Integer, Consumer4> expected = new TreeMap<Integer, Consumer4>();
		for (int i = 0; i < 20; i++)
		{
			int priority = random.nextInt(1000);
			if (expected.get(priority) == null)
			{
				Consumer4 consumer = new Consumer4(mock, priority); 
				expected.put(priority, consumer);
				_channel.addConsumer(consumer, method, priority, null, exec);
			}
		}
		
		Integer event = 1;
		for (int i: expected.keySet())
		{
			mock.push(same(event), eq(i));
		}

		replay(mock);
		_channel.publish(event);
		verify(mock);
	}

	static public interface Consumer3
	{
		public void push(Integer event, int priority);
	}
	static public class Consumer4
	{
		public Consumer4(Consumer3 mock, int priority)
		{
			_mock = mock;
			_priority = priority;
		}
		public void push(Integer event)
		{
			_mock.push(event, _priority);
		}
		private final Consumer3 _mock;
		private final int _priority;
	}
	
	private ConsumerExceptionHandler _exceptionHandler;
	private Cleaner _cleanup;
	private ChannelImpl<Integer> _channel;
}
