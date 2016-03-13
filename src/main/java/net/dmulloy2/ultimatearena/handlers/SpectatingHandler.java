/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2012 - 2015 MineSworn
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.ultimatearena.handlers;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.gui.PlayerSelectionGUI;
import net.dmulloy2.ultimatearena.types.ArenaSpectator;
import net.dmulloy2.util.CompatUtil;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * @author dmulloy2
 */

public class SpectatingHandler implements Listener
{
	private boolean registered;
	private final UltimateArena plugin;

	public SpectatingHandler(UltimateArena plugin)
	{
		this.plugin = plugin;
	}

	// ---- Spectator Management

	public final boolean isSpectating(Player player)
	{
		return getSpectator(player) != null;
	}

	public ArenaSpectator getSpectator(Player player)
	{
		for (Arena arena : plugin.getActiveArenas())
		{
			for (ArenaSpectator spectator : arena.getSpectators())
			{
				if (spectator.getUniqueId().equals(player.getUniqueId().toString()))
					return spectator;
			}
		}

		return null;
	}

	public final ArenaSpectator addSpectator(Arena arena, Player player)
	{
		// Lazy register
		if (! registered)
		{
			plugin.getServer().getPluginManager().registerEvents(this, plugin);
			registered = true;
		}

		ArenaSpectator spectator = new ArenaSpectator(player, arena, plugin);
		spectator.spawn();

		arena.getSpectators().add(spectator);
		return spectator;
	}

	public final void removeSpectator(ArenaSpectator spectator)
	{
		spectator.endPlayer();
		spectator.getPlayer().closeInventory();
		spectator.getArena().getSpectators().remove(spectator);
	}

	// ---- Events

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		if (isSpectating(player))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockDamage(BlockDamageEvent event)
	{
		Player player = event.getPlayer();
		if (isSpectating(player))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		if (isSpectating(player))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if (isSpectating(player))
		{
			if (CompatUtil.getItemInMainHand(player).getType() == Material.COMPASS)
			{
				PlayerSelectionGUI psGUI = new PlayerSelectionGUI(plugin, player);
				plugin.getGuiHandler().open(player, psGUI);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		Player player = event.getPlayer();
		if (isSpectating(player))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();
		if (isSpectating(player))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if (event.getDamager() instanceof Player)
		{
			Player player = (Player) event.getDamager();
			if (isSpectating(player))
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event)
	{
		Player player = null;
		if (event.getEntity() instanceof Player)
		{
			player = (Player) event.getEntity();
		}
		else if (event.getEntity() instanceof Projectile)
		{
			Projectile proj = (Projectile) event.getEntity();
			if (proj.getShooter() instanceof Player)
				player = (Player) proj.getShooter();
		}

		if (player != null)
		{
			if (isSpectating(player))
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerMoveLowest(PlayerMoveEvent event)
	{
		if (! event.isCancelled())
		{
			Player player = event.getPlayer();
			if (isSpectating(player))
			{
				if (! plugin.isInArena(event.getTo()))
				{
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityTarget(EntityTargetEvent event)
	{
		Entity entity = event.getTarget();
		if (entity instanceof Player)
		{
			Player player = (Player) entity;
			if (isSpectating(player))
			{
				event.setCancelled(true);
			}
		}
	}
}
