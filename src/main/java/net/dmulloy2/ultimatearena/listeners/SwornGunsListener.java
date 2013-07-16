package net.dmulloy2.ultimatearena.listeners;

import net.dmulloy2.swornguns.events.SwornGunsFireEvent;
import net.dmulloy2.ultimatearena.UltimateArena;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author dmulloy2
 */

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
				if (plugin.getArena(pl).getType().getName().equalsIgnoreCase("Hunger"))
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