package net.dmulloy2.ultimatearena.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaSpectator;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * @author dmulloy2
 */

public class SpectatingHandler implements Listener
{
	private List<String> browsingInventory = new ArrayList<String>();
	private HashMap<Arena, List<ArenaSpectator>> spectating = new HashMap<Arena, List<ArenaSpectator>>();

	private final UltimateArena plugin;
	public SpectatingHandler(UltimateArena plugin)
	{
		this.plugin = plugin;

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	// ---- Arena Management ---- //

	public void registerArena(Arena arena)
	{
		spectating.put(arena, new ArrayList<ArenaSpectator>());
	}

	public void unregisterArena(Arena arena)
	{
		for (ArenaSpectator spectator : spectating.get(arena))
		{
			spectator.endPlayer();
		}

		spectating.get(arena).clear();
		spectating.remove(arena);
	}

	public Arena getArena(ArenaSpectator spectator)
	{
		for (Entry<Arena, List<ArenaSpectator>> entry : spectating.entrySet())
		{
			if (entry.getValue().contains(spectator))
				return entry.getKey();
		}

		return null;
	}

	// ---- Spectator Management ---- //

	public ArenaSpectator addSpectator(Arena arena, Player player)
	{
		ArenaSpectator spectator = new ArenaSpectator(player, arena, plugin);

		spectator.spawn();

		spectating.get(arena).add(spectator);

		return spectator;
	}

	public void removeSpectator(ArenaSpectator spectator)
	{
		spectator.endPlayer();

		spectating.values().remove(spectator);

		closeInventory(spectator.getPlayer());
	}

	public boolean isSpectating(Player player)
	{
		return getSpectator(player) != null;
	}

	public ArenaSpectator getSpectator(Player player)
	{
		for (List<ArenaSpectator> spectators : spectating.values())
		{
			for (ArenaSpectator spectator : spectators)
			{
				if (spectator.getName().equals(player.getName()))
					return spectator;
			}
		}

		return null;
	}

	// ---- Inventory ---- //

	public void openInventory(Player p, Arena a)
	{
		String name = FormatUtil.format("&4&lActive Players");
		Inventory inventory = plugin.getServer().createInventory(p, 27, name);

		for (ArenaPlayer pl : a.getActivePlayers())
		{
			ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta meta = (SkullMeta) skull.getItemMeta();
			meta.setOwner(pl.getName());
			skull.setItemMeta(meta);
			inventory.addItem(skull);
		}

		p.openInventory(inventory);
		browsingInventory.add(p.getName());
	}

	public void closeInventory(Player p)
	{
		if (isBrowsingInventory(p))
		{
			browsingInventory.remove(p.getName());
			p.closeInventory();
		}
	}

	public boolean isBrowsingInventory(Player p)
	{
		return browsingInventory.contains(p.getName());
	}

	// ---- Events ---- //

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
	public void onPlayerClickEvent(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if (isSpectating(player))
		{
			ArenaSpectator spectator = getSpectator(player);
			if (player.getItemInHand().getType() == Material.COMPASS)
			{
				Arena arena = getArena(spectator);
				if (arena != null)
				{
					event.setCancelled(true);
					openInventory(player, arena);
				}
				else
				{
					removeSpectator(spectator);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event)
	{
		if (event.getWhoClicked() instanceof Player)
		{
			Player player = (Player) event.getWhoClicked();
			if (isBrowsingInventory(player))
			{
				ItemStack stack = event.getCurrentItem();
				if (stack.getType() == Material.SKULL_ITEM)
				{
					SkullMeta meta = (SkullMeta) stack.getItemMeta();
					if (meta.hasOwner())
					{
						Player pl = Util.matchPlayer(meta.getOwner());
						if (pl != null)
						{
							if (plugin.isInArena(pl))
							{
								event.setCancelled(true);
								player.teleport(pl);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClose(InventoryCloseEvent event)
	{
		if (event.getPlayer() instanceof Player)
		{
			Player player = (Player) event.getPlayer();
			if (isBrowsingInventory(player))
			{
				closeInventory(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemPickup(PlayerPickupItemEvent event)
	{
		Player player = event.getPlayer();
		if (isSpectating(player))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemDrop(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();
		if (isSpectating(player))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageByEntityEvent event)
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
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
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
				if (! plugin.isInArena(event.getFrom()))
				{
					event.setCancelled(true);
				}
			}
		}
	}
}