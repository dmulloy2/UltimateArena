package com.orange451.UltimateArena.listeners;

import net.dmulloy2.swornguns.events.SwornGunsFireEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.orange451.UltimateArena.UltimateArena;

public class SwornGunsListener implements Listener
{
	private final UltimateArena plugin;
	public SwornGunsListener(final UltimateArena plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerGunFire(final SwornGunsFireEvent event)
	{
		Player pl = event.getShooterAsPlayer();
		if (pl != null)
		{
			if (plugin.isInArena(pl)) 
			{
				/**Hunger Games Check**/
				if (plugin.getArena(pl).type.equals("Hunger"))
				{
					event.setCancelled(true);
					return;
				}
					
				/**If its a gun in a regular arena, unlimited ammo!**/
				if (plugin.getConfig().getBoolean("unlimitedAmmo", true))
				{
					event.setAmountAmmoNeeded(0);
				}
			}
		}
	}
}