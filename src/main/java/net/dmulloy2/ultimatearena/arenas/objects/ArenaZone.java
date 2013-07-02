package net.dmulloy2.ultimatearena.arenas.objects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.dmulloy2.ultimatearena.Field;
import net.dmulloy2.ultimatearena.UltimateArena;

public class ArenaZone
{
	public int amtLobbys = 2;
	public int amtSpawnpoints = 2;
	public int stepnum;
	public int specialType = 20;
	public int timesPlayed = 0;
	public int liked;
	public int disliked;
	public int maxPlayers = 24;
	
	public boolean loaded = false;
	public boolean disabled = false;
	
	public String step;
	public String player;
	public String defaultClass;
	public String arenaName = "";
	public String arenaType = "";
	
	public Location lobby1 = null;
	public Location lobby2 = null;
	public Location arena1 = null;
	public Location arena2 = null;
	public Location flag1point = null;
	public Location flag2point = null;
	public Location team1spawn = null;
	public Location team2spawn = null;
	public Location lobbyREDspawn = null;
	public Location lobbyBLUspawn = null;
	
	public Field lobby;
	public Field arena;
	
	public List<String> voted = new ArrayList<String>();
	public List<String> steps = new ArrayList<String>();
	public List<Location> spawns = new ArrayList<Location>();
	public List<Location> flags = new ArrayList<Location>();
	
	public World world;
	
	public UltimateArena plugin;
	
	public ArenaZone(UltimateArena plugin, File file)
	{
		this.arenaName = getName(file);
		this.plugin = plugin;
		
		initialize();
	}
	
	public ArenaZone(UltimateArena plugin, String str) 
	{
		this.arenaName = str;
		this.plugin = plugin;
	}
	
	public void initialize()
	{
		this.lobby = new Field();
		this.arena = new Field();
		this.defaultClass = plugin.classes.get(0).name;
		
		load();
		
		if (loaded)
		{
			lobby.setParam(lobby1.getWorld(), lobby1.getBlockX(), lobby1.getBlockZ(), lobby2.getBlockX(), lobby2.getBlockZ());
			arena.setParam(arena1.getWorld(), arena1.getBlockX(), arena1.getBlockZ(), arena2.getBlockX(), arena2.getBlockZ());
		}
		else
		{
			plugin.getLogger().warning("Arena: " + arenaName + " has failed to load!");
		}
	}
	
	public boolean checkLocation(Location loc) 
	{
		return (lobby.isInside(loc) || arena.isInside(loc));
	}
	
	public void save()
	{
		plugin.getFileHelper().save(this);
	}
	
	public void load()
	{
		plugin.getFileHelper().load(this);
	}

	public boolean canLike(Player player)
	{
		return !voted.contains(player.getName());
	}
	
	public String getName(File file)
	{
		return file.getName().replaceAll(".dat", "");
	}
}