package net.dmulloy2.ultimatearena.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaSign;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Manager for Signs
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
		plugin.debug("Loading all signs!");

		this.signsSave = new File(plugin.getDataFolder(), "signs.yml");
		if (! signsSave.exists())
		{
			try
			{
				signsSave.createNewFile();
			}
			catch (IOException e)
			{
				plugin.debug("Could not create new signs save: {0}", e);
				return;
			}
		}

		YamlConfiguration fc = YamlConfiguration.loadConfiguration(signsSave);
		if (fc.isSet("total"))
		{
			int total = fc.getInt("total");
			for (int i = 0; i < total; i++)
			{
				plugin.debug("Attempting to load sign: {0}", i);

				String path = "signs." + i + ".";
				if (! fc.isSet(path))
					continue;

				String arenaName = fc.getString(path + "name");

				String locPath = path + "location.";
				String worldName = fc.getString(locPath + "world");
				World world = plugin.getServer().getWorld(worldName);
				if (world != null)
				{
					Location loc = new Location(world, fc.getInt(locPath + "x"), fc.getInt(locPath + "y"), fc.getInt(locPath + "z"));
					if (loc != null)
					{
						ArenaZone az = plugin.getArenaZone(arenaName);
						if (az != null)
						{
							ArenaSign as = new ArenaSign(plugin, loc, az, i);
							plugin.getArenaSigns().add(as);

							plugin.debug("Successfully loaded sign: {0}", as);
						}
					}
				}
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

	public void refreshSave()
	{
		plugin.debug("Refreshing signs save!");

		signsSave.delete();

		try
		{
			signsSave.createNewFile();
		}
		catch (IOException e)
		{
			plugin.debug("Could not refresh sign save: {0}", e);
			return;
		}

		int total = 0;

		YamlConfiguration fc = YamlConfiguration.loadConfiguration(signsSave);
		for (ArenaSign sign : plugin.getArenaSigns())
		{
			plugin.debug("Attempting to save sign: {0}", sign);

			String path = "signs." + sign.getId() + ".";

			fc.set(path + "name", sign.getName());

			Location location = sign.getLocation();
			String locPath = path + "location.";
			fc.set(locPath + "world", location.getWorld().getName());
			fc.set(locPath + "x", location.getBlockX());
			fc.set(locPath + "y", location.getBlockX());
			fc.set(locPath + "z", location.getBlockX());

			total = sign.getId();
		}

		fc.set("total", total + 1);

		try
		{
			fc.save(signsSave);
		}
		catch (IOException e)
		{
			plugin.debug("Could not refresh sign save: {0}", e);
			return;
		}
	}

	public void updateSigns()
	{
		for (ArenaSign sign : plugin.getArenaSigns())
		{
			sign.update();
		}
	}
	
	public ArenaSign getSign(Location loc)
	{
		for (ArenaSign sign : plugin.getArenaSigns())
		{
			if (Util.checkLocation(sign.getLocation(), loc))
				return sign;
		}

		return null;
	}
	
	public List<ArenaSign> getSigns(ArenaZone az)
	{
		List<ArenaSign> ret = new ArrayList<ArenaSign>();
		
		for (ArenaSign sign : plugin.getArenaSigns())
		{
			if (sign.getName().equals(az.getArenaName()))
				ret.add(sign);
		}
		
		return ret;
	}
	
	public void deleteSign(ArenaSign sign)
	{
		plugin.debug("Deleting sign {0}!", sign.getId());

		plugin.getArenaSigns().remove(sign);

		refreshSave();
		updateSigns();
	}
}