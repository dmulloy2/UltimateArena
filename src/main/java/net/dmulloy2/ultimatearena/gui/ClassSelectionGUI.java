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
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class ClassSelectionGUI extends AbstractGUI
{
	private final UltimateArena plugin;
	public ClassSelectionGUI(UltimateArena plugin, Player player)
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
		return FormatUtil.format(plugin.getConfig().getString("classSelector.title", "         &l&nSelect a class!&r"));
	}

	@Override
	public void stock(Inventory inventory)
	{
		ArenaPlayer ap = plugin.getArenaPlayer(player);
		if (ap != null)
		{
			Arena arena = ap.getArena();
			for (ArenaClass ac : plugin.getClasses())
			{
				if (ac.checkPermission(player) && arena.isValidClass(ac))
					inventory.addItem(ac.getIcon());
			}
		}
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event)
	{
		ArenaPlayer ap = plugin.getArenaPlayer(player);
		if (ap != null)
		{
			ItemStack current = event.getCurrentItem();
			if (current != null)
			{
				ArenaClass ac = plugin.getArenaClass(current);
				if (ac != null)
				{
					ap.setClass(ac);

					if (ap.getArena().isInLobby())
					{
						String name = ac.getName();
						String article = FormatUtil.getArticle(name);
						String spawn = ap.getArena().isInGame() ? "respawn" : "spawn";

						sendpMessage("&3You will {0} as {1} &e{2}", spawn, article, name);
					}
				}
			}
		}

		event.setCancelled(true);
		player.closeInventory();
	}
}
