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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.dmulloy2.io.Closer;
import net.dmulloy2.ultimatearena.UltimateArena;

import org.apache.commons.lang.Validate;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

/**
 * @author dmulloy2
 */

public class ArenaLoader
{
	private final Yaml yaml;
	private final UltimateArena plugin;
	private final Map<String, Class<?>> classes;
	private final Map<String, ArenaClassLoader> loaders;

	public ArenaLoader(UltimateArena plugin)
	{
		this.plugin = plugin;
		this.yaml = new Yaml(new SafeConstructor());
		this.classes = new HashMap<>();
		this.loaders = new HashMap<>();
	}

	protected final ArenaType loadArenaType(File file) throws InvalidArenaException
	{
		Validate.notNull(file, "file cannot be null!");
		if (! file.exists())
			throw new InvalidArenaException(new FileNotFoundException(file.getPath() + " does not exist"));

		ArenaDescription description = getArenaDescription(file);
		ArenaClassLoader loader;

		try
		{
			loader = loadClasses(description.getName(), file);
		}
		catch (MalformedURLException ex)
		{
			throw new InvalidArenaException("Failed to load classes from file " + file.getName(), ex);
		}

		Class<?> jarClass;

		try
		{
			jarClass = Class.forName(description.getMain(), true, loader);
		}
		catch (ClassNotFoundException ex)
		{
			throw new InvalidArenaException("Cannot find main class '" + description.getMain() + "'", ex);
		}

		Class<? extends ArenaType> clazz;

		try
		{
			clazz = jarClass.asSubclass(ArenaType.class);
		}
		catch (ClassCastException ex)
		{
			throw new InvalidArenaException("Main class '" + jarClass.getName() + "' does not extend ArenaType", ex);
		}

		try
		{
			ArenaType type = clazz.newInstance();
			type.initialize(plugin, description, loader, file, new File(plugin.getDataFolder(), type.getName()));
			return type;
		}
		catch (Throwable ex)
		{
			throw new InvalidArenaException("Failed to create and initialize instance", ex);
		}
	}

	private final ArenaClassLoader loadClasses(String key, File file) throws MalformedURLException
	{
		Validate.notNull(key, "key cannot be null!");
		Validate.notNull(file, "file cannot be null!");

		ArenaClassLoader loader = null;

		URL[] urls = new URL[1];
		urls[0] = file.toURI().toURL();

		loader = new ArenaClassLoader(this, urls, getClass().getClassLoader());
		loaders.put(key, loader);
		return loader;
	}

	public final ArenaDescription getArenaDescription(File file) throws InvalidArenaException
	{
		Validate.notNull(file, "file cannot be null!");

		try (Closer closer = new Closer())
		{
			JarFile jar = closer.register(new JarFile(file));
			JarEntry entry = jar.getJarEntry("arena.yml");

			if (entry == null)
				throw new InvalidArenaException(new FileNotFoundException("Jar does not contain arena.yml"));

			InputStream stream = closer.register(jar.getInputStream(entry));
			Map<?, ?> map = (Map<?, ?>) yaml.load(stream);

			String name = (String) map.get("name");
			Validate.notNull(name, "Name cannot be null");
			Validate.isTrue(name.matches("^[A-Za-z0-9 _.-]+$"), "Name '" + name + "' contains invalid chargacters");

			String main = (String) map.get("main");
			Validate.notNull(main, "Main class cannot be null");

			String version = (String) map.get("version");
			Validate.notNull(version, "Version cannot be null");

			String author = (String) map.get("author");
			if (author == null)
				author = "Unascribed";

			String stylized = (String) map.get("stylized");
			if (stylized == null)
				stylized = name;

			return new ArenaDescription(name, main, stylized, version, author);
		}
		catch (InvalidArenaException ex)
		{
			throw ex;
		}
		catch (Throwable ex)
		{
			throw new InvalidArenaException("Failed to read arena.yml from " + file.getName(), ex);
		}
	}

	public final Class<?> getClassByName(String name)
	{
		Validate.notNull(name, "name cannot be null!");

		Class<?> cachedClass = classes.get(name);
		if (cachedClass == null)
		{
			for (String current : loaders.keySet())
			{
				ArenaClassLoader loader = loaders.get(current);

				try
				{
					cachedClass = loader.findClass(name, false);
				} catch (ClassNotFoundException ex) { }
			}
		}

		return cachedClass;
	}
}