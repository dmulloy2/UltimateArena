package net.dmulloy2.ultimatearena.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import lombok.Getter;
import lombok.NonNull;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.creation.ArenaCreator;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
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
	private ArenaDescriptionFile description;

	public ArenaType()
	{
		//
	}

	// ---- Optional Hooks

	/**
	 * Called when this ArenaType is loaded
	 */
	public void onLoad() { }

	/**
	 * Called when this ArenaType is enabled
	 */
	public void onEnable() { }

	/**
	 * Called when this ArenType is disabled
	 */
	public void onDisable() { }

	/**
	 * Called when UltimateArena is reloaded
	 */
	public void onReload() { }

	/**
	 * Gets the {@link ArenaZone} associated with this ArenaType. Will return a
	 * default ArenaZone if this method is not overriden.
	 * 
	 * @param plugin
	 *        - UltimateArena instance
	 * @param file
	 *        - Data file
	 */
	public ArenaZone getArenaZone(UltimateArena plugin, File file)
	{
		return new ArenaZone(plugin, file);
	}

	/**
	 * Gets the {@link ArenaConfig} associated with this ArenaType. Will return
	 * a default ArenaConfig if this method is not overriden.
	 */
	public ArenaConfig newConfig()
	{
		return new ArenaConfig(getPlugin(), getName().toLowerCase(), new File(getDataFolder(), "config.yml"));
	}

	// ---- Required Hooks

	/**
	 * Gets the {@link ArenaCreator} associated with this ArenaType. Must be
	 * overriden.
	 * 
	 * @param player
	 *        - {@link Player} creating the arena
	 * @param name
	 *        - Name of the arena
	 * @param plugin
	 *        - UltimateArena instance
	 */
	public abstract ArenaCreator newCreator(Player player, String name, UltimateArena plugin);

	/**
	 * Gets the {@link Arena} associated with this ArenaType. Must be overriden.
	 * 
	 * @param az
	 *        - Underlying {@link ArenaZone}
	 */
	public abstract Arena newArena(ArenaZone az);

	/**
	 * @return The name of this ArenaType
	 */
	public final String getName()
	{
		return description.getName();
	}

	/**
	 * @return The stylized name of this ArenaType
	 */
	public final String getStylizedName()
	{
		return description.getStylized();
	}

	protected final void initialize(UltimateArena plugin, ArenaDescriptionFile description, ArenaClassLoader classLoader, File file,
			File dataFolder)
	{
		Validate.isTrue(! initialized, "Already initialized!");

		this.plugin = plugin;
		this.description = description;
		this.classLoader = classLoader;
		this.file = file;
		this.dataFolder = dataFolder;
		this.logger = new ArenaLogger(this);
		this.initialized = true;
	}

	// ---- Configuration

	/**
	 * Saves the default config for this ArenaType
	 */
	public final void saveDefaultConfig()
	{
		File file = new File(dataFolder, "config.yml");
		if (! file.exists())
			saveResource("config.yml", false);
	}

	protected ArenaConfig config;

	/**
	 * @return The config
	 */
	public final ArenaConfig getConfig()
	{
		if (config == null)
		{
			saveDefaultConfig();
			config = newConfig();
		}

		return config;
	}

	/**
	 * Reloads the config
	 */
	public final void reloadConfig()
	{
		if (config != null)
			config.reload();
	}

	// ---- Utility Methods (Credit to Bukkit for the resource methods)

	/**
	 * Saves the raw contents of any resource embedded with a plugin's .jar file
	 * assuming it can be found using {@link #getResource(String)}.
	 * <p>
	 * The resource is saved into the plugin's data folder using the same
	 * hierarchy as the .jar file (subdirectories are preserved).
	 *
	 * @param resourcePath
	 *        the embedded resource path to look for within the plugin's .jar
	 *        file. (No preceding slash).
	 * @param replace
	 *        if true, the embedded resource will overwrite the contents of an
	 *        existing file.
	 * @throws IllegalArgumentException
	 *         if the resource path is null, empty, or points to a nonexistent
	 *         resource.
	 */
	protected final void saveResource(@NonNull String resourcePath, boolean replace)
	{
		Validate.notEmpty(resourcePath, "resourcePath cannot be empty!");

		resourcePath = resourcePath.replace('\\', '/');
		InputStream in = getResource(resourcePath);
		if (in == null)
		{
			logger.log(Level.WARNING, "The embedded resource '" + resourcePath + "' cannot be found in " + file);
		}

		File outFile = new File(dataFolder, resourcePath);
		int lastIndex = resourcePath.lastIndexOf('/');
		File outDir = new File(dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

		if (! outDir.exists())
		{
			outDir.mkdirs();
		}

		try
		{
			if (! outFile.exists() || replace)
			{
				OutputStream out = new FileOutputStream(outFile);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0)
				{
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
			}
		}
		catch (IOException ex)
		{
			logger.log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
		}
	}

	/**
	 * Gets an embedded resource in this plugin
	 *
	 * @param filename
	 *        Filename of the resource
	 * @return File if found, otherwise null
	 */
	protected final InputStream getResource(String filename)
	{
		Validate.notNull(filename, "Filename cannot be null!");

		try
		{
			URL url = getClassLoader().getResource(filename);
			if (url == null)
				return null;

			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
			return connection.getInputStream();
		} catch (Throwable ex) { }
		return null;
	}

	/**
	 * Registers an Event {@link Listener} via UltimateArena
	 * 
	 * @param listener
	 *        - {@link Listener} to register
	 */
	protected final void registerListener(Listener listener)
	{
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}
}