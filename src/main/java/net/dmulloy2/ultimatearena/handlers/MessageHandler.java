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
package net.dmulloy2.ultimatearena.handlers;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.dmulloy2.types.Reloadable;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.util.Util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.base.Charsets;

/**
 * @author dmulloy2
 */

public class MessageHandler implements Reloadable
{
	private static final String FILE_NAME = "messages.yml";

	private YamlConfiguration config;
	private File file;

	private final Map<String, String> messages;
	private final UltimateArena plugin;

	public MessageHandler(UltimateArena plugin)
	{
		this.messages = new HashMap<>();
		this.plugin = plugin;
		this.reload();
	}

	public String getMessage(String key)
	{
		key = key.toLowerCase();
		if (messages.containsKey(key))
			return messages.get(key);

		String message = config.getString(key);
		if (message != null)
		{
			messages.put(key, message);
			return message;
		}

		return "[Missing message \"" + key + "\"]";
	}

	@Override
	public void reload()
	{
		messages.clear();

		saveDefaultConfig();
		reloadConfig();
		saveConfig();
	}

	public void saveDefaultConfig()
	{
		if (file == null)
			file = new File(plugin.getDataFolder(), FILE_NAME);
		if (! file.exists())
			plugin.saveResource(FILE_NAME, false);
	}

	public FileConfiguration getConfig()
	{
		if (config == null)
			reloadConfig();
		return config;
	}

	public void reloadConfig()
	{
		if (file == null)
			file = new File(plugin.getDataFolder(), FILE_NAME);
		config = YamlConfiguration.loadConfiguration(file);

		// Look for defaults in the jar
		Reader defConfigStream = new InputStreamReader(plugin.getResource(FILE_NAME), Charsets.UTF_8);
		if (defConfigStream != null)
		{
			System.out.println("setting default config");
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
			config.options().copyDefaults(true);
		}
	}

	public void saveConfig()
	{
		if (config == null || file == null)
			return;

		try
		{
			System.out.println("saving config");
			getConfig().save(file);
		}
		catch (IOException ex)
		{
			plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "saving {0}", FILE_NAME));
		}
	}
}
