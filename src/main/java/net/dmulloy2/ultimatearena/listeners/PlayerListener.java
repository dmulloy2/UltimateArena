package net.dmulloy2.ultimatearena.listeners;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.SPLEEFArena;
import net.dmulloy2.ultimatearena.creation.ArenaCreator;
import net.dmulloy2.ultimatearena.tasks.ArenaJoinTask;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Field3D;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.ultimatearena.types.LeaveReason;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.ultimatearena.util.FormatUtil;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
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

public class PlayerListener implements Listener
{
	private final UltimateArena plugin;
	public PlayerListener(UltimateArena plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		onPlayerDisconnect(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerKick(PlayerKickEvent event)
	{
		if (! event.isCancelled())
		{
			onPlayerDisconnect(event.getPlayer());
		}
	}

	private void onPlayerDisconnect(Player player)
	{
		if (plugin.isCreatingArena(player))
		{
			ArenaCreator ac = plugin.getArenaCreator(player);

			plugin.outConsole("{0} stopping the creation of {1} from quit", player.getName(), ac.getArenaName());

			plugin.getMakingArena().remove(plugin.getArenaCreator(player));
		}

		if (plugin.isInArena(player))
		{
			ArenaPlayer ap = plugin.getArenaPlayer(player);

			plugin.outConsole("{0} leaving arena {1} from quit", ap.getName(), ap.getArena().getName());

			ap.leaveArena(LeaveReason.QUIT);
		}

		if (plugin.isPlayerWaiting(player))
		{
			ArenaJoinTask task = plugin.getWaiting().get(player.getName());

			task.cancel();
			plugin.getWaiting().remove(player.getName());
		}

		if (plugin.getSpectatingHandler().isSpectating(player))
		{
			plugin.getSpectatingHandler().removeSpectator(player);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		if (! event.isCancelled())
		{
			Player pl = event.getPlayer();
			if (plugin.isInArena(pl))
			{
				Arena arena = plugin.getArena(pl);
				if (arena.getType() != FieldType.HUNGER)
				{
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		if (! event.isCancelled())
		{
			Player pl = event.getPlayer();
			if (plugin.isInArena(pl))
			{
				Arena arena = plugin.getArena(pl);
				if (arena.getType() != FieldType.HUNGER)
				{
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Action action = event.getAction();
		Player player = event.getPlayer();
		if (plugin.isInArena(player))
		{
			if (plugin.isInArena(player.getLocation()))
			{
				if (action == Action.RIGHT_CLICK_BLOCK)
				{
					if (event.hasBlock())
					{
						Block block = event.getClickedBlock();
						if (block.getState() instanceof Sign)
						{
							Sign s = (Sign) block.getState();
							String line1 = s.getLine(0);
							ArenaPlayer ap = plugin.getArenaPlayer(player);
							if (ap != null)
							{
								ArenaClass ac = plugin.getArenaClass(line1);
								if (ac != null)
								{
									if (ac.checkPermission(player))
									{
										if (ap.setClass(ac))
										{
											String name = ac.getName();
											String article = FormatUtil.getArticle(name);

											ap.sendMessage("&3You will spawn as {0}: &e{1}", article, name);
										}
										else
										{
											ap.sendMessage("&cYou cannot use this class in this arena");
										}
									}
									else
									{
										ap.sendMessage("&cYou do not have the necessary perms for this class");
									}
								}
							}
						}
					}
				}
				else if (action == Action.LEFT_CLICK_BLOCK)
				{
					if (event.hasBlock())
					{
						Block block = event.getClickedBlock();
						if (plugin.isInArena(block.getLocation()))
						{
							Arena a = plugin.getArena(player);
							if (a != null)
							{
								if (a instanceof SPLEEFArena)
								{
									SPLEEFArena spa = (SPLEEFArena) a;
									Field3D splf = spa.getSpleefGround();
									if (splf.isInside(block.getLocation()))
									{
										block.setType(Material.AIR);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSignInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Action action = event.getAction();
		if (action == Action.RIGHT_CLICK_BLOCK)
		{
			if (event.hasBlock())
			{
				Block block = event.getClickedBlock();
				if (block.getState() instanceof Sign)
				{
					Sign s = (Sign) block.getState();
					if (s.getLine(0).equalsIgnoreCase("[UltimateArena]"))
					{
						String name = s.getLine(1);
						if (plugin.getArenaZone(name) != null)
						{
							boolean found = false;
							for (Arena a : plugin.getActiveArenas())
							{
								if (a.getName().equalsIgnoreCase(name))
								{
									if (a.isInLobby())
									{
										plugin.attemptJoin(player, a.getName());
										found = true;
									}
								}
							}
							if (! found)
							{
								for (ArenaZone az : plugin.getLoadedArenas())
								{
									if (az != null && az.getArenaName().equalsIgnoreCase(name))
									{
										plugin.attemptJoin(player, az.getArenaName());
										found = true;
									}
								}
								if (! found)
								{
									player.sendMessage(plugin.getPrefix() + 
											FormatUtil.format("&cNo arena by the name of \"{0}\" exists!", name));
								}
							}
						}
					}
					else
					{
						if (s.getLine(2).equalsIgnoreCase("Auto assign"))
						{
							boolean found = false;
							if (! plugin.getActiveArenas().isEmpty())
							{
								for (Arena a : plugin.getActiveArenas())
								{
									if (a.isInLobby())
									{
										plugin.attemptJoin(player, a.getName());
										found = true;
									}
								}
							}
							if (! found)
							{
								if (! plugin.getLoadedArenas().isEmpty())
								{
									ArenaZone az = plugin.getLoadedArenas().get(0);
									if (az != null)
									{
										plugin.attemptJoin(player, az.getArenaName());
										found = true;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		// If the player is in an arena, respawn them a second later,
		// Since plugins like Multiverse and Essentials generally take
		// priority with respawning

		final Player player = event.getPlayer();
		if (plugin.isInArena(player))
		{
			final Arena arena = plugin.getArena(player);
			final ArenaPlayer ap = plugin.getArenaPlayer(player);
			if (ap.getDeaths() < arena.getMaxDeaths())
			{
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						arena.spawn(player);
					}
				}.runTaskLater(plugin, 20L);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMoveMonitor(PlayerMoveEvent event)
	{
		if (! event.isCancelled())
		{
			// If they didnt move, don't do anything.
			if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
					event.getFrom().getBlockZ() == event.getTo().getBlockZ())
				return;

			Player player = event.getPlayer();
			if (plugin.isPlayerWaiting(player))
			{
				ArenaJoinTask task = plugin.getWaiting().get(player.getName());

				task.cancel();
				plugin.getWaiting().remove(player.getName());

				player.sendMessage(plugin.getPrefix() + 
						FormatUtil.format("&cCancelled!"));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerMoveLowest(PlayerMoveEvent event)
	{
		if (! event.isCancelled())
		{
			Player player = event.getPlayer();
			if (! plugin.isInArena(player))
				return;

			if (! plugin.isInArena(event.getFrom()))
			{
				if (! plugin.isInArena(event.getTo()))
				{
					plugin.getArena(player).spawn(player);
				}
				else
				{
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		if (! event.isCancelled())
		{
			if (event.getEntity() instanceof Player)
			{
				Player player = (Player) event.getEntity();
				if (plugin.isInArena(player))
				{
					Arena a = plugin.getArena(player);
					if (a.isInLobby())
					{
						// Prevent food level change
						player.setFoodLevel(20);
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		if (! event.isCancelled())
		{
			Player player = event.getPlayer();
			if (plugin.isInArena(player))
			{
				if (event.getCause() == TeleportCause.COMMAND)
				{
					ArenaPlayer ap = plugin.getArenaPlayer(player);
					ap.sendMessage("&cYou cannot teleport while ingame!");

					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if (! event.isCancelled())
		{
			Player player = event.getPlayer();
			if (plugin.isInArena(player))
			{
				if (! plugin.getPermissionHandler().hasPermission(player, Permission.BYPASS))
				{
					String cmd = event.getMessage().toLowerCase();
					if (! cmd.contains("/ua") && ! plugin.isWhitelistedCommand(cmd))
					{
						player.sendMessage(plugin.getPrefix() +
								FormatUtil.format("&3You cannot use non-ua commands in an arena!"));
						player.sendMessage(plugin.getPrefix() + 
								FormatUtil.format("&3If you wish to use commands again, use &e/ua leave"));
						event.setCancelled(true);
					}
				}
			}
		}
	}
}