/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.signs;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;

/**
 * @author dmulloy2
 */

public class StatusSign extends ArenaSign
{
	public static final SignType TYPE = SignType.STATUS;

	public StatusSign(UltimateArena plugin, Location loc, ArenaZone az, int id)
	{
		super(plugin, TYPE, loc, az, id);
	}

	public StatusSign(UltimateArena plugin, MemorySection section)
	{
		super(plugin, TYPE, section);
	}

	@Override
	public void update()
	{
		if (! ensureSign())
			return;

		sign.setLine(0, "[Arena Status]");
		sign.setLine(1, az.getName());

		StringBuilder line3 = new StringBuilder();

		if (az.isActive())
		{
			Arena ar = getArena();
			switch (ar.getGameMode())
			{
				case DISABLED:
					line3.append(ChatColor.RED + "Disabled");
					break;
				case IDLE:
					line3.append(ChatColor.YELLOW + "Idle");
					break;
				case INGAME:
					line3.append(ChatColor.GREEN + "In Game - ").append(ar.getGameTimer());
					break;
				case LOBBY:
					line3.append(ChatColor.GREEN + "Lobby - ").append(ar.getStartTimer());
					break;
				case STOPPING:
					line3.append(ChatColor.YELLOW + "Stopping");
					break;
				default:
					break;
				
			}
		}
		else
		{
			line3.append(ChatColor.YELLOW + "Idle");
		}

		sign.setLine(2, line3.toString());

		StringBuilder line4 = new StringBuilder();

		if (az.isActive())
		{
			Arena ar = getArena();
			switch (ar.getGameMode())
			{
				case DISABLED:
					line4.append("DISABLED (0/");
					line4.append(az.getConfig().getMaxPlayers());
					line4.append(")");
					break;
				case IDLE:
					line4.append("IDLE (0/");
					line4.append(az.getConfig().getMaxPlayers());
					line4.append(")");
					break;
				case INGAME:
					line4.append("INGAME (");
					line4.append(ar.getPlayerCount());
					line4.append("/");
					line4.append(az.getConfig().getMaxPlayers());
					line4.append(")");
					break;
				case LOBBY:
					line4.append("LOBBY (");
					line4.append(ar.getPlayerCount());
					line4.append("/");
					line4.append(az.getConfig().getMaxPlayers());
					line4.append(")");
					break;
				case STOPPING:
					line4.append("STOPPING (0/");
					line4.append(az.getConfig().getMaxPlayers());
					line4.append(")");
					break;
				default:
					break;
			}
		}
		else
		{
			if (az.isDisabled())
			{
				line4.append("DISABLED");
			}
			else
			{
				line4.append("IDLE");
			}

			line4.append(" (0/");
			line4.append(az.getConfig().getMaxPlayers());
			line4.append(")");
		}

		sign.setLine(3, line4.toString());
		sign.update();
	}

	@Override
	public void clear()
	{
		if (! ensureSign())
			return;

		sign.setLine(0, "[Arena Status]");
		sign.setLine(1, az.getName());

		if (az.isDisabled())
		{
			sign.setLine(2, ChatColor.RED + "Disabled");
			sign.setLine(3, "");
		}
		else
		{
			sign.setLine(2, ChatColor.YELLOW + "Idle");
			sign.setLine(3, "IDLE (0/" + az.getConfig().getMaxPlayers() + ")");
		}

		sign.update();
	}
}