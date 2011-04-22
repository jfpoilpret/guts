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

package net.guts.binding;

import net.guts.properties.Bean;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.BufferedValueModel;
import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.binding.value.ValueModel;

//TODO javadoc + javdoc refs to JGoodies PM
public class GutsPresentationModel<B>
{
	public GutsPresentationModel(
		Class<B> clazz, ValueModel<B> beanChannel, ValueModel<Boolean> trigger)
	{
		_helper = Bean.create(clazz);
		_pm = new PresentationModel<B>(beanChannel, trigger);
	}

	public ValueModel<B> getBeanChannel()
	{
		return _pm.getBeanChannel();
	}

	public B of()
	{
		return _helper.mock();
	}
	
	public <T> ValueModel<T> getPropertyModel(T getter)
	{
		return _pm.getModel(_helper.property(getter).name());
	}

	public <T> ComponentValueModel<T> getComponentModel(T getter)
	{
		return _pm.getComponentModel(_helper.property(getter).name());
	}
	
	public <T> BufferedValueModel<T> getBufferedModel(T getter)
	{
		return _pm.getBufferedModel(_helper.property(getter).name());
	}

	public <T> ComponentValueModel<T> getBufferedComponentModel(T getter)
	{
		return _pm.getBufferedComponentModel(_helper.property(getter).name());
	}
	
	public ValueModel<Boolean> getTriggerChannel()
	{
		return _pm.getTriggerChannel();
	}

	public void setTriggerChannel(ValueModel<Boolean> newTriggerChannel)
	{
		_pm.setTriggerChannel(newTriggerChannel);
	}
	
	public void triggerCommit()
	{
		_pm.triggerCommit();
	}
	
	public void triggerFlush()
	{
		_pm.triggerFlush();
	}
	
	public boolean isBuffering()
	{
		return _pm.isBuffering();
	}
	
	public boolean isChanged()
	{
		return _pm.isChanged();
	}
	
	public void resetChanged()
	{
		_pm.resetChanged();
	}
	
	public void observeChanged(ValueModel<?> valueModel)
	{
		_pm.observeChanged(valueModel);
	}

	public void retractInterestFor(ValueModel<?> valueModel)
	{
		_pm.retractInterestFor(valueModel);
	}

	public void release()
	{
		_pm.release();
	}

	protected PresentationModel<B> getSourceModel()
	{
		return _pm;
	}

	final private Bean<B> _helper;
	final private PresentationModel<B> _pm;
}
