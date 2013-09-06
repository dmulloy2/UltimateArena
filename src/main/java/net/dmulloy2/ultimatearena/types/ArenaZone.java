package net.dmulloy2.ultimatearena.types;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.dmulloy2.ultimatearena.Field;
import net.dmulloy2.ultimatearena.UltimateArena;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ArenaZone
{
	private int amtLobbys = 2;
	private int amtSpawnpoints = 2;
	private int specialType = 20;
	private int timesPlayed = 0;
	private int liked;
	private int disliked;
	private int maxPlayers = 24;
	
	private boolean loaded = false;
	private boolean disabled = false;
	
	private String step;
	private String player;
	private String defaultClass;
	private String arenaName = "";
	private FieldType type;
	
	private Location lobby1 = null;
	private Location lobby2 = null;
	private Location arena1 = null;
	private Location arena2 = null;
	private Location team1spawn = null;
	private Location team2spawn = null;
	private Location lobbyREDspawn = null;
	private Location lobbyBLUspawn = null;
	
	private Field lobby;
	private Field arena;
	
	private List<String> voted = new ArrayList<String>();
	private List<Location> spawns = new ArrayList<Location>();
	private List<Location> flags = new ArrayList<Location>();
	
	private World world;
	
	private final UltimateArena plugin;
	
	/**
	 * Represents an ArenaZone to be loaded from configuration
	 * 
	 * @param plugin - {@link UltimateArena} plugin instance
	 * @param file - {@link File} to load
	 * @return new {@link ArenaZone}
	 */
	public ArenaZone(final UltimateArena plugin, File file)
	{
		this.arenaName = getName(file);
		this.plugin = plugin;
		
		initialize();
	}
	
	/**
	 * Represents an ArenaZone to be created from an {@link ArenaCreator}
	 * 
	 * @param ac - {@link ArenaCreator} to create arena from
	 * @return new {@link ArenaZone}
	 */
	public ArenaZone(final UltimateArena plugin, ArenaCreator ac)
	{
		this.plugin = plugin;
		this.arenaName = ac.arenaName;
		this.type = ac.arenaType;
		this.lobbyBLUspawn = ac.lobbyBLUspawn;
		this.lobbyREDspawn = ac.lobbyREDspawn;
		this.team1spawn = ac.team1spawn;
		this.team2spawn = ac.team2spawn;
		this.lobby1 = ac.lobby1;
		this.lobby2 = ac.lobby2;
		this.arena1 = ac.arena1;
		this.arena2 = ac.arena2;
		this.spawns = ac.spawns;
		this.flags = ac.flags;
		this.maxPlayers = 24;
		this.specialType = 80;
		this.defaultClass = plugin.getClasses().get(0).getName();
		this.world = lobby1.getWorld();

		save();
		
		initialize();
		
		if (loaded)
		{
			plugin.outConsole("Creation of Arena {0} successful!", arenaName);
		}
		else
		{
			plugin.outConsole(Level.WARNING, "Creation of Arena {0} has failed!", arenaName);
		}
	}

	public void initialize()
	{
		this.lobby = new Field();
		this.arena = new Field();
		
		ArenaClass ac = plugin.getClasses().get(0);
		if (ac != null)
			this.defaultClass = ac.getName();
		
		load();
		
		if (isLoaded())
		{
			lobby.setParam(lobby1.getWorld(), lobby1.getBlockX(), lobby1.getBlockZ(), lobby2.getBlockX(), lobby2.getBlockZ());
			arena.setParam(arena1.getWorld(), arena1.getBlockX(), arena1.getBlockZ(), arena2.getBlockX(), arena2.getBlockZ());
			
			plugin.getLoadedArenas().add(this);
		}
		else
		{
			plugin.outConsole(Level.WARNING, "Arena {0} has failed to load!", arenaName);
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
		return ! voted.contains(player.getName());
	}
	
	public String getName(File file)
	{
		return file.getName().replaceAll(".dat", "");
	}

	public int getAmtLobbys() 
	{
		return amtLobbys;
	}

	public void setAmtLobbys(int amtLobbys)
	{
		this.amtLobbys = amtLobbys;
	}

	public int getAmtSpawnpoints()
	{
		return amtSpawnpoints;
	}

	public void setAmtSpawnpoints(int amtSpawnpoints) 
	{
		this.amtSpawnpoints = amtSpawnpoints;
	}

	public int getSpecialType() 
	{
		return specialType;
	}

	public void setSpecialType(int specialType) 
	{
		this.specialType = specialType;
	}

	public int getTimesPlayed() 
	{
		return timesPlayed;
	}

	public void setTimesPlayed(int timesPlayed) 
	{
		this.timesPlayed = timesPlayed;
	}

	public int getLiked()
	{
		return liked;
	}

	public void setLiked(int liked) 
	{
		this.liked = liked;
	}

	public int getDisliked()
	{
		return disliked;
	}

	public void setDisliked(int disliked) 
	{
		this.disliked = disliked;
	}

	public int getMaxPlayers()
	{
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers)
	{
		this.maxPlayers = maxPlayers;
	}

	public boolean isDisabled() 
	{
		return disabled;
	}

	public void setDisabled(boolean disabled) 
	{
		this.disabled = disabled;
	}

	public String getStep() 
	{
		return step;
	}

	public void setStep(String step)
	{
		this.step = step;
	}

	public String getPlayer() 
	{
		return player;
	}

	public void setPlayer(String player) 
	{
		this.player = player;
	}

	public String getDefaultClass()
	{
		return defaultClass;
	}

	public void setDefaultClass(String defaultClass) 
	{
		this.defaultClass = defaultClass;
	}

	public FieldType getType() 
	{
		return type;
	}

	public void setArenaType(FieldType type) 
	{
		this.type = type;
	}

	public String getArenaName()
	{
		return arenaName;
	}

	public void setArenaName(String arenaName) 
	{
		this.arenaName = arenaName;
	}

	public World getWorld() 
	{
		return world;
	}
	
	public Location getLobby1()
	{
		return lobby1;
	}

	public void setLobby1(Location lobby1) 
	{
		this.lobby1 = lobby1;
	}

	public Location getLobby2()
	{
		return lobby2;
	}

	public void setLobby2(Location lobby2)
	{
		this.lobby2 = lobby2;
	}

	public Location getArena1()
	{
		return arena1;
	}

	public void setArena1(Location arena1) 
	{
		this.arena1 = arena1;
	}

	public Location getArena2() 
	{
		return arena2;
	}

	public void setArena2(Location arena2) 
	{
		this.arena2 = arena2;
	}

	public Location getLobbyREDspawn()
	{
		return lobbyREDspawn;
	}

	public void setLobbyREDspawn(Location lobbyREDspawn) 
	{
		this.lobbyREDspawn = lobbyREDspawn;
	}

	public Location getLobbyBLUspawn()
	{
		return lobbyBLUspawn;
	}

	public void setLobbyBLUspawn(Location lobbyBLUspawn) 
	{
		this.lobbyBLUspawn = lobbyBLUspawn;
	}

	public Location getTeam1spawn() 
	{
		return team1spawn;
	}

	public void setTeam1spawn(Location team1spawn) 
	{
		this.team1spawn = team1spawn;
	}

	public Location getTeam2spawn() 
	{
		return team2spawn;
	}

	public void setTeam2spawn(Location team2spawn) 
	{
		this.team2spawn = team2spawn;
	}

	public List<Location> getSpawns()
	{
		return spawns;
	}

	public void setSpawns(List<Location> spawns) 
	{
		this.spawns = spawns;
	}

	public List<Location> getFlags()
	{
		return flags;
	}

	public void setFlags(List<Location> flags) 
	{
		this.flags = flags;
	}

	public boolean isLoaded()
	{
		return loaded;
	}

	public void setLoaded(boolean loaded) 
	{
		this.loaded = loaded;
	}

	public UltimateArena getPlugin() 
	{
		return plugin;
	}

	public List<String> getVoted() 
	{
		return voted;
	}

	public void setWorld(World world)
	{
		this.world = world;	
	}
}