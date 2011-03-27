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

package net.guts.properties;

import static org.testng.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

@Test(groups = "utest") 
public class BeanTest
{
	@BeforeMethod public void init()
	{
		// Reset all called properties
		RecordedGetters.calledProperties();
		_b1helper = Bean.create(Bean1.class);
		_b1mock = _b1helper.mock();
	}
	
	@Test public void checkPropertyName() throws Exception
	{
		Property<Bean1, String> property = _b1helper.property(_b1mock.getOneString());
		assertEquals("oneString", property.name());
	}
	
	@Test public void checkStringPropertyGet() throws Exception
	{
		Property<Bean1, String> property = _b1helper.property(_b1mock.getOneString());
		
		Bean1 bean = new Bean1();
		
		// Check getter
		bean.setOneString("TEST");
		assertEquals("TEST", property.get(bean));
	}
	
	@Test public void checkStringPropertySet() throws Exception
	{
		Property<Bean1, String> property = _b1helper.property(_b1mock.getOneString());
		
		Bean1 bean = new Bean1();
		
		// Check getter
		property.set(bean, "TESTS");
		assertEquals("TESTS", bean.getOneString());
	}
	
	@Test public void checkPrimitivePropertyGet() throws Exception
	{
		Property<Bean1, Integer> property = _b1helper.property(_b1mock.getOneInt());
		
		Bean1 bean = new Bean1();
		
		// Check getter
		bean.setOneInt(123);
		assertEquals(123, (int) property.get(bean));
	}
	
	// check API misuse (eg don't use mock to get property())
	@Test(expectedExceptions = IllegalStateException.class) 
	public void checkAPIMisuseNoPreviousMockCall() throws Exception
	{
		_b1helper.property(0);
	}
	
	@Test(expectedExceptions = IllegalStateException.class) 
	public void checkAPIMisuseBadPreviousMockCall() throws Exception
	{
		_b1mock.getOneString();
		_b1helper.property(0);
	}
	
	@Test(expectedExceptions = IllegalStateException.class) 
	public void checkAPIMisuseBadPreviousMockCall2() throws Exception
	{
		_b1mock.getOneInt();
		_b1helper.property(null);
	}
	
	// check final beans / methods
	@Test(expectedExceptions = IllegalArgumentException.class) 
	public void checkErrorOnFinalBeanClass() throws Exception
	{
		Bean.create(Bean2.class);
	}
	
	@Test(expectedExceptions = IllegalStateException.class) 
	public void checkErrorOnFinalMethod() throws Exception
	{
		Bean<Bean3> helper = Bean.create(Bean3.class);
		Bean3 mock = helper.mock();
		helper.property(mock.getOneString());
	}
	
	// FIXME this test exhibits a dangerous bug!
	// How to work around?
	// - exception for any bean with a final getter? => restricts usage!
	// - log (where?) list of final methods during mock construction?
	// - let it as an option to API user?
	@Test(expectedExceptions = IllegalStateException.class) 
	public void checkErrorOnFinalMethod2() throws Exception
	{
		Bean<Bean3> helper = Bean.create(Bean3.class);
		Bean3 mock = helper.mock();
		mock.getAnotherString();
		helper.property(mock.getOneString());
	}
	
	// check complex bean hierarchies
	@Test public void checkChainedBeanProperty()
	{
		Bean<Bean4> helper4 = Bean.create(Bean4.class);
		Bean4 mock = helper4.mock();
		Property<Bean4, String> property = helper4.property(mock.getBean1().getOneString());
		assertNotNull(property, "Property for Bean4.getBean1.getOneString()");
		assertEquals(property.name(), "bean1.oneString", "Property's name for Bean4.getBean1().getOneString()");
		assertEquals(property.type(), TypeLiteral.get(String.class), "Property's type for Bean4.getBean1().getOneString()");
	}
	
	@Test(expectedExceptions = UnsupportedOperationException.class) 
	public void checkNonGetterCallsAreIllegal()
	{
		_b1mock.setOneInt(0);
	}
	
	@Test(expectedExceptions = IllegalStateException.class) 
	public void checkMismatchedTypes()
	{
		_b1mock.getOneInt();
		_b1mock.getOneInt();
		_b1helper.property(0);
	}
	
	// check proxy generation
	@Test public void checkProxyGeneration()
	{
		Bean1 bean = new Bean1();
		Bean1 proxy = _b1helper.proxy(bean);
		assertNotNull(proxy, "BeanHelper<Bean1>.proxy(new Bean1())");
		assertTrue(proxy instanceof ChangeListenerAdapter,
				"proxy instanceof ChangeListenerAdapter");
	}
	
