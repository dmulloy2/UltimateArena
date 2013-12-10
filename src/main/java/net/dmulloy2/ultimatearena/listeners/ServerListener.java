package net.dmulloy2.ultimatearena.listeners;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.util.TimeUtil;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class ServerListener implements Listener
{
	private final UltimateArena plugin;
	public ServerListener(UltimateArena plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(PluginEnableEvent event)
	{
		// If we are delaying startup until after Multiverse loads, check for it here
		// If multiverse is the plugin being loaded, then wait 10 seconds and load our arenas
		// This also accounts for if event.isAsynchronous()
		if (plugin.isDelayedStartup())
		{
			if (event.getPlugin().getName().equalsIgnoreCase("Multiverse-Core"))
			{
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						plugin.loadFiles();
					}
				}.runTaskLater(plugin, TimeUtil.toTicks(10));
			}
		}
	}
}