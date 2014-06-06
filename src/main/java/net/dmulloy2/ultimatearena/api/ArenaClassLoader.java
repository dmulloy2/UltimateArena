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