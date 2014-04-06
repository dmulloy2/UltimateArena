package net.dmulloy2.ultimatearena.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaSign;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.util.Util;

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
	private static final int CURRENT_VERSION = 1;

	private File file;
	private FileConfiguration signsSave;

	private List<ArenaSign> signs;

	private final UltimateArena plugin;
	public SignHandler(UltimateArena plugin)
	{
		this.plugin = plugin;
		this.signs = new ArrayList<ArenaSign>();
		this.loadFromDisk();
	}

	private final void loadFromDisk()
	{
		try
		{
			this.file = new File(plugin.getDataFolder(), "signs.yml");
			if (! file.exists())
			{
				if (! createNewSave(false))
					return;
			}

			this.signsSave = YamlConfiguration.loadConfiguration(file);
			if (! signsSave.isSet("version"))
			{
				if (! createNewSave(true))
					return;
			}

			Map<String, Object> values = signsSave.getValues(false);
			for (Entry<String, Object> value : values.entrySet())
			{
				if (value.getKey().equals("version"))
				{
					// Normally, we would do some sort of conversion here, but signs were broken beyond repair before this.
					continue;
				}

				MemorySection mem = (MemorySection) value.getValue();
				ArenaSign sign = new ArenaSign(plugin, mem.getValues(true));
				if (sign != null)
				{
					signs.add(sign);
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
		catch (Exception ex)
		{
			plugin.outConsole(Level.SEVERE, Util.getUsefulStack(ex, "loading signs"));
		}
	}

	private final void saveToDisk()
	{
		try
		{
			if (! createNewSave(true))
				return;

			for (ArenaSign sign : getSigns())
			{
				Map<String, Object> values = sign.serialize();
				signsSave.set("" + sign.getId(), values);
			}

			signsSave.set("version", CURRENT_VERSION);
			signsSave.save(file);
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, Util.getUsefulStack(e, "saving signs"));
		}
	}

	private final boolean createNewSave(boolean delete)
	{
		try
		{
			if (file.exists() && delete)
				file.delete();

			file.createNewFile();
			signsSave = YamlConfiguration.loadConfiguration(file);
			return true;
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, Util.getUsefulStack(e, "creating new sign save"));
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

	private final void updateAllSigns()
	{
		for (ArenaSign sign : getSigns())
		{
			sign.update();
		}
	}

	/**
	 * Attempts to get an {@link ArenaSign} based on location
	 *
	 * @param loc
	 *        - Location
	 */
	public final ArenaSign getSign(Location loc)
	{
		for (ArenaSign sign : getSigns())
		{
			if (Util.checkLocation(sign.getLoc().getLocation(), loc))
				return sign;
		}

		return null;
	}

	/**
	 * Attempts to get an {@link ArenaSign} based on location
	 *
	 * @param loc
	 *        - {@link ArenaLocation}
	 */
	public final ArenaSign getLocation(ArenaLocation loc)
	{
		for (ArenaSign sign : getSigns())
		{
			if (sign.getLoc().equals(loc))
				return sign;
		}

		return null;
	}

	/**
	 * Adds a sign to track and save
	 *
	 * @param sign
	 *        - {@link ArenaSign} to add
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
	 * @param sign
	 *        - {@link ArenaSign} to delete
	 */
	public final void deleteSign(ArenaSign sign)
	{
		signs.remove(sign);
		updateAllSigns();
	}

	/**
	 * Updates an {@link ArenaZone}'s signs
	 *
	 * @param az
	 *        - {@link ArenaZone}
	 */
	public final void updateSigns(ArenaZone az)
	{
		for (ArenaSign sign : getSigns(az))
		{
			sign.update();
		}
	}

	/**
	 * Clears an {@link ArenaZone}'s signs
	 *
	 * @param az
	 *        - {@link ArenaZone}
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
	 * @param az
	 *        - {@link ArenaZone}
	 */
	public final List<ArenaSign> getSigns(ArenaZone az)
	{
		List<ArenaSign> ret = new ArrayList<ArenaSign>();

		for (ArenaSign sign : getSigns())
		{
			if (sign.getArenaName().equals(az.getArenaName()))
				ret.add(sign);
		}

		return ret;
	}

	/**
	 * Workaround for concurrency issues
	 */
	public final List<ArenaSign> getSigns()
	{
		return Util.newList(signs);
	}

	// ---- ID Related Stuff ---- //

	/**
	 * Gets the lowest free id
	 *
	 * @param start
	 *        - Start index
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

	private final int recurseFreeId(int start, Set<Integer> keySet)
	{
		int id = start;
		for (int i : keySet)
		{
			if (id == i)
				id++;
		}

		return id;
	}

	private final Map<Integer, ArenaSign> getById()
	{
		Map<Integer, ArenaSign> ret = new HashMap<Integer, ArenaSign>();
		for (ArenaSign sign : getSigns())
		{
			ret.put(sign.getId(), sign);
		}

		return ret;
	}
}