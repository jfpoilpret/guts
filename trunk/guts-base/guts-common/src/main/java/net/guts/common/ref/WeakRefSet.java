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

package net.guts.common.ref;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.guts.common.cleaner.Cleanable;

public class WeakRefSet<T> implements Cleanable
{
	static public <T> WeakRefSet<T> create()
	{
		return new WeakRefSet<T>();
	}
	
	public boolean add(T element)
	{
		if (element == null)
		{
			return false;
		}
		synchronized (_list)
		{
			for (WeakReference<T> ref: _list)
			{
				if (ref.get() == element)
				{
					return false;
				}
			}
			return _list.add(new WeakReference<T>(element));
		}
	}

	public boolean isEmpty()
	{
		synchronized (_list)
		{
			return _list.isEmpty();
		}
	}

	public boolean remove(T element)
	{
		synchronized (_list)
		{
			Iterator<WeakReference<T>> i = _list.iterator();
			while (i.hasNext())
			{
				if (i.next().get() == element)
				{
					i.remove();
					return true;
				}
			}
			return false;
		}
	}

	static public interface Performer<T>
	{
		public boolean perform(T element);
	}
	
	public boolean perform(Performer<T> performer)
	{
		boolean cleanup = false;
		boolean complete = true;
		synchronized (_list)
		{
			for (WeakReference<T> ref: _list)
			{
				T element = ref.get();
				if (element != null)
				{
					if (!performer.perform(element))
					{
						complete = false;
						break;
					}
				}
				else
				{
					cleanup = true;
				}
			}
		}
		if (cleanup)
		{
			cleanup();
		}
		return complete;
	}
	
	@Override public void cleanup()
	{
		remove(null);
	}

	final private List<WeakReference<T>> _list = new ArrayList<WeakReference<T>>();
}
