package net.dmulloy2.ultimatearena.tasks;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class ArenaJoinTask extends BukkitRunnable
{
	private final String arenaName;
	private final String playerName;
	private final UltimateArena plugin;
	public ArenaJoinTask(String playerName, String arenaName, UltimateArena plugin)
	{
		this.playerName = playerName;
		this.arenaName = arenaName;
		this.plugin = plugin;
	}

	@Override
	public void run()
	{
		Player player = getPlayer();
		if (player != null)
		{
			plugin.join(player, arenaName);
		}

		plugin.getWaiting().remove(playerName);
	}

	public final Player getPlayer()
	{
		return Util.matchPlayer(playerName);
	}
}