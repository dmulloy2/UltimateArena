package net.dmulloy2.ultimatearena.tasks;

import java.util.UUID;

import lombok.AllArgsConstructor;
import net.dmulloy2.ultimatearena.UltimateArena;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

@AllArgsConstructor
public class ArenaJoinTask extends BukkitRunnable
{
	private final UUID uniqueId;
	private final String arenaName;
	private final UltimateArena plugin;

	@Override
	public void run()
	{
		Player player = getPlayer();
		if (player != null)
		{
			plugin.join(player, arenaName);
		}

		plugin.getWaiting().remove(uniqueId);
	}

	public final Player getPlayer()
	{
		return plugin.getServer().getPlayer(uniqueId);
	}
}