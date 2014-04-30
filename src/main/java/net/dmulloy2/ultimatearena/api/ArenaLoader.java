package net.dmulloy2.ultimatearena.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.dmulloy2.ultimatearena.UltimateArena;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import com.google.common.collect.Maps;

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
		this.classes = Maps.newHashMap();
		this.loaders = Maps.newHashMap();
	}

	public final ArenaType loadArenaType(File file) throws Exception
	{
		Validate.notNull(file, "File cannot be null");

		if (! file.exists())
		{
			throw new FileNotFoundException(file.getPath() + " does not exist");
		}

		ArenaDescriptionFile description = getArenaDescription(file);
		ArenaClassLoader loader = loadClasses(description.getName(), file);

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
			throw new InvalidArenaException("Main class '" + description.getMain() + "' does not extend ArenaType", ex);
		}

		ArenaType type = clazz.newInstance();
		type.initialize(plugin, description, loader, file, new File(plugin.getDataFolder(), type.getName()));
		return type;
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

	public final ArenaDescriptionFile getArenaDescription(File file) throws InvalidArenaException
	{
		Validate.notNull(file, "File cannot be null");

		JarFile jar = null;
		InputStream stream = null;

		try
		{
			jar = new JarFile(file);
			JarEntry entry = jar.getJarEntry("arena.yml");

			if (entry == null)
				throw new InvalidArenaException(new FileNotFoundException("Jar does not contain arena.yml"));

			stream = jar.getInputStream(entry);

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
				author = "";

			String stylized = (String) map.get("stylized");
			if (stylized == null)
				stylized = name;

			return new ArenaDescriptionFile(name, main, stylized, version, author);
		}
		catch (Exception e)
		{
			throw new InvalidArenaException(e);
		}
		finally
		{
			try
			{
				jar.close();
			} catch (Throwable ex) { }

			try
			{
				stream.close();
			} catch (Throwable ex) { }
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
				} catch (ClassNotFoundException e) { }
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