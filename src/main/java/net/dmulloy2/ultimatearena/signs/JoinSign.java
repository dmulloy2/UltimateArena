/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.signs;

import lombok.Getter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;

/**
 * @author dmulloy2
 */

@Getter
public final class JoinSign extends ArenaSign
{
	public static final SignType TYPE = SignType.JOIN;

	public JoinSign(UltimateArena plugin, Location loc, ArenaZone az, int id)
	{
		super(plugin, TYPE, loc, az, id);
	}

	public JoinSign(UltimateArena plugin, MemorySection section)
	{
		super(plugin, TYPE, section);
	}

	@Override
	public void update()
	{
		if (! ensureSign())
			return;

		sign.setLine(0, "[UltimateArena]");
		sign.setLine(1, az.getName());

		// Line 3
		StringBuilder line = new StringBuilder();
		if (az.isActive())
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
		if (az.isActive())
		{
			Arena ar = getArena();

			switch (ar.getGameMode())
			{
				case DISABLED:
					line.append("DISABLED (0/");
					line.append(az.getConfig().getMaxPlayers());
					line.append(")");
					break;
				case IDLE:
					line.append("IDLE (0/");
					line.append(az.getConfig().getMaxPlayers());
					line.append(")");
					break;
				case INGAME:
					line.append("INGAME (");
					line.append(ar.getPlayerCount());
					line.append("/");
					line.append(az.getConfig().getMaxPlayers());
					line.append(")");
					break;
				case LOBBY:
					line.append("LOBBY (");
					line.append(ar.getPlayerCount());
					line.append("/");
					line.append(az.getConfig().getMaxPlayers());
					line.append(")");
					break;
				case STOPPING:
					line.append("STOPPING (0/");
					line.append(az.getConfig().getMaxPlayers());
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
			line.append(az.getConfig().getMaxPlayers());
			line.append(")");
		}

		sign.setLine(3, line.toString());
		sign.update();
	}

	@Override
	public final void clear()
	{
		if (! ensureSign())
			return;

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
			sign.setLine(3, "IDLE (0/" + az.getConfig().getMaxPlayers() + ")");
		}

		sign.update();
	}
}