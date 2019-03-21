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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public final class ArenaTypeHandler implements Reloadable
{
	private final Map<String, ArenaType> arenaTypes;
	private final UltimateArena plugin;

	public ArenaTypeHandler(UltimateArena plugin)
	{
		this.plugin = plugin;
		this.arenaTypes = new HashMap<>();
		this.loadArenaTypes();
	}

	public ArenaType getArenaType(String name)
	{
		Validate.notEmpty(name, "name cannot be null or empty!");

		for (ArenaType type : arenaTypes.values())
		{
			if (type.getName().equalsIgnoreCase(name))
				return type;
		}

		return null;
	}

	public void loadArenaTypes()
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
		File[] files = directory.listFiles(file -> file.getName().endsWith(".jar"));

		if (files != null && files.length > 0)
		{
			for (File file : files)
			{
				loadArenaType(loader, file);
			}
		}
	}

	private void loadArenaType(ArenaType type)
	{
		try
		{
			File types = new File(plugin.getDataFolder(), "types");
			if (! types.exists())
				types.mkdirs();

			File dataFolder = new File(types, type.getName().toLowerCase());
			if (! dataFolder.exists())
				dataFolder.mkdirs();

			type.initialize(plugin, type.getDescription(), null, null);
			type.onEnable();

			// Attempt to move the config
			attemptConfigMove(type);

			// Save and load configs
			type.saveDefaultJarConfig();
			type.loadConfig();

			arenaTypes.put(type.getName(), type);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "loading arena type '" + type.getName() + "'"));
		}
	}

	private void attemptConfigMove(ArenaType type)
	{
		// Create data folder
		File dataFile = type.getDataFolder();
		if (! dataFile.exists())
			dataFile.mkdirs();

		File configFile = new File(dataFile, "config.yml");
		if (! configFile.exists())
		{
			// Check if the old file exists
			File configs = new File(plugin.getDataFolder(), "configs");
			if (configs.exists())
			{
				File config = new File(configs, type.getName().toLowerCase() + "Config.yml");
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
						plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "copying config file: " + config.getName()));
					}
				}

				String[] children = configs.list();
				if (children == null || children.length == 0)
					configs.delete();
			}
		}
	}

	private void loadArenaType(ArenaLoader loader, File file)
	{
		try
		{
			ArenaType type = loader.loadArenaType(file);

			ArenaType sameName = getArenaType(type.getName());
			if (sameName != null)
				throw new IllegalArgumentException("Name '" + type.getName() + "' is already taken by: " + sameName);

			type.onEnable();

			type.loadConfig();
			arenaTypes.put(type.getName().toLowerCase(), type);
	
			plugin.getLogHandler().log("Loaded custom arena: {0}", type.getName());
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "loading arena type '" + file.getName() + "'"));
		}
	}

	public void disable()
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
				type.reloadConfig();
				type.onReload();
			}
			catch (Throwable ex)
			{
				plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "reloading arena type '" + type.getName() + "'"));
			}
		}
	}

	public List<ArenaType> getArenaTypes()
	{
		return new ArrayList<>(arenaTypes.values());
	}
}
