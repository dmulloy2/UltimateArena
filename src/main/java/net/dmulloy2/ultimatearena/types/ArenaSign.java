package net.dmulloy2.ultimatearena.types;

import lombok.Getter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Represents an ArenaSign, whether it be join or not
 * 
 * @author dmulloy2
 */

@Getter
public class ArenaSign
{
	private Location location;
	private ArenaZone arena;
	private int id;
	private Sign sign;
	
	private final UltimateArena plugin;

	/**
	 * Creates a new ArenaSign
	 * 
	 * @param plugin
	 *            - {@link UltimateArena} plugin instance
	 * @param loc
	 *            - {@link Location} of the spawn
	 * @param zone
	 *            - {@link ArenaZone} that the sign is for
	 * @param id
	 *            - The sign's ID
	 */
	public ArenaSign(UltimateArena plugin, Location loc, ArenaZone zone, int id)
	{
		this.plugin = plugin;
		this.location = loc;
		this.arena = zone;
		this.id = id;
		this.sign = getSign();
	}

	/**
	 * Gets the {@link Sign} instance
	 * 
	 * @return {@link Sign} instance
	 */
	public Sign getSign()
	{
		Block block = location.getWorld().getBlockAt(location);
		if (block.getState() instanceof Sign)
		{
			return (Sign) block.getState();
		}

		return null;
	}

	/**
	 * Updates the Sign
	 */
	public void update()
	{
		if (getSign() == null)
		{
			plugin.getSignHandler().deleteSign(this);
			return;
		}

		plugin.debug("Updating sign: {0}", id);

		sign.setLine(0, "[UltimateArena]");
		sign.setLine(1, "Click to Join");
		sign.setLine(2, arena.getArenaName());
		sign.setLine(3, getStatus());

		sign.update(true);
	}

	/**
	 * Gets the status of the {@link Arena}
	 * 
	 * @return Status of the {@link Arena}
	 */
	public String getStatus()
	{
		StringBuilder line = new StringBuilder();
		if (plugin.getArena(arena.getArenaName()) != null)
		{
			Arena a = plugin.getArena(arena.getArenaName());
			line.append(a.getGameMode().toString() + " (");
			line.append(a.getActivePlayers() + "/" + arena.getMaxPlayers() + ")");
		}
		else
		{
			if (arena.isDisabled())
			{
				line.append("DISABLED (0/0)");
			}
			else
			{
				line.append("IDLE (0/");
				line.append(arena.getMaxPlayers());
				line.append(")");
			}
		}

		return line.toString();
	}

	@Override
	public String toString()
	{
		StringBuilder ret = new StringBuilder();
		ret.append("ArenaSign {");
		ret.append("id=" + id + ", ");
		ret.append("loc=" + Util.locationToString(location));
		ret.append("}");

		return ret.toString();
	}
}