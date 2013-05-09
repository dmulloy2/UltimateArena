package com.orange451.UltimateArena.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.pvpgunplus.events.PVPGunPlusFireGunEvent;

public class PVPGunPlusListener implements Listener
{
	UltimateArena plugin;
	public PVPGunPlusListener(UltimateArena plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerGunFire(PVPGunPlusFireGunEvent event)
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
				event.setCancelled(false);
				event.setAmountAmmoNeeded(0);
			}
		}
	}
}