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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.swornapi.io.Closer;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaCreator;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import net.dmulloy2.swornapi.util.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * Represents an arena type.
 *
 * @author dmulloy2
 */

@Getter
public abstract class ArenaType
{
	private boolean initialized;

	private File file;
	private File dataFolder;
	private ArenaLogger logger;
	private UltimateArena plugin;
	private ArenaClassLoader classLoader;
	private ArenaDescription description;

	// Base Constructor
	public ArenaType() { }

	// ---- Optional Hooks

	/**
	 * Called when this ArenaType is enabled.
	 */
	public void onEnable() { }

	/**
	 * Called when this ArenaType is disabled.
	 */
	public void onDisable() { }

	/**
	 * Called when UltimateArena is reloaded.
	 */
	public void onReload() { }

	/**
	 * Gets the {@link ArenaZone} associated with this ArenaType. Will return a
	 * default ArenaZone if this method is not overriden.
	 *
	 * @param file Data file
	 * @return The ArenaZone
	 */
	public ArenaZone getArenaZone(File file)
	{
		return new ArenaZone(this, file);
	}

	/**
	 * Gets the {@link ArenaConfig} associated with this ArenaType. Will return
	 * a default ArenaConfig if this method is not overriden.
	 *
	 * @return The ArenaConfig
	 */
	public ArenaConfig newConfig()
	{
		String name = getName().toLowerCase();
		return new ArenaConfig(plugin, name, new File(dataFolder, "config.yml"));
	}

	/**
	 * Gets the {@link ArenaConfig} associated with this ArenaType for an arena.
	 * Will return a default ArenaConfig if this method is not overriden.
	 *
	 * @param az Arena to get the config for
	 * @return The ArenaConfig
	 */
	public ArenaConfig newConfig(ArenaZone az)
	{
		return new ArenaConfig(az);
	}

	// ---- Required Hooks

	/**
	 * Gets the {@link ArenaCreator} associated with this ArenaType. Must be
	 * overriden.
	 *
	 * @param player {@link Player} creating the arena
	 * @param name Name of the arena
	 * @return The ArenaCreator
	 */
	public abstract ArenaCreator newCreator(Player player, String name);

	/**
	 * Gets the {@link Arena} associated with this ArenaType. Must be overriden.
	 *
	 * @param az Underlying {@link ArenaZone}
	 * @return The Arena
	 */
	public abstract Arena newArena(ArenaZone az);

	/**
	 * Gets the name of this ArenaType.
	 *
	 * @return The name of this ArenaType
	 */
	public final String getName()
	{
		return getDescription().getName();
	}

	/**
	 * Gets the stylized name of this ArenaType.
	 *
	 * @return The stylized name of this ArenaType
	 */
	public final String getStylizedName()
	{
		return getDescription().getStylized();
	}

	final void initialize(UltimateArena plugin, ArenaDescription description, ArenaClassLoader classLoader, File file)
	{
		Validate.isTrue(! initialized, "Already initialized!");

		this.plugin = plugin;
		this.description = description;
		this.classLoader = classLoader;
		this.file = file;
		this.logger = new ArenaLogger(this);

		File types = new File(plugin.getDataFolder(), "types");
		this.dataFolder = new File(types, getName().toLowerCase());

		this.initialized = true;
	}

	// ---- Configuration

	/**
	 * Loads this type's config. If the config doesn't exist, the default config
	 * is saved.
	 *
	 * @return The loaded config
	 */
	public final ArenaConfig loadConfig()
	{
		saveDefaultConfig();
		return getConfig();
	}

	protected ArenaConfig config;

	/**
	 * Gets this type's configuration.
	 *
	 * @return This type's configuration
	 */
	public final ArenaConfig getConfig()
	{
		if (config == null)
		{
			try
			{
				saveDefaultConfig();
			}
			catch (Throwable ex)
			{
				getLogger().log(Level.WARNING, "Failed to save default configuration: ", ex);
			}

			config = newConfig();
		}

		return config;
	}

	/**
	 * Saves this type's default config.
	 */
	public void saveDefaultConfig()
	{
		File file = new File(dataFolder, "config.yml");
		if (! file.exists())
			saveResource("config.yml", false);
	}

