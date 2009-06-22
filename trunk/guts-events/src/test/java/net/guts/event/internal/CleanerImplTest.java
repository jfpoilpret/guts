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

import java.lang.Thread.UncaughtExceptionHandler;

import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups = "utest")
public class CleanerImplTest
{
	@BeforeMethod public void setup()
	{
		_handler = new ThreadExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(_handler);
		_control = EasyMock.createStrictControl();
		_clean1 = _control.createMock(Cleanable.class);
		_clean2 = _control.createMock(Cleanable.class);
		_clean3 = _control.createMock(Cleanable.class);
		_cleanup = new CleanerImpl(DELAY_SECS);
	}
	
	@AfterMethod public void teardown()
	{
		_cleanup.stop();
		Thread.setDefaultUncaughtExceptionHandler(null);
	}

	//TODO better checks: don't use EasyMock, check time cleanup is called,
	// do it several times (2 or 3)
	public void checkPeriodicCalls() throws Exception
	{
		_clean1.cleanup();
		_clean2.cleanup();
		_control.replay();
		_cleanup.addCleanable(_clean1);
		_cleanup.addCleanable(_clean2);
		Thread.sleep(DELAY_SECS * 1000 + 100);
		_handler.verify();
		_control.verify();

		// Cannot work with EasyMock: Threading execption?
//		_clean1.cleanup();
//		expectLastCall().once();
//		_clean2.cleanup();
//		expectLastCall().once();
//		replay(_clean1, _clean2);
//		Thread.sleep(DELAY_SECS * 1000 + 100);
//		verify(_clean1, _clean2);
	}
	
	public void checkCleanableRemoval() throws Exception
	{
		_clean1.cleanup();
		_control.replay();
		_cleanup.addCleanable(_clean1);
		_cleanup.addCleanable(_clean2);
		_cleanup.removeCleanable(_clean2);
		Thread.sleep(DELAY_SECS * 1000 + 100);
		_handler.verify();
		_control.verify();
	}
	
	public void checkEnqueue() throws Exception
	{
		_clean1.cleanup();
		_clean2.cleanup();
		_clean3.cleanup();

		_control.replay();
		_cleanup.enqueueCleanable(_clean1);
		_cleanup.enqueueCleanable(_clean2);
		_cleanup.enqueueCleanable(_clean3);
		Thread.sleep(100);
		_handler.verify();
		_control.verify();
	}

	public void checkInterruptability() throws Exception
	{
		_control.replay();
		_cleanup.addCleanable(_clean1);
		_cleanup.stop();
		
		Thread.sleep(DELAY_SECS * 1000 + 100);
		_handler.verify();
		_control.verify();

		_control.reset();
		_clean1.cleanup();
		_control.replay();
		_cleanup.start();
		Thread.sleep(DELAY_SECS * 1000 + 100);
		_handler.verify();
		_control.verify();
	}
	
	static private class ThreadExceptionHandler implements UncaughtExceptionHandler
	{
		public void verify()
		{
			if (_exception != null)
			{
				throw new AssertionError(_exception);
			}
		}
		
		public void uncaughtException(Thread t, Throwable e)
		{
			_exception = e;
		}

		private Throwable _exception = null;
	}

	static final private long DELAY_SECS = 1;
	private CleanerImpl _cleanup;
	private IMocksControl _control;
	private Cleanable _clean1;
	private Cleanable _clean2;
	private Cleanable _clean3;
	private ThreadExceptionHandler _handler;
}
