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
package net.dmulloy2.ultimatearena.gui;

import net.dmulloy2.gui.AbstractGUI;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaSpectator;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.ChatColor;
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
		this.setup();
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
				ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
				SkullMeta meta = (SkullMeta) head.getItemMeta();
				meta.setDisplayName(ChatColor.RESET + ap.getName());
				meta.setOwningPlayer(ap.getPlayer());
				head.setItemMeta(meta);

				inventory.addItem(head);
			}
		}
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event)
	{
		ArenaSpectator spectator = plugin.getSpectatingHandler().getSpectator(player);
		if (spectator != null)
		{
			Arena arena = spectator.getArena();
			ItemStack current = event.getCurrentItem();
			if (current != null && current.getType() == Material.PLAYER_HEAD)
			{
				SkullMeta meta = (SkullMeta) current.getItemMeta();
				for (ArenaPlayer ap : arena.getActive())
				{
					if (ap.getPlayer().equals(meta.getOwningPlayer()))
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
