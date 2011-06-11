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

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;

public final class ValidationModule extends AbstractModule
{
	@Override protected void configure()
	{
		bind(ValidationMessageFactory.class).toProvider(FactoryProvider.newFactory(
			ValidationMessageFactory.class, ResourceValidationMessage.class));
		requestStaticInjection(ValidationHelper.class);
	}
	
	@Override public boolean equals(Object other)
	{
		return other instanceof ValidationModule;
	}

	@Override public int hashCode()
	{
		return ValidationModule.class.hashCode();
	}
}
