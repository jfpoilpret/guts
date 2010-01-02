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

package net.guts.gui.action;

import java.util.List;

public abstract class AbstractTask<T, V> implements Task<T, V>, TaskListener<T, V>
{
	@Override public void doInBackground(Task<?, ?> source)
	{
	}

	@Override public void failed(Task<?, ?> source, Throwable cause)
	{
	}

	@Override public void finished(Task<?, ?> source)
	{
	}

	@Override public void process(Task<?, ?> source, List<V> chunks)
	{
	}

	@Override public void succeeded(Task<?, ?> source, T result)
	{
	}
}
