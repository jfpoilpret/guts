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

package net.guts.gui.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.injection.AbstractSingletonModule;
import net.guts.gui.window.WindowProcessor;
import net.guts.gui.window.Windows;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.jgoodies.validation.ValidationMessage;

/**
 * Guice {@link com.google.inject.Module} for Guts-GUI Validation support, based
 * on JGoodies Validation library.
 * This module must be added to the list of modules passed to 
 * {@link com.google.inject.Guice#createInjector}:
 * <pre>
 * Injector injector = Guice.createInjector(new ValidationModule(), ...);
 * </pre>
 * <p/>
 * This Module requires that {@code jgoodies-validation.jar} be present in the
 * classpath, otherwise adding this Module will be effectless.
 * 
 * @author Jean-Francois Poilpret
 */
public final class ValidationModule extends AbstractSingletonModule
{
	static final private Logger _logger = LoggerFactory.getLogger(ValidationModule.class);
	
	@Override protected void configure()
	{
		if (isJGoodiesValidationInClasspath())
		{
			install(new FactoryModuleBuilder()
				.implement(ValidationMessage.class, ResourceValidationMessage.class)
				.build(ValidationMessageFactory.class));
			
			requestStaticInjection(ValidationHelper.class);
			Windows.bindWindowProcessor(binder(), WindowProcessor.SESSION_STORAGE + 1000)
				.to(WpWindowValidation.class);
		}
		else
		{
			_logger.warn(
				"JGoodies Validation library not in classpath, ValidationModule not used.");
		}
	}
	
	static boolean isJGoodiesValidationInClasspath()
	{
		try
		{
			Class.forName("com.jgoodies.validation.ValidationResult");
			return true;
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
	}
}
