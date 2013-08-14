package net.dmulloy2.ultimatearena;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaSign;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;

/**
 * Manager for Signs
 * @author dmulloy2
 */

public class SignManager 
{
	private File signsSave;
	
	private List<ArenaSign> signs;
	
	private final UltimateArena plugin;
	public SignManager(final UltimateArena plugin)
	{
		this.plugin = plugin;
		this.signs = new ArrayList<ArenaSign>();
		
		load();
	}
	
	public void load()
	{
		this.signsSave = new File(plugin.getDataFolder(), "signs.yml");
		if (! signsSave.exists()) 
		{
			try { signsSave.createNewFile(); } catch (Exception e) { return; }
		}
		
		YamlConfiguration fc = YamlConfiguration.loadConfiguration(signsSave);
		if (fc.isSet("total"))
		{
			int total = fc.getInt("total");
			for (int i = 0; i < total; i++)
			{
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
							plugin.arenaSigns.add(as);
							as.update();
						}
					}
				}
			}
		}
	}
	
	public void deleteSign(ArenaSign sign)
	{
		signs.remove(sign);
		
		refreshSave();
	}
	
	public void refreshSave()
	{
		signsSave.delete();
		
		try { signsSave.createNewFile(); }
		catch (Exception e) { return; }
		
		int total = 0;
		
		YamlConfiguration fc = YamlConfiguration.loadConfiguration(signsSave);
		for (ArenaSign sign : plugin.arenaSigns)
		{
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
		
		fc.set("total", total);
		
		try { fc.save(signsSave); } catch (IOException e) { }
	}
	
	public void updateSigns()
	{
		for (ArenaSign sign : plugin.arenaSigns)
		{
			sign.update();
		}
	}
}
