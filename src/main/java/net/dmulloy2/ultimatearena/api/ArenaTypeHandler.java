/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.api;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.dmulloy2.types.Reloadable;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.bomb.BombType;
import net.dmulloy2.ultimatearena.arenas.conquest.ConquestType;
import net.dmulloy2.ultimatearena.arenas.ctf.CTFType;
import net.dmulloy2.ultimatearena.arenas.ffa.FFAType;
import net.dmulloy2.ultimatearena.arenas.hunger.HungerType;
import net.dmulloy2.ultimatearena.arenas.infect.InfectType;
import net.dmulloy2.ultimatearena.arenas.koth.KOTHType;
import net.dmulloy2.ultimatearena.arenas.mob.MobType;
import net.dmulloy2.ultimatearena.arenas.pvp.PvPType;
import net.dmulloy2.ultimatearena.arenas.spleef.SpleefType;
import net.dmulloy2.util.Util;

import org.apache.commons.lang.Validate;

import com.google.common.io.Files;

/**
 * @author dmulloy2
 */

public class ArenaTypeHandler implements Reloadable
{
	private final Map<String, ArenaType> arenaTypes;
	private final UltimateArena plugin;

	public ArenaTypeHandler(UltimateArena plugin)
	{
		this.plugin = plugin;
		this.arenaTypes = new HashMap<>();
		this.loadArenaTypes();
	}

	public final ArenaType getArenaType(String name)
	{
		Validate.notEmpty(name, "Name cannot be empty!");

		for (ArenaType type : arenaTypes.values())
		{
			if (type.getName().equalsIgnoreCase(name))
				return type;
		}

		return null;
	}

	public final void loadArenaTypes()
	{
		File directory = new File(plugin.getDataFolder(), "types");
		if (! directory.exists())
			directory.mkdirs();

		ArenaLoader loader = new ArenaLoader(plugin);

		// Add default types
		loadArenaType(new BombType());
		loadArenaType(new ConquestType());
		loadArenaType(new CTFType());
		loadArenaType(new FFAType());
		loadArenaType(new HungerType());
		loadArenaType(new InfectType());
		loadArenaType(new KOTHType());
		loadArenaType(new MobType());
		loadArenaType(new PvPType());
		loadArenaType(new SpleefType());

		// Load custom types
		File[] files = directory.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				return file.getName().endsWith(".jar");
			}
		});

		if (files != null && files.length > 0)
		{
			for (File file : files)
			{
				loadArenaType(loader, file);
			}
		}
	}

	private final void loadArenaType(ArenaType type)
	{
		try
		{
			File types = new File(plugin.getDataFolder(), "types");
			if (! types.exists())
				types.mkdirs();

			File dataFolder = new File(types, type.getName().toLowerCase());
			if (! dataFolder.exists())
				dataFolder.mkdirs();

			type.initialize(plugin, type.getDescription(), null, null, new File(types, type.getName().toLowerCase()));
			type.onLoad();

			// Attempt to move the config
			attemptConfigMove(type);

			// Save and load configs
			type.saveDefaultConfig(true);
			type.loadConfig();

			arenaTypes.put(type.getName(), type);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "loading arena type '" + type.getName() + "'"));
		}
	}

	private final void attemptConfigMove(ArenaType type)
	{
		// Create data folder
		File dataFile = type.getDataFolder();
		if (! dataFile.exists())
			dataFile.mkdirs();

		String name = type.getName().toLowerCase();
		File configFile = new File(dataFile, name + "Config.yml");
		if (! configFile.exists())
		{
			// Check if the old file exists
			File configs = new File(plugin.getDataFolder(), "configs");
			if (configs.exists())
			{
				File config = new File(configs, "config.yml");
				if (config.exists())
				{
					try
					{
						// Attempt to copy
						Files.copy(config, configFile);
						config.delete();
					}
					catch (Throwable ex)
					{
						plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "Copying config file: " + config.getName()));
					}
				}
			}
		}
	}

	private final void loadArenaType(ArenaLoader loader, File file)
	{
		try
		{
			ArenaType type = loader.loadArenaType(file);
			if (arenaTypes.containsKey(type.getName()))
				throw new IllegalArgumentException("name '" + type.getName() + "' is already taken!");

			type.onLoad();

			type.loadConfig();
			arenaTypes.put(type.getName(), type);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "loading arena type '" + file.getName() + "'"));
		}
	}

	public final void enableArenaTypes()
	{
		for (ArenaType type : arenaTypes.values())
		{
			try
			{
				type.onEnable();
			}
			catch (Throwable ex)
			{
				plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "enabling arena type '" + type.getName() + "'"));
			}
		}
	}

	public final void disableArenaTypes()
	{
		for (ArenaType type : arenaTypes.values())
		{
			try
			{
				type.onDisable();
			}
			catch (Throwable ex)
			{
				plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "disabling arena type '" + type.getName() + "'"));
			}
		}

		arenaTypes.clear();
	}

	@Override
	public void reload()
	{
		for (ArenaType type : arenaTypes.values())
		{
			try
			{
				type.onReload();
			}
			catch (Throwable ex)
			{
				plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "reloading arena type '" + type.getName() + "'"));
			}
		}
	}
}