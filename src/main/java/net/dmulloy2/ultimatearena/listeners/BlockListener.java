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
package net.dmulloy2.ultimatearena.listeners;

import lombok.AllArgsConstructor;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.signs.ArenaSign;
import net.dmulloy2.ultimatearena.signs.JoinSign;
import net.dmulloy2.ultimatearena.signs.LastGameSign;
import net.dmulloy2.ultimatearena.signs.StatusSign;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

/**
 * @author dmulloy2
 */

@AllArgsConstructor
public class BlockListener implements Listener
{
	private final UltimateArena plugin;

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event)
	{
		Block block = event.getBlock();
		if (plugin.isInArena(block.getLocation()))
		{
			ArenaPlayer ap = plugin.getArenaPlayer(event.getPlayer());
			if (ap != null)
			{
				Arena arena = ap.getArena();
				if (! arena.getConfig().isCanModifyWorld())
				{
					ap.sendMessage(plugin.getMessage("cantBreak"));
					event.setCancelled(true);
				}
			}
			else
			{
				Player player = event.getPlayer();
				if (! plugin.getPermissionHandler().hasPermission(player, Permission.BUILD))
				{
					player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("cantBreak")));
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Block block = event.getBlock();
		if (plugin.isInArena(block.getLocation()))
		{
			ArenaPlayer ap = plugin.getArenaPlayer(event.getPlayer());
			if (ap != null)
			{
				Arena arena = ap.getArena();
				if (! arena.getConfig().isCanModifyWorld())
				{
					ap.sendMessage(plugin.getMessage("cantBuild"));
					event.setCancelled(true);
				}
			}
			else
			{
				Player player = event.getPlayer();
				if (! plugin.getPermissionHandler().hasPermission(player, Permission.BUILD))
				{
					player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("cantBuild")));
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event)
	{
		String line1 = event.getLine(0);
		if (line1.endsWith("Arena]"))
		{
			if (plugin.getPermissionHandler().hasPermission(event.getPlayer(), Permission.BUILD))
			{
				if (event.getLine(1).equalsIgnoreCase("Click to Join"))
				{
					if (event.getLine(2).equalsIgnoreCase("Auto Assign"))
						return;

					ArenaZone az = plugin.getArenaZone(event.getLine(2));
					if (az != null)
					{
						int id = plugin.getSignHandler().getFreeId(1);

						ArenaSign sign = new JoinSign(plugin, event.getBlock().getLocation(), az, id);
						plugin.getSignHandler().addSign(sign);

						event.getPlayer().sendMessage(plugin.getPrefix() + FormatUtil.format("&aCreated new Join Sign!"));
					}
					else
					{
						event.setLine(0, FormatUtil.format("[UltimateArena]"));
						event.setLine(1, FormatUtil.format("&4Invalid Arena"));
						event.setLine(2, "");
						event.setLine(3, "");
					}
				}
				else if (event.getLine(1).equalsIgnoreCase("Classes"))
				{
					// This will open a class selection gui
					return;
				}
				else
				{
					ArenaZone az = plugin.getArenaZone(event.getLine(1));
					if (az != null)
					{
						int id = plugin.getSignHandler().getFreeId(1);

						ArenaSign sign = new JoinSign(plugin, event.getBlock().getLocation(), az, id);
						plugin.getSignHandler().addSign(sign);

						event.getPlayer().sendMessage(plugin.getPrefix() + FormatUtil.format("&aCreated new Join Sign!"));
					}
					else
					{
						event.setLine(0, FormatUtil.format("[UltimateArena]"));
						event.setLine(1, FormatUtil.format("&4Invalid Arena"));
						event.setLine(2, "");
						event.setLine(3, "");
					}
				}
			}
			else
			{
				event.setLine(0, FormatUtil.format("[UltimateArena]"));
				event.setLine(1, FormatUtil.format("&4No permission"));
				event.setLine(2, "");
				event.setLine(3, "");
			}
		}
		else if (line1.equalsIgnoreCase("[Arena Status]"))
		{
			if (plugin.getPermissionHandler().hasPermission(event.getPlayer(), Permission.BUILD))
			{
				ArenaZone az = plugin.getArenaZone(event.getLine(1));
				if (az != null)
				{
					int id = plugin.getSignHandler().getFreeId(1);

					ArenaSign sign = new StatusSign(plugin, event.getBlock().getLocation(), az, id);
					plugin.getSignHandler().addSign(sign);

					event.getPlayer().sendMessage(plugin.getPrefix() + FormatUtil.format("&aCreated new Status Sign!"));
				}
				else
				{
					event.setLine(0, FormatUtil.format("[UltimateArena]"));
					event.setLine(1, FormatUtil.format("&4Invalid Arena"));
					event.setLine(2, "");
					event.setLine(3, "");
				}
			}
			else
			{
				event.setLine(0, FormatUtil.format("[UltimateArena]"));
				event.setLine(1, FormatUtil.format("&4No permission"));
				event.setLine(2, "");
				event.setLine(3, "");
			}
		}
		else if (line1.equalsIgnoreCase("[Last Game]"))
		{
			if (plugin.getPermissionHandler().hasPermission(event.getPlayer(), Permission.BUILD))
			{
				ArenaZone az = plugin.getArenaZone(event.getLine(1));
				if (az != null)
				{
					int id = plugin.getSignHandler().getFreeId(1);

					ArenaSign sign = new LastGameSign(plugin, event.getBlock().getLocation(), az, id);
					plugin.getSignHandler().addSign(sign);

					event.getPlayer().sendMessage(plugin.getPrefix() + FormatUtil.format("&aCreated new Leaderboard Sign!"));
				}
				else
				{
					event.setLine(0, FormatUtil.format("[UltimateArena]"));
					event.setLine(1, FormatUtil.format("&4Invalid Arena"));
					event.setLine(2, "");
					event.setLine(3, "");
				}
			}
			else
			{
				event.setLine(0, FormatUtil.format("[UltimateArena]"));
				event.setLine(1, FormatUtil.format("&4No permission"));
				event.setLine(2, "");
				event.setLine(3, "");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSignBreak(BlockBreakEvent event)
	{
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if (block.getState() instanceof Sign)
		{
			Sign s = (Sign) block.getState();
			String line1 = s.getLine(0);

			if (line1.endsWith("Arena]") || line1.equalsIgnoreCase("[Arena Status]")
					|| line1.startsWith("[Last "))
			{
				ArenaSign sign = plugin.getSignHandler().getSign(block.getLocation());
				if (sign != null)
				{
					if (plugin.getPermissionHandler().hasPermission(player, Permission.BUILD))
					{
						plugin.getSignHandler().deleteSign(sign);
						player.sendMessage(plugin.getPrefix() + FormatUtil.format("&cSuccessfully removed arena sign!"));
					}
					else
					{
						event.setCancelled(true);
						player.sendMessage(plugin.getPrefix() + FormatUtil.format("&cPermission denied!"));
					}
				}
			}
		}
	}
}
