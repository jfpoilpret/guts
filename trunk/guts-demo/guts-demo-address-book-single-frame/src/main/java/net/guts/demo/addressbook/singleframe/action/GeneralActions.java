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

package net.guts.demo.addressbook.singleframe.action;

import java.util.Locale;

import net.guts.demo.addressbook.singleframe.dialog.PreferencesPanel;
import net.guts.gui.action.GutsAction;
import net.guts.gui.dialog.DialogFactory;
import net.guts.gui.resource.ResourceInjector;
import net.guts.gui.window.BoundsPolicy;
import net.guts.gui.window.StatePolicy;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GeneralActions
{
	@Inject public GeneralActions(ResourceInjector injector)
	{
		_resourceInjector = injector;
	}
	
	public GutsAction throwException()
	{
		return _throwException;
	}
	
	public GutsAction frenchLocale()
	{
		return _french;
	}
	
	public GutsAction englishLocale()
	{
		return _english;
	}
	
	public GutsAction showPreferences(){
	    return _showPreferences;
	}
	
	// Only to demonstrate the exception handling
	final private GutsAction _throwException = new GutsAction()
	{
		@Override protected void perform()
		{
			throw new IllegalArgumentException("Some message here");
		}
	};
	final private GutsAction _french = new GutsAction()
	{
		@Override protected void perform()
		{
			_resourceInjector.setLocale(Locale.FRENCH);
		}
	};
	final private GutsAction _english = new GutsAction()
	{
		@Override protected void perform()
		{
			_resourceInjector.setLocale(Locale.ENGLISH);
		}
	};
	
	final private GutsAction _showPreferences = new GutsAction(){
	    @Override protected void perform() {
	        _dialogFactory.showDialog(PreferencesPanel.class, BoundsPolicy.PACK_AND_CENTER, StatePolicy.RESTORE_IF_EXISTS);
	        
	    }
	};
	
	@Inject private DialogFactory _dialogFactory;
	final private ResourceInjector _resourceInjector;
}
