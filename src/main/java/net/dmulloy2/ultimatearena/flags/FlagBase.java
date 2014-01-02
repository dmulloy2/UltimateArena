package net.dmulloy2.ultimatearena.flags;

import java.util.List;

import lombok.Data;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author dmulloy2
 */

@Data
public abstract class FlagBase
{
	protected Location location;
	protected Block notify;
	protected Arena arena;

	protected final UltimateArena plugin;

	public FlagBase(Arena arena, Location location, UltimateArena plugin)
	{
		this.arena = arena;
		this.location = location.clone().subtract(0, 1, 0);
		this.plugin = plugin;
		this.setup();
	}

	public FlagBase(Arena arena, ArenaLocation location, UltimateArena plugin)
	{
		this(arena, location.getLocation(), plugin);
	}

	protected void setup()
	{
		Location flag = location.clone().add(0, 5, 0);

		this.notify = flag.getBlock();
		this.notify.setType(Material.WOOL);

		(location.clone().add(1, 0, 0)).getBlock().setType(Material.STONE);
		(location.clone().add(1, 0, 1)).getBlock().setType(Material.STONE);
		(location.clone().add(-1, 0, -1)).getBlock().setType(Material.STONE);
		(location.clone().add(1, 0, -1)).getBlock().setType(Material.STONE);
		(location.clone().add(-1, 0, 1)).getBlock().setType(Material.STONE);
		(location.clone().add(2, 0, 0)).getBlock().setType(Material.STONE);
		(location.clone().add(-1, 0, 0)).getBlock().setType(Material.STONE);
		(location.clone().add(-2, 0, 0)).getBlock().setType(Material.STONE);
		(location.clone().add(0, 0, 1)).getBlock().setType(Material.STONE);
		(location.clone().add(0, 0, 2)).getBlock().setType(Material.STONE);
		(location.clone().add(0, 0, -1)).getBlock().setType(Material.STONE);
		(location.clone().add(0, 0, -2)).getBlock().setType(Material.STONE);
	}

	public abstract void checkNear(List<ArenaPlayer> arenaPlayers);
}