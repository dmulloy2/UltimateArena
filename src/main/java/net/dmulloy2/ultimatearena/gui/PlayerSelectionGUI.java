/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.gui;

import net.dmulloy2.gui.AbstractGUI;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaSpectator;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * @author dmulloy2
 */

public class PlayerSelectionGUI extends AbstractGUI
{
	private final UltimateArena plugin;
	public PlayerSelectionGUI(UltimateArena plugin, Player player)
	{
		super(plugin, player);
		this.plugin = plugin;
	}

	@Override
	public int getSize()
	{
		return 27;
	}

	@Override
	public String getTitle()
	{
		return FormatUtil.format("        &l&nSelect a player!&r");
	}

	@Override
	public void stock(Inventory inventory)
	{
		ArenaSpectator spectator = plugin.getSpectatingHandler().getSpectator(player);
		if (spectator != null)
		{
			Arena arena = spectator.getArena();
			for (ArenaPlayer ap : arena.getActive())
			{
				ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				SkullMeta meta = (SkullMeta) head.getItemMeta();
				meta.setDisplayName(ap.getName() + "''s Head");
				meta.setOwner(ap.getName());
				head.setItemMeta(meta);

				inventory.addItem(head);
			}
		}
	}

	@Override
	public void onInventoryClick(Player player, InventoryClickEvent event)
	{
		ArenaSpectator spectator = plugin.getSpectatingHandler().getSpectator(player);
		if (spectator != null)
		{
			Arena arena = spectator.getArena();
			ItemStack current = event.getCurrentItem();
			if (current != null && current.getType() == Material.SKULL_ITEM)
			{
				SkullMeta meta = (SkullMeta) current.getItemMeta();
				for (ArenaPlayer ap : arena.getActive())
				{
					if (ap.getName().equals(meta.getOwner()))
					{
						spectator.teleport(ap.getPlayer().getLocation());
						break;
					}
				}
			}
		}

		event.setCancelled(true);
		player.closeInventory();
	}
}