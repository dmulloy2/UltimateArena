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
package net.dmulloy2.ultimatearena.signs;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.signs.ArenaSign.SignType;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.util.Util;

import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Handles sign functions
 *
 * @author dmulloy2
 */

public class SignHandler
{
	private static final int CURRENT_VERSION = 2;

	private File file;
	private FileConfiguration signsSave;

	private List<ArenaSign> signs;

	private final UltimateArena plugin;
	public SignHandler(UltimateArena plugin)
	{
		this.plugin = plugin;
		this.signs = new ArrayList<>();
		this.loadFromDisk();
	}

	private void loadFromDisk()
	{
		try
		{
			file = new File(plugin.getDataFolder(), "signs.yml");
			if (! file.exists())
			{
				if (! createNewSave(false))
					return;
			}

			signsSave = new YamlConfiguration();
			signsSave.load(file);

			if (! signsSave.isSet("version"))
			{
				if (! createNewSave(true))
					return;
			}

			Map<String, Object> values = signsSave.getValues(false);
			if (values.containsKey("version"))
			{
				int version = (int) values.get("version");
				if (version == 1)
				{
					// Convert to modular sign format
					signsSave.set("version", CURRENT_VERSION);

					for (Entry<String, Object> entry : values.entrySet())
					{
						if (entry.getKey().equals("version"))
							continue;

						MemorySection mem = (MemorySection) entry.getValue();
						mem.set("type", "JOIN");
					}
				}
			}

			values = signsSave.getValues(false);
			for (Entry<String, Object> entry : values.entrySet())
			{
				if (entry.getKey().equals("version"))
					continue;

				String id = entry.getKey();

				try
				{
					MemorySection section = (MemorySection) entry.getValue();
					SignType type = SignType.valueOf(section.getString("type", "JOIN"));

					ArenaSign sign;
					switch (type)
					{
						case JOIN:
							sign = new JoinSign(plugin, section);
							break;
						case LAST_GAME:
							sign = new LastGameSign(plugin, section);
							break;
						case STATUS:
							sign = new StatusSign(plugin, section);
							break;
						default:
							throw new IllegalArgumentException("Unknown sign type: " + section.getString("type"));
					}

					signs.add(sign);
				}
				catch (Throwable ex)
				{
					plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading sign {0}", id));
				}
			}

			// Update signs
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					updateAllSigns();
				}
			}.runTaskLater(plugin, 120L);
		}
		catch (Throwable ex)
		{
			plugin.log(Level.SEVERE, Util.getUsefulStack(ex, "loading signs"));
		}
	}

	private void saveToDisk()
	{
		try
		{
			if (! createNewSave(true))
				return;

			signsSave.set("version", CURRENT_VERSION);

			for (ArenaSign sign : getSigns())
			{
				Map<String, Object> values = sign.serialize();
				signsSave.set("" + sign.getId(), values);
			}

			signsSave.save(file);
		}
		catch (Throwable ex)
		{
			plugin.log(Level.SEVERE, Util.getUsefulStack(ex, "saving signs"));
		}
	}

	private boolean createNewSave(boolean delete)
	{
		try
		{
			if (file.exists() && delete)
				file.delete();

			file.createNewFile();
			signsSave = new YamlConfiguration();
			return true;
		}
		catch (Throwable ex)
		{
			plugin.log(Level.SEVERE, Util.getUsefulStack(ex, "creating new sign save"));
			return false;
		}
	}

	/**
	 * Saves and clears all signs
	 */
	public final void onDisable()
	{
		// Save signs
		saveToDisk();

		// Clear the list
		signs.clear();
	}

	private void updateAllSigns()
	{
		for (ArenaSign sign : getSigns())
		{
			sign.update();
		}
	}

	/**
	 * Attempts to get an {@link ArenaSign} based on location
	 *
	 * @param loc Location
	 */
	public final ArenaSign getSign(Location loc)
	{
		for (ArenaSign sign : getSigns())
		{
			if (Util.coordsEqual(sign.getLoc().getLocation(), loc))
				return sign;
		}

		return null;
	}

	/**
	 * Adds a sign to track and save
	 *
	 * @param sign {@link ArenaSign} to add
	 */
	public final void addSign(final ArenaSign sign)
	{
		signs.add(sign);

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				sign.update();
			}
		}.runTaskLater(plugin, 60L);

	}

	/**
	 * Deletes a sign
	 *
	 * @param sign {@link ArenaSign} to delete
	 */
	public final void deleteSign(ArenaSign sign)
	{
		signs.remove(sign);
		updateAllSigns();
	}

	/**
	 * Updates an {@link ArenaZone}'s signs
	 *
	 * @param az {@link ArenaZone}
	 */
	public final void updateSigns(ArenaZone az)
	{
		for (ArenaSign sign : getSigns(az))
		{
			sign.update();
		}
	}

	public final void onArenaCompletion(Arena arena)
	{
		for (ArenaSign sign : getSigns(arena.getAz()))
		{
			sign.onArenaCompletion(arena);
		}
	}

	/**
	 * Clears an {@link ArenaZone}'s signs
	 *
	 * @param az {@link ArenaZone}
	 */
	public final void clearSigns(ArenaZone az)
	{
		for (ArenaSign sign : getSigns(az))
		{
			sign.clear();
		}
	}

	/**
	 * Gets the signs associated with a given arena
	 *
	 * @param az {@link ArenaZone}
	 */
	public final List<ArenaSign> getSigns(ArenaZone az)
	{
		List<ArenaSign> ret = new ArrayList<>();

		for (ArenaSign sign : getSigns())
		{
			if (sign.getArenaName().equals(az.getName()))
				ret.add(sign);
		}

		return ret;
	}

	/**
	 * Workaround for concurrency issues
	 */
	public final ArenaSign[] getSigns()
	{
		return signs.toArray(new ArenaSign[signs.size()]);
	}

	public final int getSignCount()
	{
		return signs.size();
	}

	// ---- ID Related Stuff

	/**
	 * Gets the lowest free id
	 *
	 * @param start Starting index
	 */
	public final int getFreeId(int start)
	{
		Set<Integer> keySet = getById().keySet();

		int id = recurseFreeId(start, keySet);
		int newId;

		// Iterate through until we find an id that does not already exist
		while (id != (newId = recurseFreeId(id, keySet)))
			id = newId;

		return id;
	}

	private int recurseFreeId(int start, Set<Integer> keySet)
	{
		int id = start;
		for (int i : keySet)
		{
			if (id == i)
				id++;
		}

		return id;
	}

	private Map<Integer, ArenaSign> getById()
	{
		Map<Integer, ArenaSign> ret = new LinkedHashMap<>();

		for (ArenaSign sign : getSigns())
		{
			ret.put(sign.getId(), sign);
		}

		return ret;
	}
}
