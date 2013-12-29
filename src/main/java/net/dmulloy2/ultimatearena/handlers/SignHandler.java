package net.dmulloy2.ultimatearena.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.dmulloy2.ultimatearena.UltimateArena;
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
	private File file;
	private FileConfiguration signsSave;

	private List<ArenaSign> signs;

	private final UltimateArena plugin;
	public SignHandler(UltimateArena plugin)
	{
		this.plugin = plugin;
		this.signs = new ArrayList<ArenaSign>();

		try
		{
			this.file = new File(plugin.getDataFolder(), "signs.yml");
			if (! file.exists())
			{
				if (! createNewSave(false))
					return;
			}

			this.signsSave = YamlConfiguration.loadConfiguration(file);
			if (signsSave.isSet("total"))
			{
				if (! createNewSave(true))
					return;
			}

			// Dynamic ID's
			int nextId = 1;

			for (Entry<String, Object> value : signsSave.getValues(false).entrySet())
			{
				MemorySection mem = (MemorySection) value.getValue();

				ArenaSign sign = new ArenaSign(plugin, mem.getValues(true));
				if (sign != null)
				{
					sign.setId(nextId);
					nextId++;

					signs.add(sign);

					plugin.debug("Successfully loaded ArenaSign {0}. Next id = {1}", sign, nextId);
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

	/**
	 * Saves and clears all signs
	 */
	public final void onDisable()
	{
		// Save signs
		save();

		// Clear the list
		signs.clear();
	}

	private final void save()
	{
		plugin.debug("Saving signs...");

		try
		{
			if (! createNewSave(true))
				return;

			for (ArenaSign sign : getSigns())
			{
				plugin.debug("Saving sign: " + sign);

				Map<String, Object> values = sign.serialize();
				signsSave.set("" + sign.getId(), values);
			}

			signsSave.save(file);
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, Util.getUsefulStack(e, "saving signs"));
		}
	}

	private final boolean createNewSave(boolean delete)
	{
		plugin.debug("Creating new sign save. Delete = {0}", delete);

		try
		{
			if (file.exists() && delete)
			{
				file.delete();
			}

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

	public final void updateAllSigns()
	{
		for (ArenaSign sign : getSigns())
		{
			sign.update();
		}
	}

	public final ArenaSign getSign(Location loc)
	{
		for (ArenaSign sign : getSigns())
		{
			if (Util.checkLocation(sign.getLoc(), loc))
				return sign;
		}

		return null;
	}

	public final void addSign(final ArenaSign sign)
	{
		signs.add(sign);

		plugin.debug("Added new sign: {0}", sign);

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				sign.update();
			}
		}.runTaskLater(plugin, 60L);

	}

	public final void deleteSign(ArenaSign sign)
	{
		plugin.debug("Deleting sign {0}!", sign.getId());

		signs.remove(sign);

		updateAllSigns();
	}

	public final void updateSigns(ArenaZone az)
	{
		for (ArenaSign sign : getSigns(az))
		{
			sign.update();
		}
	}

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
}