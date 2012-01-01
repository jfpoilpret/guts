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

package net.guts.gui.application.docking;

import java.io.IOException;

import org.flexdock.docking.state.PersistenceException;
import org.flexdock.perspective.Perspective;
import org.flexdock.perspective.persist.PersistenceHandler;
import org.flexdock.perspective.persist.Persister;
import org.flexdock.perspective.persist.PerspectiveModel;

import net.guts.gui.session.SessionManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class SessionPersistenceHandler implements PersistenceHandler
{
	@Inject SessionPersistenceHandler(SessionManager sessionManager)
	{
		_sessionManager = sessionManager;
	}
	
	@Override public Persister createDefaultPersister()
	{
		return null;
	}

	@Override public PerspectiveModel load(String key)
		throws IOException, PersistenceException
	{
		PerspectiveModel model = new PerspectiveModel(null, null, PERSPECTIVES);
		_sessionManager.restore(prepareKey(key), model);
		// Return null if no such key in Session Storage
		if (model.getDefaultPerspective() != null)
		{
			return model;
		}
		else
		{
			return null;
		}
	}

	@Override public boolean store(String key, PerspectiveModel model)
		throws IOException, PersistenceException
	{
		_sessionManager.save(prepareKey(key), model);
		return true;
	}
	
	private String prepareKey(String key)
	{
		if (key == null || key.trim().length() == 0)
		{
			return KEY_PREFIX;
		}
		else
		{
			return KEY_PREFIX + "-" + key.trim();
		}
	}

	static final private String KEY_PREFIX = "flexdock";
	static final private Perspective[] PERSPECTIVES = {};
	final private SessionManager _sessionManager;
}
