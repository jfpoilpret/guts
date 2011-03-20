package net.guts.properties;

import static org.testng.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.guts.properties.Bean;
import net.guts.properties.ChangeListenerAdapter;
import net.guts.properties.Property;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups = "utest")
public class BeanTest {
	@BeforeMethod
	public void init() {
		_b1helper = Bean.create(Bean1.class);
		_b1mock = _b1helper.mock();
	}

	@Test
	public void checkPropertyName() throws Exception {
		Property<Bean1, String> property = _b1helper.property(_b1mock.getOneString());
		assertEquals("oneString", property.name());
	}

	@Test
	public void checkStringPropertyGet() throws Exception {
		Property<Bean1, String> property = _b1helper.property(_b1mock.getOneString());

		Bean1 bean = new Bean1();

		// Check getter
		bean.setOneString("TEST");
		assertEquals("TEST", property.get(bean));
	}

	@Test
	public void checkStringPropertySet() throws Exception {
		Property<Bean1, String> property = _b1helper.property(_b1mock.getOneString());

		Bean1 bean = new Bean1();

		// Check getter
		property.set(bean, "TESTS");
		assertEquals("TESTS", bean.getOneString());
	}

	@Test
	public void checkPrimitivePropertyGet() throws Exception {
		Property<Bean1, Integer> property = _b1helper.property(_b1mock.getOneInt());

		Bean1 bean = new Bean1();

		// Check getter
		bean.setOneInt(123);
		assertEquals(123, (int) property.get(bean));
	}

	// check API misuse (eg don't use mock to get property())
	@Test(expectedExceptions = RuntimeException.class)
	public void checkAPIMisuseNoPreviousMockCall() throws Exception {
		_b1helper.property(0);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void checkAPIMisuseBadPreviousMockCall() throws Exception {
		_b1mock.getOneString();
		_b1helper.property(0);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void checkAPIMisuseBadPreviousMockCall2() throws Exception {
		_b1mock.getOneInt();
		_b1helper.property(null);
	}

	// check final beans / methods
	@Test(expectedExceptions = RuntimeException.class)
	public void checkErrorOnFinalBeanClass() throws Exception {
		Bean.create(Bean2.class);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void checkErrorOnFinalMethod() throws Exception {
		Bean<Bean3> helper = Bean.create(Bean3.class);
		Bean3 mock = helper.mock();
		helper.property(mock.getOneString());
	}

	// FIXME this test exhibits a dangerous bug!
	// How to work around?
	// - exception for any bean with a final getter? => restricts usage!
	// - log (where?) list of final methods during mock construction?
	// - let it as an option to API user?
	@Test(expectedExceptions = RuntimeException.class)
	public void checkErrorOnFinalMethod2() throws Exception {
		Bean<Bean3> helper = Bean.create(Bean3.class);
		Bean3 mock = helper.mock();
		mock.getAnotherString();
		helper.property(mock.getOneString());
	}

	// check proxy generation
	@Test
	public void checkProxyGeneration() {
		Bean1 bean = new Bean1();
		Bean1 proxy = _b1helper.proxy(bean);
		assertNotNull(proxy, "BeanHelper<Bean1>.proxy(new Bean1())");
		assertTrue(proxy instanceof ChangeListenerAdapter,
				"proxy instanceof ChangeListenerAdapter");
	}

	// check events propagation
	@Test
	public void checkStringPropertyEventPropagation() {
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

	@Test
	public void checkIntPropertyEventPropagation() {
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
	@Test
	public void checkNamedPropertyEventPropagation() {
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

	@Test
	public void checkNamedPropertyEventNonPropagation() {
		Bean1 bean = new Bean1();
		Bean1 proxy = _b1helper.proxy(bean);
		ChangeListenerAdapter pclProxy = (ChangeListenerAdapter) proxy;
		EventListenerMock listener = new EventListenerMock();

		pclProxy.addPropertyChangeListener("oneInt", listener);
		proxy.setOneString("Dummy");

		PropertyChangeEvent event = listener.getEvent();
		assertNull(event, "Passed event");
	}

	// TODO
	// check proxy for bean that already has PCL support

	// check complex bean hierarchies

	static public class Bean1 {
		private String _oneString;
		private int _oneInt;

		public int getOneInt() {
			return _oneInt;
		}

		public void setOneInt(int oneInt) {
			_oneInt = oneInt;
		}

		public String getOneString() {
			return _oneString;
		}

		public void setOneString(String oneString) {
			_oneString = oneString;
		}
	}

	static final public class Bean2 {
		private String _oneString;

		public String getOneString() {
			return _oneString;
		}

		public void setOneString(String oneString) {
			_oneString = oneString;
		}
	}

	static public class Bean3 {
		private String _oneString;
		private String _anotherString;

		public String getAnotherString() {
			return _anotherString;
		}

		public void setAnotherString(String anotherString) {
			_anotherString = anotherString;
		}

		final public String getOneString() {
			return _oneString;
		}

		public void setOneString(String oneString) {
			_oneString = oneString;
		}
	}

	static private class EventListenerMock implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			_event = evt;
		}

		public PropertyChangeEvent getEvent() {
			return _event;
		}

		private PropertyChangeEvent _event = null;
	}

	private Bean<Bean1> _b1helper;
	private Bean1 _b1mock;
}
