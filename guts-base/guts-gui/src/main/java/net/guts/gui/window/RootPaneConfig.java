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

import javax.swing.JApplet;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.RootPaneContainer;

import net.guts.gui.util.TypeSafeMap;

//CSOFF: AbstractClassName
public class RootPaneConfig<T extends RootPaneContainer>
{
	RootPaneConfig()
	{
	}
	
	static public ConfigBuilder2<JFrame> forFrame()
	{
		return new BoundsAndStateConfigBuilder<JFrame>();
	}
	
	static public ConfigBuilder2<JDialog> forDialog()
	{
		return new BoundsAndStateConfigBuilder<JDialog>();
	}
	
	static public ConfigBuilder1<JApplet> forApplet()
	{
		return new BoundsAndStateConfigBuilder<JApplet>();
	}

//	static public ConfigBuilder<JInternalFrame> forInternalFrame()
//	{
//		return null;
//	}
	
	static public interface ConfigBuilder1<T extends RootPaneContainer>
	{
		public ConfigBuilder1<T> state(StatePolicy state);
		public RootPaneConfig<T> config();
	}
	
	static public interface ConfigBuilder2<T extends RootPaneContainer>
	extends ConfigBuilder1<T>
	{
		public ConfigBuilder2<T> bounds(BoundsPolicy bounds);
		public ConfigBuilder2<T> state(StatePolicy state);
	}
	
	static public class BoundsAndStateConfigBuilder<T extends RootPaneContainer>
	implements ConfigBuilder2<T>
	{
		BoundsAndStateConfigBuilder()
		{
			_config._properties.put(BoundsPolicy.class, BoundsPolicy.PACK_AND_CENTER);
			_config._properties.put(StatePolicy.class, StatePolicy.RESTORE_IF_EXISTS);
		}
		
		public BoundsAndStateConfigBuilder<T> bounds(BoundsPolicy bounds)
		{
			_config._properties.put(BoundsPolicy.class, bounds);
			return this;
		}
		
		public BoundsAndStateConfigBuilder<T> state(StatePolicy state)
		{
			_config._properties.put(StatePolicy.class, state);
			return this;
		}
		
		public RootPaneConfig<T> config()
		{
			return _config;
		}
		
		final private RootPaneConfig<T> _config = new RootPaneConfig<T>();
	}
	
	protected final TypeSafeMap _properties = new TypeSafeMap();
}
//CSON: AbstractClassName
