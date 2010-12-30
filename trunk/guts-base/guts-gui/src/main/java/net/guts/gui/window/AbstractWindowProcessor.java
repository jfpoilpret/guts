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

package net.guts.gui.window;

import javax.swing.RootPaneContainer;

public abstract class AbstractWindowProcessor<U, V extends RootPaneContainer>
implements WindowProcessor
{
	protected AbstractWindowProcessor(Class<U> processedClass)
	{
		_processedClass = processedClass;
	}
	
	@SuppressWarnings("unchecked") @Override 
	final public <T extends RootPaneContainer> void process(T root, RootPaneConfig<T> config)
	{
		if (_processedClass.isInstance(root))
		{
			processRoot((U) root, (RootPaneConfig<V>) config);
		}
	}
	
	abstract protected void processRoot(U root, RootPaneConfig<V> config);

	final protected Class<U> _processedClass;
}
