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

package net.guts.common.delegate;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

@Test(groups = "utest")
public class DelayedProxyTest
{
	public void checkCallsForwardedOnInterface()
	{
		TestInterface delegate = DelayedProxy.create(TestInterface.class);
		TestInterface mock = EasyMock.createStrictMock(TestInterface.class);
		TestInterface target = new TestImplementation(mock);
		DelayedProxy.setDelegate(delegate, target);

		doCalls(mock);
		EasyMock.replay(mock);

		doCalls(delegate);
		EasyMock.verify(mock);
	}

	public void checkCallsDelayedOnInterface()
	{
		TestInterface delegate = DelayedProxy.create(TestInterface.class);
		TestInterface mock = EasyMock.createStrictMock(TestInterface.class);
		TestInterface target = new TestImplementation(mock);

		doCalls(mock);
		doCalls(delegate);
		EasyMock.replay(mock);

		DelayedProxy.setDelegate(delegate, target);
		EasyMock.verify(mock);
	}

	public void checkCallsForwardedOnClass()
	{
		TestClass delegate = DelayedProxy.create(TestClass.class);
		TestInterface mock = EasyMock.createStrictMock(TestInterface.class);
		TestClass target = new TestClass(mock);
		DelayedProxy.setDelegate(delegate, target);

		doCalls(mock);
		EasyMock.replay(mock);

		doCalls(delegate);
		EasyMock.verify(mock);
	}

	public void checkCallsDelayedOnClass()
	{
		TestClass delegate = DelayedProxy.create(TestClass.class);
		TestInterface mock = EasyMock.createStrictMock(TestInterface.class);
		TestClass target = new TestClass(mock);

		doCalls(mock);
		doCalls(delegate);
		EasyMock.replay(mock);

		DelayedProxy.setDelegate(delegate, target);
		EasyMock.verify(mock);
	}
	
	private void doCalls(TestInterface target)
	{
		target.setA(0);
		target.setB("");
		target.setA(100);
		target.setB("Hello");
	}
	
	private void doCalls(TestClass target)
	{
		target.setA(0);
		target.setB("");
		target.setA(100);
		target.setB("Hello");
	}
	
	public interface TestInterface
	{
		public void setA(int a);
		public void setB(String b);
	}
	
	static public class TestImplementation implements TestInterface
	{
		public TestImplementation(TestInterface spy)
		{
			_spy = spy;
		}
		
		public void setA(int a)
		{
			_spy.setA(a);
		}
		
		public void setB(String b)
		{
			_spy.setB(b);
		}
		
		private final TestInterface _spy;
	}
	
	static public class TestClass
	{
		public TestClass()
		{
			this(null);
		}
		
		public TestClass(TestInterface spy)
		{
			_spy = spy;
		}
		
		public void setA(int a)
		{
			_spy.setA(a);
		}
		
		public void setB(String b)
		{
			_spy.setB(b);
		}
		
		private final TestInterface _spy;
	}
}
