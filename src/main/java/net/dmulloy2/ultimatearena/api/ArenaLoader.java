/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.dmulloy2.ultimatearena.arenas.Arena;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.InvalidDescriptionException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import com.google.common.collect.Maps;

/**
 * @author dmulloy2
 */

public class ArenaLoader
{
	private final Yaml yaml;
	private final Map<String, Class<?>> classes;
	private final Map<String, ArenaClassLoader> loaders;

	public ArenaLoader()
	{
		this.yaml = new Yaml(new SafeConstructor());
		this.classes = Maps.newHashMap();
		this.loaders = Maps.newHashMap();
	}

	public final Entry<ArenaDescriptionFile, Class<? extends Arena>> loadArenaClass(File file) throws Exception
	{
		Validate.notNull(file, "File cannot be null");

		if (! file.exists())
		{
			throw new FileNotFoundException(file.getPath() + " does not exist");
		}

		ArenaDescriptionFile description;

		try
		{
			description = getArenaDescription(file);
		}
		catch (InvalidDescriptionException e)
		{
			throw new Exception(e);
		}

		ArenaClassLoader loader = loadClasses(description.getName(), file);
		return new AbstractMap.SimpleEntry<ArenaDescriptionFile, Class<? extends Arena>>(description, Class.forName(description.getMain(),
				true, loader).asSubclass(Arena.class));
	}

	private final ArenaClassLoader loadClasses(String key, File file) throws Exception
	{
		ArenaClassLoader loader = null;

		URL[] urls = new URL[1];
		urls[0] = file.toURI().toURL();

		loader = new ArenaClassLoader(this, urls, getClass().getClassLoader());

		loaders.put(key, loader);

		return loader;
	}

	public final ArenaDescriptionFile getArenaDescription(File file) throws InvalidDescriptionException
	{
		Validate.notNull(file, "File cannot be null");

		JarFile jar = null;
		InputStream stream = null;

		try
		{
			jar = new JarFile(file);
			JarEntry entry = jar.getJarEntry("plugin.yml");

			if (entry == null)
				throw new InvalidDescriptionException(new FileNotFoundException("Jar does not contain plugin.yml"));

			stream = jar.getInputStream(entry);

			Map<?, ?> map = (Map<?, ?>) yaml.load(stream);

			String name = (String) map.get("name");
			Validate.notNull(name, "Name cannot be null");

			if (! name.matches("^[A-Za-z0-9 _.-]+$"))
				throw new Exception("Name '" + name + "' contains invalid characters");

			String main = (String) map.get("main");
			Validate.notNull(main, "Main class cannot be null");

			String version = (String) map.get("version");
			Validate.notNull(version, "Version cannot be null");

			String author = (String) map.get("author");
			if (author == null)
				author = "";

			return new ArenaDescriptionFile(name, main, version, author);
		}
		catch (Exception e)
		{
			throw new InvalidDescriptionException(e);
		}
		finally
		{
			try
			{
				jar.close();
			}
			catch (Exception e)
			{
			}

			try
			{
				stream.close();
			}
			catch (Exception e)
			{
			}
		}
	}

	public final Class<?> getClassByName(String name)
	{
		Class<?> cachedClass = classes.get(name);

		if (cachedClass == null)
		{
			for (String current : loaders.keySet())
			{
				ArenaClassLoader loader = loaders.get(current);

				try
				{
					cachedClass = loader.findClass(name, false);
				}
				catch (ClassNotFoundException e)
				{
				}
			}
		}

		return cachedClass;
	}

	public final void setClass(String name, Class<?> clazz)
	{
		if (! classes.containsKey(name))
		{
			classes.put(name, clazz);

			if (ConfigurationSerializable.class.isAssignableFrom(clazz))
			{
				Class<? extends ConfigurationSerializable> serializable = clazz.asSubclass(ConfigurationSerializable.class);
				ConfigurationSerialization.registerClass(serializable);
			}
		}
	}
}