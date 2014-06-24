package net.dmulloy2.ultimatearena.types;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * @author dmulloy2
 */

@Getter
public final class ArenaSign implements ConfigurationSerializable
{
	private final int id;
	private final String arenaName;
	private final ArenaLocation loc;

	private transient Sign sign;
	private transient ArenaZone az;
	private transient final UltimateArena plugin;

	/**
	 * Creates a new {@link ArenaSign}
	 *
	 * @param plugin
	 *        - {@link UltimateArena} plugin instance
	 * @param loc
	 *        - {@link Location} of the spawn
	 * @param zone
	 *        - {@link ArenaZone} that the sign is for
	 * @param id
	 *        - The sign's ID
	 */
	public ArenaSign(@NonNull UltimateArena plugin, @NonNull Location loc, @NonNull ArenaZone az, int id)
	{
		this.id = id;
		this.arenaName = az.getName();
		this.loc = new ArenaLocation(loc);

		this.getSign();
		this.az = az;
		this.plugin = plugin;
	}

	/**
	 * Constructs an ArenaSign from configuration
	 */
	public ArenaSign(@NonNull UltimateArena plugin, @NonNull Map<String, Object> args)
	{
		this.id = (int) args.get("id");
		this.arenaName = (String) args.get("arenaName");
		this.loc = (ArenaLocation) args.get("loc");

		this.getSign();
		this.az = plugin.getArenaZone(arenaName);
		this.plugin = plugin;
	}

	/**
	 * Updates the Sign
	 */
	public void update()
	{
		// Update the sign
		this.getSign();

		// Abort if the sign is null
		if (sign == null)
		{
			plugin.getSignHandler().deleteSign(this);
			return;
		}

		// Abort if the ArenaZone is null
		if (az == null)
		{
			sign.setLine(0, "[UltimateArena]");
			sign.setLine(1, FormatUtil.format("&4Null Arena"));
			sign.update();

			plugin.getSignHandler().deleteSign(this);
			return;
		}

		sign.setLine(0, "[UltimateArena]");
		sign.setLine(1, az.getName());

		// Line 3
		StringBuilder line = new StringBuilder();
		if (isActive())
		{
			Arena ar = getArena();
			switch (ar.getGameMode())
			{
				case LOBBY:
					line.append(FormatUtil.format("&aJoin - {0}", ar.getStartTimer()));
					break;
				case INGAME:
					line.append(FormatUtil.format("&eIn Game - {0}", ar.getGameTimer()));
					break;
				case DISABLED:
					line.append(FormatUtil.format("&4Disabled"));
					break;
				case IDLE:
					line.append(FormatUtil.format("&aJoin"));
					break;
				case STOPPING:
					line.append(FormatUtil.format("&eStopping"));
					break;
				default:
					break;
			}
		}
		else
		{
			line.append(FormatUtil.format("&aJoin"));
		}

		sign.setLine(2, line.toString());

		// Line 4
		line = new StringBuilder();
		if (isActive())
		{
			Arena ar = getArena();

			switch (ar.getGameMode())
			{
				case DISABLED:
					line.append("DISABLED (0/");
					line.append(az.getMaxPlayers());
					line.append(")");
					break;
				case IDLE:
					line.append("IDLE (0/");
					line.append(az.getMaxPlayers());
					line.append(")");
					break;
				case INGAME:
					line.append("INGAME (");
					line.append(ar.getPlayerCount());
					line.append("/");
					line.append(az.getMaxPlayers());
					line.append(")");
					break;
				case LOBBY:
					line.append("LOBBY (");
					line.append(ar.getPlayerCount());
					line.append("/");
					line.append(az.getMaxPlayers());
					line.append(")");
					break;
				case STOPPING:
					line.append("STOPPING (0/");
					line.append(az.getMaxPlayers());
					line.append(")");
					break;
				default:
					break;

			}
		}
		else
		{
			if (az.isDisabled())
			{
				line.append("DISABLED");
			}
			else
			{
				line.append("IDLE");
			}

			line.append(" (0/");
			line.append(az.getMaxPlayers());
			line.append(")");
		}

		sign.setLine(3, line.toString());
		sign.update();
	}

	private final void getSign()
	{
		Block block = loc.getWorld().getBlockAt(loc.getLocation());
		if (block.getState() instanceof Sign)
		{
			this.sign = (Sign) block.getState();
			return;
		}

		this.sign = null;
	}

	/**
	 * Clears the sign
	 */
	public final void clear()
	{
		// Update the sign
		this.getSign();

		// Abort if the sign is null
		if (sign == null)
		{
			plugin.getSignHandler().deleteSign(this);
			return;
		}

		// Abort if the ArenaZone is null
		if (az == null)
		{
			sign.setLine(0, "[UltimateArena]");
			sign.setLine(1, FormatUtil.format("&4Null Arena"));
			sign.update();

			plugin.getSignHandler().deleteSign(this);
			return;
		}

		sign.setLine(0, "[UltimateArena]");
		sign.setLine(1, az.getName());

		if (az.isDisabled())
		{
			sign.setLine(2, FormatUtil.format("&4Disabled"));
			sign.setLine(3, "");
		}
		else
		{
			sign.setLine(2, FormatUtil.format("&aJoin"));
			sign.setLine(3, "IDLE (0/" + az.getMaxPlayers() + ")");
		}

		sign.update();
	}

	private final boolean isActive()
	{
		return getArena() != null;
	}

	private final Arena getArena()
	{
		return plugin.getArena(az.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return "ArenaSign { id = " + id + ", arenaName = " + arenaName + ", loc = " + loc + " }";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> data = new HashMap<String, Object>();

		data.put("id", id);
		data.put("arenaName", arenaName);
		data.put("loc", loc);

		return data;
	}
}