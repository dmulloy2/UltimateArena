package net.dmulloy2.ultimatearena;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaJoinTask extends BukkitRunnable 
{
	private final UltimateArena plugin;
	private final Player player;
	private final String name;
	private final boolean forced;
	
	public ArenaJoinTask(final UltimateArena plugin, final Player player, final String name, final boolean forced) 
	{
		this.plugin = plugin;
		this.player = player;
		this.name = name;
		this.forced = forced;
	}

	@Override
	public void run() 
	{
		plugin.waiting.remove(this);
		plugin.joinBattle(forced, player, name);
	}
	
	public final Player getPlayer()
	{
		return player;
	}
	
	public final String getArena()
	{
		return name;
	}
	
	public final boolean isForced()
	{
		return forced;
	}
}