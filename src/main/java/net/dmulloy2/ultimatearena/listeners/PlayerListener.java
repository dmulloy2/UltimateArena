package net.dmulloy2.ultimatearena.listeners;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.SPLEEFArena;
import net.dmulloy2.ultimatearena.tasks.ArenaJoinTask;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Field3D;
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

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		onPlayerDisconnect(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event)
	{
		if (! event.isCancelled())
		{
			onPlayerDisconnect(event.getPlayer());
		}
	}

	public void onPlayerDisconnect(Player player)
	{
		if (plugin.isCreatingArena(player))
		{
			plugin.debug("Player {0} left the game, stopping the creation of an arena", player.getName());
			plugin.getMakingArena().remove(plugin.getArenaCreator(player));
		}

		if (plugin.isInArena(player))
		{
			plugin.debug("Player {0} leaving arena from quit", player.getName());

			plugin.getArenaPlayer(player).leaveArena(LeaveReason.QUIT);
		}

		for (int i = 0; i < plugin.getWaiting().size(); i++)
		{
			ArenaJoinTask task = plugin.getWaiting().get(i);
			if (task.getPlayer().getName().equals(player.getName()))
			{
				task.cancel();
				plugin.getWaiting().remove(task);
			}
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
				if (! arena.getType().getName().equalsIgnoreCase("Hunger"))
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
				if (!arena.getType().getName().equalsIgnoreCase("Hunger"))
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
				if (action.equals(Action.RIGHT_CLICK_BLOCK))
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
										ap.setClass(ac);

										String name = ac.getName();
										String article = FormatUtil.getArticle(name);

										ap.sendMessage("&3You will spawn as {0}: &e{1}", article, name);
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
				else if (action.equals(Action.LEFT_CLICK_BLOCK))
				{
					if (event.hasBlock())
					{
						Block block = event.getClickedBlock();
						if (plugin.isInArena(block))
						{
							Arena a = plugin.getArena(player);
							if (a != null)
							{
								if (a instanceof SPLEEFArena)
								{
									SPLEEFArena spa = ((SPLEEFArena) a);
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
						if (s.getLine(1).equalsIgnoreCase("Click to join"))
						{
							if (s.getLine(2).equalsIgnoreCase("Auto assign"))
							{
								boolean found = false;
								if (plugin.getActiveArenas().size() > 0)
								{
									for (Arena a : plugin.getActiveArenas())
									{
										if (a.isInLobby())
										{
											plugin.join(player, a.getName());
											found = true;
										}
									}
								}
								if (! found)
								{
									if (plugin.getLoadedArenas().size() > 0)
									{
										ArenaZone az = plugin.getLoadedArenas().get(0);
										if (az != null)
										{
											plugin.join(player, az.getArenaName());
											found = true;
										}
									}
								}
							}
							else
							{
								String name = s.getLine(2);
								boolean found = false;
								for (Arena a : plugin.getActiveArenas())
								{
									if (a.getName().equalsIgnoreCase(name))
									{
										if (a.isInLobby())
										{
											plugin.join(player, a.getName());
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
											plugin.join(player, az.getArenaName());
											found = true;
										}
									}
									if (! found)
									{
										player.sendMessage(plugin.getPrefix()
												+ FormatUtil.format("&cNo arena by the name of \"{0}\" exists!", name));
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
		Player pl = event.getPlayer();
		if (plugin.isInArena(pl))
		{
			Arena a = plugin.getArena(pl);
			ArenaPlayer apl = plugin.getArenaPlayer(pl);
			if (apl.getDeaths() < a.getMaxDeaths())
			{
				if (a.isInGame() && ! a.isStopped())
				{
					if (a.getSpawn(apl) != null)
					{
						event.setRespawnLocation(a.getSpawn(apl));
						a.spawn(pl);
					}
				}
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
					event.getFrom().getBlockZ() == event.getTo().getBlockY())
				return;

			Player player = event.getPlayer();
			
			for (int i = 0; i < plugin.getWaiting().size(); i++)
			{
				ArenaJoinTask task = plugin.getWaiting().get(i);
				if (task.getPlayer().getName().equals(player.getName()))
				{
					task.cancel();
					plugin.getWaiting().remove(task);

					player.sendMessage(plugin.getPrefix() + FormatUtil.format("&cCancelled!"));
				}
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
			if (! plugin.getPermissionHandler().hasPermission(player, Permission.BYPASS))
			{
				String cmd = event.getMessage().toLowerCase();
				if (! cmd.contains("/ua") && plugin.isInArena(player) && ! plugin.isWhitelistedCommand(cmd))
				{
					player.sendMessage(plugin.getPrefix() + FormatUtil.format("&3You cannot use non-ua commands in an arena!"));
					player.sendMessage(plugin.getPrefix() + FormatUtil.format("&3If you wish to use commands again, use &e/ua leave"));
					event.setCancelled(true);
				}
			}
		}
	}
}