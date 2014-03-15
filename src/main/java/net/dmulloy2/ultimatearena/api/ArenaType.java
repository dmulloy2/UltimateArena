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
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.creation.ArenaCreator;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

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
	public void onLoad() { }

	public void onEnable() { }

	public void onDisable() { }

	public ArenaZone getArenaZone(UltimateArena plugin, File file)
	{
		return new ArenaZone(plugin, file);
	}

	public ArenaConfig newConfig()
	{
		return new ArenaConfig(getPlugin(), getName().toLowerCase(), new File(getDataFolder(), "config.yml"));
	}

	// ---- Required Hooks
	public abstract ArenaCreator newCreator(Player player, String name, UltimateArena plugin);

	public abstract Arena newArena(ArenaZone az);
	
	
	public final String getName()
	{
		return description.getName();
	}

	public final void initialize(UltimateArena plugin, ArenaDescriptionFile description, ArenaClassLoader classLoader, File file, File dataFolder)
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

	public final void saveDefaultConfig()
	{
		File file = new File(dataFolder, "config.yml");
		if (! file.exists())
			saveResource("config.yml", false);
	}

	protected ArenaConfig config;

	public final ArenaConfig getConfig()
	{
		reloadConfig();
		return config;
	}

	public final void reloadConfig()
	{
		if (config == null)
		{
			saveDefaultConfig();
			config = newConfig();
		}
	}

	// ---- Credit to Bukkit for these methods from JavaPlugin

	public final void saveResource(String resourcePath, boolean replace)
	{
		if (resourcePath == null || resourcePath.equals(""))
		{
			throw new IllegalArgumentException("ResourcePath cannot be null or empty");
		}

		resourcePath = resourcePath.replace('\\', '/');
		InputStream in = getResource(resourcePath);
		if (in == null)
		{
			throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + file);
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
			if (!outFile.exists() || replace)
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
			else
			{
				logger.log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName()
						+ " already exists.");
			}
		}
		catch (IOException ex)
		{
			logger.log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
		}
	}

	public final InputStream getResource(String filename)
	{
		if (filename == null)
		{
			throw new IllegalArgumentException("Filename cannot be null");
		}

		try
		{
			URL url = getClassLoader().getResource(filename);

			if (url == null)
			{
				return null;
			}

			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
			return connection.getInputStream();
		}
		catch (IOException ex)
		{
			return null;
		}
	}
}