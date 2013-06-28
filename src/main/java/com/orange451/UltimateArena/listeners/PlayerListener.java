package com.orange451.UltimateArena.listeners;

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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.orange451.UltimateArena.Field3D;
import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.SPLEEFArena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaClass;
import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;

public class PlayerListener implements Listener 
{
	private UltimateArena plugin;
	public PlayerListener(UltimateArena plugin)
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
				if (plugin.waiting.get(i).player.getName().equals(pl.getName()))
				{
					plugin.waiting.get(i).cancel();
					plugin.waiting.remove(i);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent event) 
	{
		Player player = event.getPlayer();
		if (plugin.isInArena(player)) 
		{
			if (plugin.isInArena(player.getLocation())) 
			{
				if (event.getReason().equals("You moved too quickly :( (Hacking?)")) 
				{
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) 
	{
		Player pl = event.getPlayer();
		if (pl != null)
		{
			plugin.onJoin(pl);
		}
	}
	
	// dmulloy2 improved method.
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) 
	{
		Player pl = event.getPlayer();
		if (pl != null)
		{
			if (plugin.isInArena(pl))
			{
				if (! plugin.getArena(pl).type.equals("Hunger"))
				{
					event.setCancelled(true);
				}
			}
		}
	}
	
	// dmulloy2 improved method.
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerDropItemEvent event) 
	{
		Player pl = event.getPlayer();
		if (pl != null)
		{
			if (plugin.isInArena(pl)) 
			{
				if (! plugin.getArena(pl).type.equals("Hunger"))
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
										ac.mclass = arc;
										player.sendMessage(ChatColor.GRAY + "You will spawn as a(n): " + ChatColor.GOLD + line1);
									}
									else 
									{
										player.sendMessage(ChatColor.RED + "You do not have the necessary perms for this class");
									}
								} 
								else 
								{
									player.sendMessage(ChatColor.RED + "Error: \"" + line1 + "\" is not a class!");
								}
							}
						}
					}
				}
				if (action.equals(Action.LEFT_CLICK_BLOCK)) 
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
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract1(PlayerInteractEvent event)
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
										if (a.starttimer > 1)
										{
											plugin.fight(player, a.name);
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
											plugin.fight(player, az.arenaName);
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
									if (a.name.equalsIgnoreCase(name) && a.starttimer > 1)
									{
										plugin.fight(player, a.name);
										found = true;
									}
								}
								if (!found)
								{
									for (ArenaZone az : plugin.loadedArena)
									{
										if (az != null && az.arenaName.equalsIgnoreCase(name))
										{
											plugin.fight(player, az.arenaName);
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
		if (pl != null)
		{
			if (plugin.isInArena(pl)) 
			{
				ArenaPlayer apl = plugin.getArenaPlayer(pl);
				if (apl != null && !apl.out) 
				{
					if (plugin.getArenaPlayer(pl).deaths < plugin.getArena(pl).maxDeaths) 
					{
						Arena are = plugin.getArena(pl);
						if (are != null && !are.stopped)
						{
							if (are.gametimer > 1) 
							{
								if (are.getSpawn(apl) != null)
								{
									event.setRespawnLocation(are.getSpawn(apl));
								}
								
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
			if (plugin.waiting.get(i).player.getName().equals(p.getName()))
			{
				p.sendMessage(ChatColor.RED + "Cancelled!");
				
				plugin.waiting.get(i).cancel();
				plugin.waiting.remove(i);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
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