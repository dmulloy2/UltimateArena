/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.NumberUtil;
import net.dmulloy2.util.Util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * @author dmulloy2
 */

public abstract class Configuration implements ConfigurationSerializable, Reloadable
{
	protected Object get(Map<String, Object> map, String key, Object def)
	{
		for (Entry<String, Object> entry : map.entrySet())
		{
			if (entry.getKey().equalsIgnoreCase(key))
				return entry.getValue();
		}

		return def;
	}

	protected boolean isSet(Map<String, Object> map, String key)
	{
		return get(map, key, null) != null;
	}

	protected String getString(Map<String, Object> map, String key, String def)
	{
		return get(map, key, def).toString();
	}

	protected int getInt(Map<String, Object> map, String key, int def)
	{
		return NumberUtil.toInt(get(map, key, def));
	}

	protected double getDouble(Map<String, Object> map, String key, double def)
	{
		return NumberUtil.toDouble(get(map, key, def));
	}

	protected boolean getBoolean(Map<String, Object> map, String key, boolean def)
	{
		return Util.toBoolean(get(map, key, def));
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> getList(Map<String, Object> map, String key, List<T> def)
	{
		List<T> list = null;

		for (Entry<String, Object> entry : map.entrySet())
		{
			if (entry.getKey().equalsIgnoreCase(key))
			{
				list = (List<T>) entry.getValue();
				break;
			}
		}

		return list != null ? list : def;
	}

	@SuppressWarnings("unchecked")
	protected List<String> getStringList(Map<String, Object> map, String key)
	{
		List<String> list = (List<String>) get(map, key, null);
		return list != null ? list : new ArrayList<String>();
	}

	protected Map<String, Object> getSection(Map<String, Object> map, String key)
	{
		ConfigurationSection section = (ConfigurationSection) get(map, key, null);
		return section != null ? section.getValues(false) : new HashMap<String, Object>();
	}
}