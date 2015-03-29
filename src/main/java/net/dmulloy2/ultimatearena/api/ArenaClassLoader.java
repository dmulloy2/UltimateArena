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
package net.dmulloy2.ultimatearena.api;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dmulloy2
 */

public class ArenaClassLoader extends URLClassLoader
{
	private final ArenaLoader loader;
	private final Map<String, Class<?>> classes;

	public ArenaClassLoader(ArenaLoader loader, URL[] urls, ClassLoader parent)
	{
		super(urls, parent);
		this.loader = loader;
		this.classes = new HashMap<>();
	}

	@Override
	public final Class<?> findClass(String name) throws ClassNotFoundException
	{
		return findClass(name, true);
	}

	public final Class<?> findClass(String name, boolean global) throws ClassNotFoundException
	{
		Class<?> result = classes.get(name);

		if (result == null)
		{
			if (global)
			{
				result = loader.getClassByName(name);
			}

			if (result == null)
			{
				result = super.findClass(name);
			}

			classes.put(name, result);
		}

		return result;
	}
}
