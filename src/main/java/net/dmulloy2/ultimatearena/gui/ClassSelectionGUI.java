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

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornapi.gui.AbstractGUI;
import net.dmulloy2.ultimatearena.Config;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.integration.VaultHandler;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.swornapi.util.FormatUtil;
import net.dmulloy2.swornapi.util.NumberUtil;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author dmulloy2
 */

public class ClassSelectionGUI extends AbstractGUI
{
	private final List<ArenaClass> classes;
	private final ArenaPlayer ap;
	private final boolean spawnAfter;

	private final UltimateArena plugin;

	public ClassSelectionGUI(UltimateArena plugin, ArenaPlayer ap, boolean spawnAfter)
	{
		super(plugin, ap.getPlayer());
		this.spawnAfter = spawnAfter;
		this.plugin = plugin;
		this.ap = ap;

		this.classes = getClasses();
		this.setup();
	}

	private List<ArenaClass> getClasses()
	{
		List<ArenaClass> classes = new ArrayList<>();

		for (ArenaClass ac : ap.getArena().getAvailableClasses(ap.getTeam()))
		{
			if (Config.showUnavailableClasses || ac.checkAvailability(ap, false))
				classes.add(ac);
		}

		return classes;
	}

	@Override
	public int getSize()
	{
		return NumberUtil.roundUp(classes.size(), 9);
	}

	@Override
	public String getTitle()
	{
		return Config.classSelectorTitle;
	}

	@Override
	public void stock(Inventory inventory)
	{
		for (ArenaClass ac : classes)
		{
			ItemStack icon = ac.getIcon();
			ItemMeta meta = icon.getItemMeta();
			List<String> lore = meta.getLore();

			// Show cost if applicable
			double cost = ac.getCost();
			if (cost > 0.0D && plugin.isVaultEnabled())
			{
				VaultHandler handler = plugin.getVaultHandler();
				String color = handler.has(player, cost) ? "&a" : "&c";
				lore.add(FormatUtil.format(plugin.getMessage("gui.cost"), color, handler.format(cost)));
			}

			// Show the reason they can't it use if applicable
			if (Config.showUnavailableClasses)
			{
				Arena arena = ap.getArena();
				if (! ac.hasPermission(player))
				{
					lore.add(FormatUtil.format(plugin.getMessage("guiNoPermission")));
				}
				else if (! arena.isValidClass(ac))
				{
					lore.add(FormatUtil.format(plugin.getMessage("guiUnavailableArena")));
				}
				else if (! arena.getAvailableClasses(ap.getTeam()).contains(ac))
				{
					lore.add(FormatUtil.format(plugin.getMessage("guiUnavailableTeam")));
				}
			}

			inventory.addItem(icon);
		}
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event)
	{
		ItemStack current = event.getCurrentItem();
		if (current != null)
		{
			ArenaClass ac = plugin.getArenaClass(current);
			if (ac != null)
			{
				if (! ac.checkAvailability(ap))
					return;

				if (ap.setClass(ac))
				{
					String name = ac.getName();
					String article = FormatUtil.getArticle(name);
					String spawn = ap.getArena().isInGame() ? "respawn" : "spawn";

					sendpMessage(plugin.getMessage("classSpawn"), spawn, article, name);
				}
			}
		}

		event.setCancelled(true);
		player.closeInventory();

		if (spawnAfter)
			ap.getArena().spawn(ap);
	}

	@Override
	public void onInventoryClose(InventoryCloseEvent event)
	{
		if (spawnAfter)
			ap.getArena().spawn(ap);
	}
}
