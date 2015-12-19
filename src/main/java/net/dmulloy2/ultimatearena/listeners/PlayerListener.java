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
import net.dmulloy2.ultimatearena.arenas.spleef.SpleefArena;
import net.dmulloy2.ultimatearena.gui.ClassSelectionGUI;
import net.dmulloy2.ultimatearena.tasks.ArenaJoinTask;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaCreator;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaSpectator;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Field3D;
import net.dmulloy2.ultimatearena.types.LeaveReason;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

@AllArgsConstructor
public class PlayerListener implements Listener
{
	private final UltimateArena plugin;

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		ArenaCreator ac = plugin.getArenaCreator(player);
		if (ac != null)
		{
			plugin.getMakingArena().remove(ac);
			plugin.log("{0} stopping the creation of {1} from quit.", player.getName(), ac.getArenaName());
		}

		ArenaPlayer ap = null;
		while ((ap = plugin.getArenaPlayer(player, true)) != null)
		{
			if (! ap.isOut())
			{
				ap.leaveArena(LeaveReason.QUIT);
				plugin.log("{0} leaving arena {1} from quit.", ap.getName(), ap.getArena().getName());
			}

			ap.clear();
		}

		ArenaJoinTask task = plugin.getWaiting().get(player.getName());
		if (task != null)
		{
			task.cancel();
			plugin.getWaiting().remove(player.getName());
		}

		ArenaSpectator as = plugin.getSpectatingHandler().getSpectator(player);
		if (as != null)
		{
			plugin.getSpectatingHandler().removeSpectator(as);
			as.clear();
		}

		if (player.hasMetadata("UA"))
			player.removeMetadata("UA", plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		ArenaPlayer ap = plugin.getArenaPlayer(event.getPlayer());
		if (ap != null)
		{
			Arena arena = ap.getArena();
			if (! arena.getConfig().isCanModifyWorld())
			{
				event.setCancelled(true);

				// Dynamically clean up items
				Item item = event.getItem();
				if (arena.isInside(item.getLocation()))
					item.remove();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		ArenaPlayer ap = plugin.getArenaPlayer(event.getPlayer());
		if (ap != null)
		{
			Arena arena = ap.getArena();
			if (! arena.getConfig().isCanModifyWorld())
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (block != null)
		{
			Action action = event.getAction();
			if (action == Action.RIGHT_CLICK_BLOCK)
			{
				if (block.getState() instanceof Sign)
				{
					Sign sign = (Sign) block.getState();
					String line1 = sign.getLine(0);

					if (line1.equalsIgnoreCase("[UltimateArena]"))
					{
						ArenaPlayer ap = plugin.getArenaPlayer(player);
						if (ap != null)
						{
							if (sign.getLine(1).equalsIgnoreCase("Classes"))
							{
								ClassSelectionGUI csGUI = new ClassSelectionGUI(plugin, player);
								plugin.getGuiHandler().open(player, csGUI);
								return;
							}
						}
						else
						{
							if (plugin.getPermissionHandler().hasPermission(player, Permission.JOIN))
							{
								ArenaZone az = plugin.getArenaZone(sign.getLine(1));
								if (az != null)
								{
									plugin.attemptJoin(player, az.getName());
									return;
								}

								if (sign.getLine(2).equalsIgnoreCase("Auto Assign"))
								{
									for (Arena arena : plugin.getActiveArenas())
									{
										if (arena.isInLobby())
										{
											if (arena.getPlayerCount() < arena.getMaxPlayers())
											{
												plugin.attemptJoin(player, arena.getName());
												return;
											}
										}
									}

									for (ArenaZone inactive : plugin.getLoadedArenas())
									{
										if (! inactive.isActive())
										{
											plugin.attemptJoin(player, inactive.getName());
											return;
										}
									}
								}
							}
						}
					}
					else if (line1.equalsIgnoreCase("[Arena Status]"))
					{
						// Don't do anything
						return;
					}
					else if (line1.equalsIgnoreCase("[Last Game]"))
					{
						// Don't do anything
						return;
					}
					else
					{
						ArenaPlayer ap = plugin.getArenaPlayer(player);
						if (ap != null)
						{
							ArenaClass ac = plugin.getArenaClass(line1);
							if (ac != null)
							{
								if (! ac.checkAvailability(ap))
									return;

								if (ap.setClass(ac))
								{
									String name = ac.getName();
									String article = FormatUtil.getArticle(name);
									String spawn = ap.getArena().isInGame() ? "respawn" : "spawn";

									ap.sendMessage(plugin.getMessage("classSpawn"), spawn, article, name);
									return;
								}
							}
						}
					}
				}
			}
			else if (action == Action.LEFT_CLICK_BLOCK)
			{
				ArenaPlayer ap = plugin.getArenaPlayer(player);
				if (ap != null)
				{
					Arena arena = ap.getArena();
					if (arena instanceof SpleefArena)
					{
						if (arena.isInGame())
						{
							SpleefArena spleef = (SpleefArena) arena;
							Field3D field = spleef.getSpleefGround();
							if (field.isInside(block.getLocation()))
							{
								block.setType(Material.AIR);
								return;
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		// If the player is in an arena, respawn them a second later,
		// Since plugins like Multiverse and Essentials generally take
		// priority with respawning

		final ArenaPlayer ap = plugin.getArenaPlayer(event.getPlayer());
		if (ap != null)
		{
			final Arena arena = ap.getArena();
			if (ap.getDeaths() < arena.getMaxDeaths())
			{
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						arena.spawn(ap);
					}
				}.runTaskLater(plugin, 1L);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMoveMonitor(PlayerMoveEvent event)
	{
		// If they didnt move, don't do anything.
		if (event.getFrom().getBlockX() == event.getTo().getBlockX()
				&& event.getFrom().getBlockZ() == event.getTo().getBlockZ())
			return;

		Player player = event.getPlayer();
		ArenaJoinTask task = plugin.getWaiting().get(player.getName());
		if (task != null)
		{
			task.cancel();
			plugin.getWaiting().remove(player.getName());
			player.sendMessage(plugin.getPrefix() + FormatUtil.format("&cCancelled!"));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerMoveLowest(PlayerMoveEvent event)
	{
		ArenaPlayer ap = plugin.getArenaPlayer(event.getPlayer());
		if (ap != null)
		{
			Arena arena = ap.getArena();
			if (! arena.isInside(event.getTo()))
			{
				if (! arena.isInside(event.getFrom()))
				{
					ap.getArena().spawn(ap);
				}
				else
				{
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		if (event.getCause() == TeleportCause.COMMAND)
		{
			ArenaPlayer ap = plugin.getArenaPlayer(event.getPlayer());
			if (ap != null)
			{
				ap.sendMessage(plugin.getMessage("teleportInArena"));
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		ArenaPlayer ap = plugin.getArenaPlayer(player);
		if (ap != null)
		{
			if (! plugin.getPermissionHandler().hasPermission(player, Permission.BYPASS))
			{
				String cmd = event.getMessage();
				if (! cmd.startsWith("/ua") && ! plugin.isWhitelistedCommand(cmd))
				{
					ap.sendMessage(plugin.getMessage("nonUaCommand"));
					ap.sendMessage(plugin.getMessage("commandsAgain"));
					event.setCancelled(true);
				}
			}
		}
	}
}
