package net.dmulloy2.ultimatearena.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.dmulloy2.swornguns.events.SwornGunFireEvent;
import net.dmulloy2.swornguns.events.SwornGunsBlockDamageEvent;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;

public class SwornGunsListener implements Listener
{
	private final UltimateArena plugin;

	public SwornGunsListener(UltimateArena plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true)
	public void onSwornGunsFire(SwornGunFireEvent event)
	{
		ArenaPlayer ap = plugin.getArenaPlayer(event.getPlayer());
		if (ap != null)
		{
			if (ap.getArenaClass().isUnlimitedAmmo())
			{
				event.setAmmoNeeded(0);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onSwornGunsBlockDamage(SwornGunsBlockDamageEvent event)
	{
		if (plugin.isInArena(event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}
}
