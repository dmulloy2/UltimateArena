package net.dmulloy2.ultimatearena.handlers;

import java.io.File;
import java.io.IOException;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaSign;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

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
		if (!signsSave.exists())
		{
			try
			{
				signsSave.createNewFile();
			}
			catch (IOException e)
			{
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
				if (!fc.isSet(path))
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
			return;
		}

		int total = 0;

		YamlConfiguration fc = YamlConfiguration.loadConfiguration(signsSave);
		for (ArenaSign sign : plugin.getArenaSigns())
		{
			plugin.debug("Attempting to save sign: {0}", sign);

			String path = "signs." + sign.getId() + ".";

			fc.set(path + "name", sign.getArena());

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
		}
	}

	public void updateSigns()
	{
		for (int i = 0; i < plugin.getArenaSigns().size(); i++)
		{
			ArenaSign sign = plugin.getArenaSigns().get(i);
			sign.update();
		}
	}
}