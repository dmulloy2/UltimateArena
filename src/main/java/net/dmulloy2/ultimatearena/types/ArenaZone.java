package net.dmulloy2.ultimatearena.types;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import lombok.Data;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.util.FormatUtil;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.Material;

/**
 * @author dmulloy2
 */

@Data
public class ArenaZone
{
	private int amtLobbys = 2;
	private int amtSpawnpoints = 2;
//	private int specialType = 20;
	private int timesPlayed = 0;
	private int liked;
	private int disliked;
	private int maxPlayers = 24;
	
	private Material specialType;

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
	 * @param plugin
	 *            - {@link UltimateArena} plugin instance
	 * @param file
	 *            - {@link File} to load
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
	 * @param ac
	 *            - {@link ArenaCreator} to create arena from
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
		this.specialType = Material.SNOW_BLOCK;
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
		return lobby.isInside(loc) || arena.isInside(loc);
	}

	public void save()
	{
		plugin.getFileHandler().save(this);
	}

	public void load()
	{
		plugin.getFileHandler().load(this);
	}

	public boolean canLike(Player player)
	{
		return ! voted.contains(player.getName());
	}

	public String getName(File file)
	{
		return file.getName().replaceAll(".dat", "");
	}
	
	public List<String> getStats()
	{
		List<String> lines = new ArrayList<String>();
		
		StringBuilder line = new StringBuilder();
		line.append(FormatUtil.format("&3====[ &e{0} &3]====", WordUtils.capitalize(arenaName)));
		lines.add(line.toString());
		
		// Calculate percentage
		int total = plugin.getTotalArenasPlayed();
		int plays = timesPlayed;
		
		double percentage = ((double) plays / (double) total) * 100;
		
		line = new StringBuilder();
		line.append(FormatUtil.format("&3Plays: &e{0}&3/&e{1} &3(&e{2}&3)", plays, total, percentage));
		lines.add(line.toString());
		
		// Calculate popularity
		percentage = ((double) liked / (double) voted.size()) * 100;
		
		line = new StringBuilder();
		line.append(FormatUtil.format("&3Popularity: &e{0}&3/&e{1} &3(&e{2}&3)", liked, disliked, percentage));
		lines.add(line.toString());
		
		return lines;
	}
}