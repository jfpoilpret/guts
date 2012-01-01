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

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInjectionListener<T> implements InjectableInjectionListener<T>
{
	/*
	 * (non-Javadoc)
	 * @see net.guts.common.injection.InjectableInjectionListener#flush()
	 */
	@Override final public void flush()
	{
		// Register all pending instances with the newly injected EventService
		for (T injectee: _pendingInjectees)
		{
			registerInjectee(injectee);
		}
		_pendingInjectees.clear();
		_flushed = true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.inject.spi.InjectionListener#afterInjection(java.lang.Object)
	 */
	@Override final public void afterInjection(T injectee)
	{
		// At Injector creation, it may happen some objects are injected and passed
		// to afterInjection() _before_ other dependencies get injected by Guice;
		// In such cases, it is necessary to defer injectee registration to dependent
		// services.
		if (!_flushed)
		{
			_pendingInjectees.add(injectee);
		}
		else
		{
			registerInjectee(injectee);
		}
	}

	/**
	 * 
	 * @param injectee
	 */
	abstract protected void registerInjectee(T injectee);

	final private List<T> _pendingInjectees = new ArrayList<T>();
	private boolean _flushed = false;
}
