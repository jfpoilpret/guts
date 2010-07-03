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

package net.guts.gui.session;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class PrefsStorageMedium implements StorageMedium
{
	static final private Logger _logger = LoggerFactory.getLogger(PrefsStorageMedium.class);

	@Inject
	PrefsStorageMedium(@BindSessionApplication Class<?> applicationNode)
	{
		_root = Preferences.userNodeForPackage(applicationNode);
	}

	@Override public String checkName(String name)
	{
		if (name.length() > Preferences.MAX_KEY_LENGTH)
		{
			return KEY_LENGTH_ERROR;
		}
		else
		{
			return null;
		}
	}
	
	@Override public byte[] load(String name)
	{
		byte[] content = restoreAndMergeIfNeeded(name);
		if (content == null)
		{
			_logger.debug("load({}): nothing found in Backing Store.", name);
		}
		return content;
	}
	
	private byte[] restoreAndMergeIfNeeded(String name)
	{
		// First try to get content in one, whole, piece
		byte[] content = _root.getByteArray(name, null);
		if (content != null)
		{
			return content;
		}

		// Try to get indexed content instead
		List<byte[]> chunks = new ArrayList<byte[]>();
		int index = 1;
		while (true)
		{
			byte[] chunk = _root.getByteArray(name + index, null);
			if (chunk == null)
			{
				break;
			}
			chunks.add(chunk);
			index++;
		}
		// Was there indexed content?
		if (chunks.isEmpty())
		{
			return null;
		}
		// Calculate the whole content size
		int size = 0;
		for (byte[] chunk: chunks)
		{
			size += chunk.length;
		}
		// Merge the whole content
		content = new byte[size];
		int location = 0;
		for (byte[] chunk: chunks)
		{
			System.arraycopy(chunk, 0, content, location, chunk.length);
			location += chunk.length;
		}
		return content;
	}

	@Override public void save(String name, byte[] content)
	{
		try
		{
			storeAndSplitIfNeeded(name, content);
			_root.flush();
		}
		catch (BackingStoreException e)
		{
			_logger.warn("Error saving state id " + name, e);
		}
	}
	
	private void storeAndSplitIfNeeded(String name, byte[] content)
	{
		if (content.length <= MAX_LENGTH)
		{
			_root.putByteArray(name, content);
		}
		else
		{
			int index = 1;
			int location = 0;
			while (location < content.length)
			{
				int size = Math.min(MAX_LENGTH, content.length - location);
				byte[] chunk = new byte[size];
				System.arraycopy(content, location, chunk, 0, size);
				_root.putByteArray(name + index, chunk);
				location += size;
				index++;
			}
		}
	}

	// Maximum value length is 3/4 of actual prefs limit, due to Base64 encoding
	static final private int MAX_LENGTH = Preferences.MAX_VALUE_LENGTH * 3 / 4;
	static final private String KEY_LENGTH_ERROR = String.format(
		"java.util.prefs package allows key names of max %d characters.",
		Preferences.MAX_KEY_LENGTH);
	final private Preferences _root;
}
