package net.dmulloy2.ultimatearena.listeners;

import lombok.AllArgsConstructor;
import net.dmulloy2.gui.GUIHandler;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.SPLEEFArena;
import net.dmulloy2.ultimatearena.creation.ArenaCreator;
import net.dmulloy2.ultimatearena.gui.ClassSelectionGUI;
import net.dmulloy2.ultimatearena.tasks.ArenaJoinTask;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
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

			plugin.outConsole("{0} stopping the creation of {1} from quit", player.getName(), ac.getArenaName());
		}

		ArenaPlayer ap = plugin.getArenaPlayer(player);
		if (ap != null)
		{
			ap.leaveArena(LeaveReason.QUIT);

			plugin.outConsole("{0} leaving arena {1} from quit", ap.getName(), ap.getArena().getName());
		}

		ArenaJoinTask task = plugin.getWaiting().get(player.getUniqueId());
		if (task != null)
		{
			task.cancel();
			plugin.getWaiting().remove(player.getUniqueId());
		}

		if (plugin.getSpectatingHandler().isSpectating(player))
		{
			plugin.getSpectatingHandler().removeSpectator(player);
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
					if (sign.getLine(0).equalsIgnoreCase("[UltimateArena]"))
					{
						ArenaPlayer ap = plugin.getArenaPlayer(player);
						if (ap != null)
						{
							if (sign.getLine(1).equalsIgnoreCase("Classes"))
							{
								ClassSelectionGUI csGUI = new ClassSelectionGUI(plugin, player);
								GUIHandler.openGUI(player, csGUI);
								return;
							}
						}
						else
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
										if (arena.getPlayerCount() < arena.getAz().getMaxPlayers())
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
					else
					{
						ArenaPlayer ap = plugin.getArenaPlayer(player);
						if (ap != null)
						{
							ArenaClass ac = plugin.getArenaClass(sign.getLine(0));
							if (ac != null)
							{
								if (! ac.checkPermission(player))
								{
									ap.sendMessage("&cYou do not have the necessary perms for this class");
									return;
								}

								if (ap.setClass(ac))
								{
									String name = ac.getName();
									String article = FormatUtil.getArticle(name);
									String spawn = ap.getArena().isInGame() ? "respawn" : "spawn";

									ap.sendMessage("&3You will {0} as {1}: &e{2}", spawn, article, name);
									return;
								}

								ap.sendMessage("&cYou cannot use this class in this arena!");
								return;
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
					if (arena instanceof SPLEEFArena)
					{
						if (arena.isInGame())
						{
							SPLEEFArena spleef = (SPLEEFArena) arena;
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
		if (plugin.isPlayerWaiting(player))
		{
			ArenaJoinTask task = plugin.getWaiting().get(player.getUniqueId());

			task.cancel();
			plugin.getWaiting().remove(player.getUniqueId());

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
				ap.sendMessage("&cYou cannot teleport while ingame!");
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
				if (! cmd.contains("/ua") && ! plugin.isWhitelistedCommand(cmd))
				{
					ap.sendMessage("&3You cannot use non-ua commands in an arena!");
					ap.sendMessage("&3If you wish to use commands again, use &e/ua leave");
					event.setCancelled(true);
				}
			}
		}
	}
}