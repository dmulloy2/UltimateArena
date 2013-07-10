package net.dmulloy2.ultimatearena.listeners;

import net.dmulloy2.ultimatearena.ArenaJoinTask;
import net.dmulloy2.ultimatearena.Field3D;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.SPLEEFArena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaClass;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.arenas.objects.FieldType;

import org.bukkit.ChatColor;
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
				if (! FieldType.getName(arena.getType()).equalsIgnoreCase("Hunger"))
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
				if (! FieldType.getName(arena.getType()).equalsIgnoreCase("Hunger"))
				{
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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
									Field3D splf = spa.spleefGround;
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
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Action action = event.getAction();
		if (action.equals(Action.RIGHT_CLICK_BLOCK))
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
							if (s.getLine(2).equalsIgnoreCase("Auto assign"))
							{
								boolean found = false;
								if (plugin.activeArena.size() > 0)
								{
									for (Arena a : plugin.activeArena)
									{
										if (a.getStarttimer() > 1)
										{
											plugin.fight(player, a.getName());
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
											plugin.fight(player, az.getArenaName());
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
									if (a.getName().equalsIgnoreCase(name) && a.getStarttimer() > 1)
									{
										plugin.fight(player, a.getName());
										found = true;
									}
								}
								if (!found)
								{
									for (ArenaZone az : plugin.loadedArena)
									{
										if (az != null && az.getArenaName().equalsIgnoreCase(name))
										{
											plugin.fight(player, az.getArenaName());
											found = true;
										}
									}
									if (!found)
									{
										player.sendMessage(ChatColor.RED + "No arena by the name of \"" + name + "\" exists!");
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
				if (plugin.getArenaPlayer(pl).getDeaths() < plugin.getArena(pl).getMaxDeaths()) 
				{
					Arena are = plugin.getArena(pl);
					if (are != null && !are.isStopped())
					{
						if (are.getGametimer() > 1) 
						{
							if (are.getSpawn(apl) != null)
							{
								// TODO: Make sure this actually works
								event.setRespawnLocation(are.getSpawn(apl));
							}
								
							are.spawn(pl.getName(), false);
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
				
				p.sendMessage(ChatColor.RED + "Cancelled!");
			}
		}
	}
	
	// TODO: Make this actually work
	/*@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		if (! event.isCancelled())
		{
			Player player = event.getPlayer();
			if (plugin.isInArena(player))
			{
				if (event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN)
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
	}*/
	
	// TODO: Add a bypass permission?
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if (! event.isCancelled())
		{
			Player player = event.getPlayer();
			String cmd = event.getMessage().toLowerCase();
			
			String[] check = cmd.split(" ");
			if (!cmd.contains("/ua") && plugin.isInArena(player) && !plugin.wcmd.isAllowed(check))
			{
				player.sendMessage(ChatColor.GRAY + "You cannot use non-ua commands in an arena!");
				player.sendMessage(ChatColor.GRAY + "If you wish to use commands again, use " + ChatColor.LIGHT_PURPLE + "/ua leave");
				event.setCancelled(true);
				return;
			}
		}
	}	
}