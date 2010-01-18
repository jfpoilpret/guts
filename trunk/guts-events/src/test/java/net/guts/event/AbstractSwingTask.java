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

package net.guts.event;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

public abstract class AbstractSwingTask<T, V> extends SwingWorker<T, V>
{
	protected void succeeded(T result) {}
	protected void cancelled() {}
	protected void interrupted(InterruptedException e) {}
	protected void failed(Throwable cause) {}
	protected void finished() {}

	@Override protected final void done()
	{
		try
		{
			if (isCancelled())
			{
				cancelled();
			}
			else
			{
				try
				{
					succeeded(get());
				}
				catch (InterruptedException e)
				{
					interrupted(e);
				}
				catch (ExecutionException e)
				{
					failed(e.getCause());
				}
			}
		}
		finally
		{
			finished();
		}
	}
}
