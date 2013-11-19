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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Handles sign functions
 * 
 * @author dmulloy2
 */

public class SignHandler
{
	private File signsSave;

	private final UltimateArena plugin;
	public SignHandler(UltimateArena plugin)
	{
		this.plugin = plugin;

		load();
	}

	public void load()
	{
		try
		{
			this.signsSave = new File(plugin.getDataFolder(), "signs.yml");
			if (! signsSave.exists())
			{
				signsSave.createNewFile();
			}

			YamlConfiguration fc = YamlConfiguration.loadConfiguration(signsSave);
			if (fc.isSet("total"))
			{
				signsSave.delete();
				signsSave.createNewFile();

				fc = YamlConfiguration.loadConfiguration(signsSave);
			}

			// Dynamic ID's
			int nextId = 1;

			for (Entry<String, Object> value : fc.getValues(false).entrySet())
			{
				MemorySection mem = (MemorySection) value.getValue();

				ArenaSign sign = new ArenaSign(plugin, mem.getValues(true));
				if (sign != null)
				{
					sign.setId(nextId);
					nextId++;

					plugin.getArenaSigns().add(sign);
				}
			}

			// Update signs
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					updateSigns();
				}
			}.runTaskLater(plugin, 120L);
		}
		catch (Exception ex)
		{
			plugin.outConsole(Level.SEVERE, Util.getUsefulStack(ex, "loading signs"));
		}
	}

	public void save()
	{
		try
		{
			if (signsSave.exists())
			{
				signsSave.delete();
			}

			signsSave.createNewFile();

			YamlConfiguration fc = YamlConfiguration.loadConfiguration(signsSave);

			for (ArenaSign sign : getSigns())
			{
				Map<String, Object> values = sign.serialize();
				fc.set("" + sign.getId(), values);
			}

			fc.save(signsSave);
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, Util.getUsefulStack(e, "saving signs"));
		}
	}

	public final void updateSigns()
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

	public final void deleteSign(ArenaSign sign)
	{
		plugin.debug("Deleting sign {0}!", sign.getId());

		plugin.getArenaSigns().remove(sign);

		updateSigns();
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
		return Util.newList(plugin.getArenaSigns());
	}
}