package net.guts.binding;

import java.util.prefs.Preferences;

import net.guts.properties.Bean;
import net.guts.properties.Property;

import com.jgoodies.binding.adapter.PreferencesAdapter;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.value.BufferedValueModel;
import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;

//TODO - Bindings extensions for some type-unsafe API?
public class Models
{
	static public <B> GutsPresentationModel<B> createPM(Class<B> clazz)
	{
		return new GutsPresentationModel<B>(clazz);
	}
	
	static public <B> GutsPresentationModel<B> createPM(
		Class<B> clazz, ValueModel<Boolean> trigger)
	{
		return new GutsPresentationModel<B>(clazz, trigger);
	}
	
	static public <T> ValueModel<T> holder()
	{
		return new ValueHolder<T>();
	}
	
	static public <T> ValueModel<T> holderFor(T value)
	{
		return new ValueHolder<T>(value);
	}
	
	static public <T> BufferedValueModel<T> bufferedModelFor(ValueModel<T> value, ValueModel<Boolean> trigger)
	{
		return new BufferedValueModel<T>(value, trigger);
	}
	
	static public <T> ComponentValueModel<T> componentModelFor(ValueModel<T> value)
	{
		return new ComponentValueModel<T>(value);
	}

	static public <B> B of(Class<B> clazz)
	{
		return Bean.create(clazz).mock();
	}
	
	static public <B, T> ValueModel<T> propertyModel(Class<B> bean, T getter)
	{
		Bean<B> helper = Bean.create(bean);
		Property<B, T> property = helper.property(getter);
		return new PropertyAdapter<B, T>((B) null, property.name());
	}
	
	@SuppressWarnings("unchecked") 
	static public <B, T> ValueModel<T> propertyModel(ValueModel<B> beanModel, T getter)
	{
		return propertyModel(beanModel, (Class<B>) beanModel.getValue().getClass(), getter);
	}
	
	static public <B, T> ValueModel<T> propertyModel(ValueModel<B> beanModel, Class<B> clazz, T getter)
	{
		Bean<B> helper = Bean.create(clazz);
		Property<B, T> property = helper.property(getter);
		return new PropertyAdapter<B, T>(beanModel, property.name());
	}
	
	static public ValueModel<Boolean> preferenceModel(Preferences prefs, String key, Boolean defaultValue)
	{
		return new PreferencesAdapter<Boolean>(prefs, key, defaultValue);
	}
	
	static public ValueModel<String> preferenceModel(Preferences prefs, String key, String defaultValue)
	{
		return new PreferencesAdapter<String>(prefs, key, defaultValue);
	}
	
	static public ValueModel<Integer> preferenceModel(Preferences prefs, String key, Integer defaultValue)
	{
		return new PreferencesAdapter<Integer>(prefs, key, defaultValue);
	}
	
	static public ValueModel<Long> preferenceModel(Preferences prefs, String key, Long defaultValue)
	{
		return new PreferencesAdapter<Long>(prefs, key, defaultValue);
	}
	
	static public ValueModel<Float> preferenceModel(Preferences prefs, String key, Float defaultValue)
	{
		return new PreferencesAdapter<Float>(prefs, key, defaultValue);
	}
	
	static public ValueModel<Double> preferenceModel(Preferences prefs, String key, Double defaultValue)
	{
		return new PreferencesAdapter<Double>(prefs, key, defaultValue);
	}
}