	// check events propagation
	@Test public void checkStringPropertyEventPropagation()
	{
		Bean1 bean = new Bean1();
		Bean1 proxy = _b1helper.proxy(bean);
		ChangeListenerAdapter pclProxy = (ChangeListenerAdapter) proxy;
		EventListenerMock listener = new EventListenerMock();
		
		pclProxy.addPropertyChangeListener(listener);
		proxy.setOneString("Dummy");
		
		PropertyChangeEvent event = listener.getEvent();
		assertNotNull(event, "Passed event");
		assertSame(event.getSource(), bean, "event.getSource()");
		assertEquals(event.getPropertyName(), "oneString",
				"event.getPropertyName()");
		assertNull(event.getOldValue(), "event.getOldValue()");
		assertEquals(event.getNewValue(), "Dummy", "event.getNewValue()");
	}
	
	@Test public void checkIntPropertyEventPropagation()
	{
		Bean1 bean = new Bean1();
		Bean1 proxy = _b1helper.proxy(bean);
		ChangeListenerAdapter pclProxy = (ChangeListenerAdapter) proxy;
		EventListenerMock listener = new EventListenerMock();
		
		pclProxy.addPropertyChangeListener(listener);
		proxy.setOneInt(12);
		
		PropertyChangeEvent event = listener.getEvent();
		assertNotNull(event, "Passed event");
		assertSame(event.getSource(), bean, "event.getSource()");
		assertEquals(event.getPropertyName(), "oneInt",
				"event.getPropertyName()");
		assertEquals(event.getOldValue(), 0, "event.getOldValue()");
		assertEquals(event.getNewValue(), 12, "event.getNewValue()");
	}
	
	// check events propagation through property name registration
	@Test public void checkNamedPropertyEventPropagation()
	{
		Bean1 bean = new Bean1();
		Bean1 proxy = _b1helper.proxy(bean);
		ChangeListenerAdapter pclProxy = (ChangeListenerAdapter) proxy;
		EventListenerMock listener = new EventListenerMock();
		
		pclProxy.addPropertyChangeListener("oneString", listener);
		proxy.setOneString("Dummy");
		
		PropertyChangeEvent event = listener.getEvent();
		assertNotNull(event, "Passed event");
		assertSame(event.getSource(), bean, "event.getSource()");
		assertEquals(event.getPropertyName(), "oneString",
				"event.getPropertyName()");
		assertNull(event.getOldValue(), "event.getOldValue()");
		assertEquals(event.getNewValue(), "Dummy", "event.getNewValue()");
	}
	
	@Test public void checkNamedPropertyEventNonPropagation()
	{
		Bean1 bean = new Bean1();
		Bean1 proxy = _b1helper.proxy(bean);
		ChangeListenerAdapter pclProxy = (ChangeListenerAdapter) proxy;
		EventListenerMock listener = new EventListenerMock();
		
		pclProxy.addPropertyChangeListener("oneInt", listener);
		proxy.setOneString("Dummy");
		
		PropertyChangeEvent event = listener.getEvent();
		assertNull(event, "Passed event");
	}
	
	static public class Bean1
	{
		private String _oneString;
		private int _oneInt;
		
		public int getOneInt()
		{
			return _oneInt;
		}
		
		public void setOneInt(int oneInt)
		{
			_oneInt = oneInt;
		}
		
		public String getOneString()
		{
			return _oneString;
		}
		
		public void setOneString(String oneString)
		{
			_oneString = oneString;
		}
	}
	
	static final public class Bean2
	{
		private String _oneString;
		
		public String getOneString()
		{
			return _oneString;
		}
		
		public void setOneString(String oneString)
		{
			_oneString = oneString;
		}
	}
	
	static public class Bean3
	{
		private String _oneString;
		private String _anotherString;
		
		public String getAnotherString()
		{
			return _anotherString;
		}
		
		public void setAnotherString(String anotherString)
		{
			_anotherString = anotherString;
		}
		
		final public String getOneString()
		{
			return _oneString;
		}
		
		public void setOneString(String oneString)
		{
			_oneString = oneString;
		}
	}
	
	static public class Bean4
	{
		private Bean1 _bean2;
		
		public Bean1 getBean1()
		{
			return _bean2;
		}
		
		public void setBean1(Bean1 bean2)
		{
			_bean2 = bean2;
		}
	}
	
	static private class EventListenerMock implements PropertyChangeListener
	{
		@Override public void propertyChange(PropertyChangeEvent evt)
		{
			_event = evt;
		}
		
		public PropertyChangeEvent getEvent()
		{
			return _event;
		}
		
		private PropertyChangeEvent _event = null;
	}
	
	private Bean<Bean1> _b1helper;
	private Bean1 _b1mock;
}
