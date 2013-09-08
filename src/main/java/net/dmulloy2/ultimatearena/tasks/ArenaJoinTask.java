package net.dmulloy2.ultimatearena.tasks;

import net.dmulloy2.ultimatearena.UltimateArena;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaJoinTask extends BukkitRunnable
{
	private final UltimateArena plugin;
	private final Player player;
	private final String name;

	public ArenaJoinTask(final UltimateArena plugin, final Player player, final String name)
	{
		this.plugin = plugin;
		this.player = player;
		this.name = name;
	}

	@Override
	public void run()
	{
		plugin.getWaiting().remove(this);

		plugin.fight(player, name);
	}

	public final Player getPlayer()
	{
		return player;
	}
}