package net.dmulloy2.ultimatearena.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaSign;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.Location;
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
		for (Entry<String, Object> value : fc.getValues(false).entrySet())
		{
			@SuppressWarnings("unchecked")
			Map<String, Object> value1 = (Map<String, Object>) value.getValue();
			
			ArenaSign sign = new ArenaSign(plugin, value1);
			if (sign != null)
			{
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
			
			for (int i = 0; i < plugin.getArenaSigns().size(); i++)
			{
				ArenaSign sign = plugin.getArenaSigns().get(i);
				
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
	
	public final List<ArenaSign> getSigns()
	{
		return Collections.unmodifiableList(plugin.getArenaSigns());
	}
}