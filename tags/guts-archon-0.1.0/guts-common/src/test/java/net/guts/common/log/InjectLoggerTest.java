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

package net.guts.common.log;

import static org.fest.assertions.Assertions.assertThat;
import org.slf4j.Logger;
import org.testng.annotations.Test;

import com.google.inject.Guice;

@Test(groups = "itest")
public class InjectLoggerTest
{
	public void testNonStaticNonFinalPrivateFieldLoggerInjection()
	{
		NonStaticNonFinalPrivateFieldLoggerClass instance = 
			Guice.createInjector(new LogModule()).getInstance(
				NonStaticNonFinalPrivateFieldLoggerClass.class);
		assertThat(instance._logger).as("injected logger").isNotNull();
		assertThat(instance._logger.getName()).as("injected logger name").isEqualTo(
			NonStaticNonFinalPrivateFieldLoggerClass.class.getName());
	}
	static class NonStaticNonFinalPrivateFieldLoggerClass
	{
		@InjectLogger private Logger _logger;
	}

	public void testNonStaticFinalPrivateFieldLoggerInjection()
	{
		NonStaticFinalPrivateFieldLoggerClass instance = 
			Guice.createInjector(new LogModule()).getInstance(
				NonStaticFinalPrivateFieldLoggerClass.class);
		assertThat(instance._logger).as("injected logger").isNotNull();
		assertThat(instance._logger.getName()).as("injected logger name").isEqualTo(
			NonStaticFinalPrivateFieldLoggerClass.class.getName());
	}
	static class NonStaticFinalPrivateFieldLoggerClass
	{
		@InjectLogger final private Logger _logger = null;
	}

	public void testStaticNonFinalPrivateFieldLoggerInjection()
	{
		StaticNonFinalPrivateFieldLoggerClass instance = 
			Guice.createInjector(new LogModule()).getInstance(
				StaticNonFinalPrivateFieldLoggerClass.class);
		assertThat(instance._logger).as("injected logger").isNotNull();
		assertThat(instance._logger.getName()).as("injected logger name").isEqualTo(
			StaticNonFinalPrivateFieldLoggerClass.class.getName());
	}
	static class StaticNonFinalPrivateFieldLoggerClass
	{
		@InjectLogger static private Logger _logger;
	}

	public void testNonAnnonatedFieldLoggerInjection()
	{
		NonAnnotatedFieldLoggerClass instance = 
			Guice.createInjector(new LogModule()).getInstance(
				NonAnnotatedFieldLoggerClass.class);
		assertThat(instance._logger).as("injected logger").isNull();
	}
	static class NonAnnotatedFieldLoggerClass
	{
		private Logger _logger;
	}

	public void testBadTypeFieldLoggerInjection()
	{
		BadTypeFieldLoggerClass instance = 
			Guice.createInjector(new LogModule()).getInstance(
				BadTypeFieldLoggerClass.class);
		assertThat(instance._logger).as("injected logger").isNull();
	}
	static class BadTypeFieldLoggerClass
	{
		@InjectLogger private String _logger;
	}
}