	/**
	 * Saves this type's default config
	 *
	 * @param defaultType if this is a default arena type
	 * @deprecated Meaning is misleading
	 * @see #saveDefaultConfig()
	 * @see #saveDefaultJarConfig()
	 */
	@Deprecated
	public void saveDefaultConfig(boolean defaultType)
	{
		if (defaultType)
			saveDefaultJarConfig();
		else
			saveDefaultConfig();
	}

	/**
	 * Saves this type's default config from the UltimateArena jar.
	 * <p>
	 * Note: This should only be called by default types.
	 */
	protected void saveDefaultJarConfig()
	{
		String name = getName().toLowerCase();
		InputStream in = plugin.getResource("configs/" + name + "Config.yml");
		if (in == null)
			throw new IllegalStateException("saveDefaultJarConfig() can only be called by default types!");

		File outFile = new File(dataFolder, "config.yml");
		if (! outFile.exists())
			saveResource(in, outFile);
	}

	/**
	 * Reloads this type's configuration.
	 */
	public final void reloadConfig()
	{
		if (config != null)
			config.reload();
	}

	// ---- Utility Methods (Credit to Bukkit for the resource methods)

	/**
	 * Saves the raw contents of any resource embedded with a plugin's .jar file
	 * assuming it can be found using {@link #getResource(String)}.<br>
	 * The resource is saved into the plugin's data folder using the same
	 * hierarchy as the .jar file (subdirectories are preserved).
	 *
	 * @param resourcePath the embedded resource path to look for within the
	 *        plugin's .jar file. (No preceding slash).
	 * @param replace if true, the embedded resource will overwrite the contents
	 *        of an existing file.
	 * @throws IllegalArgumentException if the resource path is empty or
	 *         points to a nonexistent resource.
	 */
	protected final void saveResource(String resourcePath, boolean replace)
	{
		Validate.notEmpty(resourcePath, "resourcePath cannot be empty!");

		File outFile = null;
		try (Closer closer = new Closer())
		{
			resourcePath = resourcePath.replace('\\', '/');
			InputStream in = closer.register(getResource(resourcePath));
			if (in == null)
			{
				logger.log(Level.WARNING, "The embedded resource '" + resourcePath + "' cannot be found in " + file);
				return;
			}

			outFile = new File(dataFolder, resourcePath);
			int lastIndex = resourcePath.lastIndexOf('/');
			File outDir = new File(dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

			if (! outDir.exists())
			{
				outDir.mkdirs();
			}

			if (! outFile.exists() || replace)
			{
				OutputStream out = closer.register(new FileOutputStream(outFile));
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0)
				{
					out.write(buf, 0, len);
				}
			}
		}
		catch (IOException ex)
		{
			logger.log(Level.SEVERE, "Could not save " + resourcePath + " to " + outFile, ex);
		}
	}

	private void saveResource(InputStream in, File outFile)
	{
		try (Closer closer = new Closer())
		{
			closer.register(in);
			if (! outFile.exists())
			{
				OutputStream out = closer.register(new FileOutputStream(outFile));
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0)
				{
					out.write(buf, 0, len);
				}
			}
		}
		catch (IOException ex)
		{
			logger.log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
		}
	}

	/**
	 * Gets an embedded resource in this arena type's jar file.
	 *
	 * @param fileName Name of the resource
	 * @return File, or null if it cannot be found
	 */
	protected final InputStream getResource(String fileName)
	{
		try
		{
			URL url = getClassLoader().getResource(fileName);
			if (url == null)
				return null;

			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
			return connection.getInputStream();
		} catch (Throwable ignored) { }
		return null;
	}

	/**
	 * Registers a {@link Listener} via UltimateArena
	 *
	 * @param listener {@link Listener} to register
	 * @throws IllegalArgumentException if listener is null
	 */
	protected final void registerListener(Listener listener)
	{
		Validate.notNull(listener, "listener cannot be null!");
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}

	// ---- Generic Methods

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this) return true;
		
		if (obj instanceof ArenaType)
		{
			ArenaType that = (ArenaType) obj;
			return Objects.equals(getName(), that.getName());
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getName());
	}

	@Override
	public String toString()
	{
		return getDescription().getFullName() + " by " + getDescription().getAuthor();
	}
}
