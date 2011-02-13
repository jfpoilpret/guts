package net.guts.gui.dialog2.template.wizard;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.guts.gui.resource.AbstractCompoundResourceConverter;
import net.guts.gui.resource.ResourceConverter;
import net.guts.gui.resource.ResourceEntry;

import com.google.inject.TypeLiteral;

public class MapConverter<K, V> extends AbstractCompoundResourceConverter<Map<K, V>>
{
	MapConverter(TypeLiteral<K> keyType, TypeLiteral<V> valueType)
	{
		_keyType = keyType;
		_valueType = valueType;
	}
	
	@Override public Map<K, V> convert(ResourceEntry entry)
	{
		Map<K, V> map = new HashMap<K, V>();
		ResourceConverter<K> keyConverter = converter(_keyType);
		ResourceConverter<V> valueConverter = converter(_valueType);
		Matcher matcher = _regexp.matcher(entry.value());
		while (matcher.find())
		{
			K key = keyConverter.convert(entry.derive(matcher.group(1)));
			V value = valueConverter.convert(entry.derive(matcher.group(2)));
			map.put(key, value);
		}
		return map;
	}	

	final private Pattern _regexp = Pattern.compile("\\[([^:]+):([^\\]]*)\\] *");
	final private TypeLiteral<K> _keyType;
	final private TypeLiteral<V> _valueType;
}
