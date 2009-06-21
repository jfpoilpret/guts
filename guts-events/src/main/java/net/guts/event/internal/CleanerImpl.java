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

package net.guts.event.internal;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;
import com.google.inject.Singleton;

// Active object that removes from Channels any Consumer with a dangling
// reference
@Singleton
public class CleanerImpl implements Cleaner
{
	@Inject
	public CleanerImpl()
	{
		this(DELAY_SECONDS);
	}

	public CleanerImpl(long delay)
	{
		_delay = delay;
		// Start a thread (as daemon)
		start();
	}
	
	public void start()
	{
		if (_cleaner == null)
		{
			// Start a thread (as daemon)
			_cleaner = new Thread()
			{
				@Override public void run()
				{
					cleanupLoop();
				}
			};
			_cleaner.setDaemon(true);
			_cleaner.setPriority(Thread.MIN_PRIORITY);
			_cleaner.start();
		}
	}
	
	public void stop()
	{
		if (_cleaner != null)
		{
			_cleaner.interrupt();
			_cleaner = null;
		}
	}
	
	public void addCleanable(Cleanable cleanable)
	{
		synchronized(_allCleanables)
		{
			_allCleanables.add(cleanable);
		}
	}

	public void enqueueCleanable(Cleanable cleanable)
	{
		_waitingCleanables.offer(cleanable);
	}

	public void removeCleanable(Cleanable cleanable)
	{
		synchronized(_allCleanables)
		{
			_allCleanables.remove(cleanable);
		}
	}
	
	private void cleanupLoop()
	{
		try
		{
			long nextCleanup = System.currentTimeMillis() + _delay;
			while (true)
			{
				long delay =  nextCleanup - System.currentTimeMillis();
				Cleanable cleanable = _waitingCleanables.poll(delay, TimeUnit.SECONDS);
				if (cleanable != null)
				{
					cleanable.cleanup();
				}
				if (System.currentTimeMillis() >= nextCleanup)
				{
					cleanupAll();
					nextCleanup = System.currentTimeMillis() + _delay;
				}
			}
		}
		catch (InterruptedException e)
		{
			// Re-assert the interrupted state of the thread
			Thread.currentThread().interrupt();
		}
	}
	
	private void cleanupAll()
	{
		synchronized(_allCleanables)
		{
			for (Cleanable cleanable: _allCleanables)
			{
				cleanable.cleanup();
			}
		}
	}

	static final private long DELAY_SECONDS = 300;
	final private long _delay;
	private Thread _cleaner;
	final private BlockingQueue<Cleanable> _waitingCleanables = 
		new LinkedBlockingQueue<Cleanable>();
	final private Set<Cleanable> _allCleanables = new HashSet<Cleanable>();
}
