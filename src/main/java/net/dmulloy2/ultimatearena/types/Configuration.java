/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2012 - 2015 MineSworn
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.ultimatearena.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.swornapi.types.Reloadable;
import net.dmulloy2.swornapi.util.NumberUtil;
import net.dmulloy2.swornapi.util.Util;

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
		return list != null ? list : new ArrayList<>();
	}

	protected Map<String, Object> getSection(Map<String, Object> map, String key)
	{
		ConfigurationSection section = (ConfigurationSection) get(map, key, null);
		return section != null ? section.getValues(false) : new HashMap<>();
	}
}
