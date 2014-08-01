package net.dmulloy2.ultimatearena.types;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author dmulloy2
 */

@Getter @Setter
public abstract class FlagBase
{
	protected Location location;
	protected Block notify;
	protected Arena arena;

	protected final UltimateArena plugin;

	public FlagBase(Arena arena, ArenaLocation location, UltimateArena plugin)
	{
		this.arena = arena;
		this.location = location.getLocation().clone().subtract(0.0D, 1.0D, 0.0D);
		this.plugin = plugin;
		this.setup();
	}

	protected void setup()
	{
		this.notify = location.clone().add(0.0D, 5.0D, 0.0D).getBlock();
		this.notify.setType(Material.WOOL);

		location.clone().add(1, 0, 0).getBlock().setType(Material.STONE);
		location.clone().add(1, 0, 1).getBlock().setType(Material.STONE);
		location.clone().add(-1, 0, -1).getBlock().setType(Material.STONE);
		location.clone().add(1, 0, -1).getBlock().setType(Material.STONE);
		location.clone().add(-1, 0, 1).getBlock().setType(Material.STONE);
		location.clone().add(2, 0, 0).getBlock().setType(Material.STONE);
		location.clone().add(-1, 0, 0).getBlock().setType(Material.STONE);
		location.clone().add(-2, 0, 0).getBlock().setType(Material.STONE);
		location.clone().add(0, 0, 1).getBlock().setType(Material.STONE);
		location.clone().add(0, 0, 2).getBlock().setType(Material.STONE);
		location.clone().add(0, 0, -1).getBlock().setType(Material.STONE);
		location.clone().add(0, 0, -2).getBlock().setType(Material.STONE);
	}

	public abstract void checkNear(List<ArenaPlayer> arenaPlayers);
}