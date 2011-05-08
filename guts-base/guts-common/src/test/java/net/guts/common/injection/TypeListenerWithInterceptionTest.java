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

package net.guts.common.injection;

import static org.fest.assertions.Assertions.assertThat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.fest.assertions.Fail;
import org.testng.annotations.Test;

import net.guts.common.type.TypeHelper;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.spi.InjectionListener;

@Test(groups = "itest")
public class TypeListenerWithInterceptionTest
{
	public void checkCanDetectAnnotatedMethodOnInterceptedClass()
	{
		final TestInjectionListener listener = new TestInjectionListener();
		Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override protected void configure()
			{
				OneTypeListener<Object> typeListener = 
					new OneTypeListener<Object>(Object.class, listener);
				bindListener(Matchers.hasMethodAnnotatedWith(Annotated.class), typeListener);
				// Add interceptor to methods that return GutsAction to ensure registration
				bindInterceptor(Matchers.anyClass(), com.google.inject.matcher.Matchers.any(), new TestInterceptor());
			}
		});
		assertThat(listener.listenerCalled).as("Listener called before instance creation").isFalse();
		TestClass instance = injector.getInstance(TestClass.class);
		assertThat(listener.listenerCalled).as("Listener called after instance creation").isTrue();
		assertThat(listener.isMethodAnnotated).as("Listener found annotated method").isTrue();
	}
	
	@Target(ElementType.METHOD) 
	@Retention(RetentionPolicy.RUNTIME)
	static public @interface Annotated
	{
	}
	
	static public class TestClass
	{
		@Annotated public void f()
		{
		}
	}

	static private class TestInjectionListener implements InjectionListener<Object>
	{
		@Override public void afterInjection(Object injectee)
		{
			if (injectee instanceof TestClass)
			{
				listenerCalled = true;
				// Check that injectee had an annotated method called f()
				try
				{
					isMethodAnnotated = TypeHelper.extractPureClass(injectee)
						.getMethod("f").isAnnotationPresent(Annotated.class);
				}
				catch (Exception e)
				{
					Fail.fail("Unexpected exception", e);
				}
			}
		}

		public boolean listenerCalled = false;
		public boolean isMethodAnnotated = false;
	}
	
	static private class TestInterceptor implements MethodInterceptor
	{
		@Override public Object invoke(MethodInvocation invocation) throws Throwable
		{
			return null;
		}
	}
}
