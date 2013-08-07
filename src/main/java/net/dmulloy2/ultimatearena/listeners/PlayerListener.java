package net.dmulloy2.ultimatearena.listeners;

import net.dmulloy2.ultimatearena.ArenaJoinTask;
import net.dmulloy2.ultimatearena.Field3D;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.SPLEEFArena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaClass;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.permissions.Permission;
import net.dmulloy2.ultimatearena.util.FormatUtil;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
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

public class PlayerListener implements Listener 
{
	private final UltimateArena plugin;
	public PlayerListener(final UltimateArena plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player pl = event.getPlayer();
		if (pl != null)
		{
			plugin.onQuit(pl);
			
			for (int i = 0; i < plugin.waiting.size(); i++)
			{
				ArenaJoinTask task = plugin.waiting.get(i);
				if (task.getPlayer().getName().equals(pl.getName()))
				{
					task.cancel();
					plugin.waiting.remove(task);
				}
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
				if (! arena.getType().getName().equalsIgnoreCase("Hunger"))
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
							Sign s = (Sign)block.getState();
							String line1 = s.getLine(0);
							ArenaPlayer ac = plugin.getArenaPlayer(player);
							if (ac != null) 
							{
								ArenaClass arc = plugin.getArenaClass(line1);
								if (arc != null)
								{
									if (arc.checkPermission(player))
									{
										ac.setClass(arc, false);
										ac.sendMessage("&7You will spawn as a(n): &6{0}", arc.getName());
									}
									else 
									{
										ac.sendMessage("&cYou do not have the necessary perms for this class");
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
					Sign s = (Sign)block.getState();
					if (s.getLine(0).equalsIgnoreCase("[UltimateArena]"))
					{
						if (s.getLine(1).equalsIgnoreCase("Click to join"))
						{
							boolean force = plugin.getPermissionHandler().hasPermission(player, Permission.FORCE_JOIN);
							if (s.getLine(2).equalsIgnoreCase("Auto assign"))
							{
								boolean found = false;
								if (plugin.activeArena.size() > 0)
								{
									for (Arena a : plugin.activeArena)
									{
										if (a.isInLobby())
										{
											plugin.fight(player, a.getName(), force);
											found = true;
										}
									}
								}
								if (!found)
								{
									if (plugin.loadedArena.size() > 0)
									{
										ArenaZone az = plugin.loadedArena.get(0);
										if (az != null)
										{
											plugin.fight(player, az.getArenaName(), force);
											found = true;
										}
									}
								}
							}
							else
							{
								String name = s.getLine(2);
								boolean found = false;
								for (Arena a : plugin.activeArena)
								{
									if (a.getName().equalsIgnoreCase(name))
									{
										if (a.isInLobby())
										{
											plugin.fight(player, a.getName(), force);
											found = true;
										}
									}
								}
								if (!found)
								{
									for (ArenaZone az : plugin.loadedArena)
									{
										if (az != null && az.getArenaName().equalsIgnoreCase(name))
										{
											plugin.fight(player, az.getArenaName(), force);
											found = true;
										}
									}
									if (!found)
									{
										player.sendMessage(plugin.getPrefix() + 
												FormatUtil.format("&cNo arena by the name of \"{0}\" exists!", name));
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
			ArenaPlayer apl = plugin.getArenaPlayer(pl);
			if (apl != null && !apl.isOut()) 
			{
				Arena are = plugin.getArena(pl);
				if (are != null)
				{
					if (apl.getDeaths() < are.getMaxDeaths()) 
					{
						if (are.isInGame() && ! are.isStopped()) 
						{
							if (are.getSpawn(apl) != null)
							{
								event.setRespawnLocation(are.getSpawn(apl));
								are.spawn(pl.getName(), false);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player p = event.getPlayer();
		for (int i = 0; i < plugin.waiting.size(); i++)
		{
			ArenaJoinTask task = plugin.waiting.get(i);
			if (task.getPlayer().getName().equals(p.getName()))
			{
				task.cancel();
				plugin.waiting.remove(task);
				
				p.sendMessage(plugin.getPrefix() + 
						FormatUtil.format("&cCancelled!"));
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
					if (ap != null && !ap.isOut())
					{
						ap.sendMessage("&cYou cannot teleport while ingame!");
						event.setCancelled(true);
					}
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
			if (! plugin.getPermissionHandler().hasPermission(player, Permission.COMMAND_BYPASS))
			{
				String cmd = event.getMessage().toLowerCase();
			
				String[] check = cmd.split(" ");
				if (!cmd.contains("/ua") && plugin.isInArena(player) && !plugin.wcmd.isAllowed(check))
				{
					player.sendMessage(plugin.getPrefix() + 
							FormatUtil.format("&7You cannot use non-ua commands in an arena!"));
					player.sendMessage(plugin.getPrefix() + 
							FormatUtil.format("&7If you wish to use commands again, use &6/ua leave"));
					event.setCancelled(true);
				}
			}
		}	
	}
}