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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.dmulloy2.swornapi.types.Reloadable;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.swornapi.util.Util;

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
	private final List<String> disabled;

	private final UltimateArena plugin;

	public MessageHandler(UltimateArena plugin)
	{
		this.messages = new HashMap<>();
		this.disabled = new ArrayList<>();
		this.plugin = plugin;
		this.reload();
	}

	public String getMessage(String key)
	{
		if (disabled.contains(key))
			return "";

		if (messages.containsKey(key))
			return messages.get(key);

		String message = config.getString(key);
		if (message != null)
		{
			if (message.isEmpty() || ! config.getBoolean(key + ".enabled", true))
			{
				disabled.add(key);
				return "";
			}

			messages.put(key, message);
			return message;
		}

		return "[Missing message \"" + key + "\"]";
	}

	public boolean isDisabled(String key)
	{
		return getMessage(key).isEmpty();
	}

	@Override
	public void reload()
	{
		messages.clear();
		disabled.clear();

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
		InputStream stream = plugin.getResource(FILE_NAME);
		if (stream != null)
		{
			Reader defConfigStream = new InputStreamReader(stream, Charsets.UTF_8);
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);

			config.options().copyDefaults(true);
			config.options().copyHeader(true);
		}
	}

	public void saveConfig()
	{
		if (config == null || file == null)
			return;

		try
		{
			getConfig().save(file);
		}
		catch (IOException ex)
		{
			plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "saving {0}", FILE_NAME));
		}
	}
}
