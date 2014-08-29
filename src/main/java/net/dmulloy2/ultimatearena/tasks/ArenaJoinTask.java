/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.tasks;

import lombok.AllArgsConstructor;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

@AllArgsConstructor
public class ArenaJoinTask extends BukkitRunnable
{
	private final String name;
	private final String arenaName;
	private final UltimateArena plugin;

	@Override
	public void run()
	{
		Player player = getPlayer();
		if (player != null)
			plugin.addPlayer(player, arenaName);

		plugin.getWaiting().remove(name);
	}

	public final Player getPlayer()
	{
		return Util.matchPlayer(name);
	}
}