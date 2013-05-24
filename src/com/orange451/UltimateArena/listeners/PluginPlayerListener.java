package com.orange451.UltimateArena.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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
import org.bukkit.scheduler.BukkitRunnable;

import com.orange451.UltimateArena.Field3D;
import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.BOMBArena;
import com.orange451.UltimateArena.Arenas.CONQUESTArena;
import com.orange451.UltimateArena.Arenas.CTFArena;
import com.orange451.UltimateArena.Arenas.FFAArena;
import com.orange451.UltimateArena.Arenas.HUNGERArena;
import com.orange451.UltimateArena.Arenas.INFECTArena;
import com.orange451.UltimateArena.Arenas.KOTHArena;
import com.orange451.UltimateArena.Arenas.MOBArena;
import com.orange451.UltimateArena.Arenas.PVPArena;
import com.orange451.UltimateArena.Arenas.SPLEEFArena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaClass;
import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;

public class PluginPlayerListener implements Listener {
	private UltimateArena plugin;

	public PluginPlayerListener(UltimateArena plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player pl = event.getPlayer();
		if (pl != null) {
			plugin.onQuit(pl);
			
			for (int i = plugin.waiting.size()-1; i >= 0; i--) {
				if (plugin.waiting.get(i).player.getName().equals(pl.getName())) {
					plugin.waiting.get(i).cancel();
					plugin.waiting.remove(i);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		if (plugin.isInArena(player)) {
			if (plugin.isInArena(player.getLocation())) {
				if (event.getReason().equals("You moved too quickly :( (Hacking?)")) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player pl = event.getPlayer();
		if (pl != null) {
			plugin.onJoin(pl);
		}
	}
	
	// dmulloy2 improved method.
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player pl = event.getPlayer();
		if (pl != null) {
			if (plugin.isInArena(pl))
			{
				if (plugin.getArena(pl).type.equals("Hunger"))
				{
					event.setCancelled(false);
				}
				else
				{
					event.setCancelled(true);
				}
			}
		}
	}
	
	// dmulloy2 improved method.
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player pl = event.getPlayer();
		if (pl != null) {
			if (plugin.isInArena(pl)) {
				if (plugin.getArena(pl).type.equals("Hunger")) {
					event.setCancelled(false);
				} else {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Player player = event.getPlayer();
		if (plugin.isInArena(player)) {
			if (plugin.isInArena(player.getLocation())) {
				if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
					if (event.hasBlock()) {
						Block block = event.getClickedBlock();
						if(block.getState() instanceof Sign) {
							Sign s = (Sign)block.getState();
							String line1 = s.getLine(0);
							ArenaPlayer ac = plugin.getArenaPlayer(player);
							if (ac != null) {
								ArenaClass arc = plugin.getArenaClass(line1);
								if (arc != null) {
									if (arc.checkPermission(player)) {
										ac.mclass = arc;
										player.sendMessage(ChatColor.GRAY + "You will spawn as a(n): " + ChatColor.GOLD + line1);
									} else {
										player.sendMessage(ChatColor.RED + "You do not have the necessary perms for this class");
									}
								} else {
									player.sendMessage(ChatColor.RED + "Error: " + line1 + " is not a class!");
								}
							}
						}
					}
				}
				if (action.equals(Action.LEFT_CLICK_BLOCK)) {
					if (event.hasBlock()) {
						Block block = event.getClickedBlock();
						if (plugin.isInArena(block)) {
							Arena a = plugin.getArena(player);
							if (a != null) {
								if (a instanceof SPLEEFArena) {
									SPLEEFArena spa = ((SPLEEFArena) a);
									Field3D splf = spa.spleefGround;
									if (splf.isInside(block.getLocation())) {
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
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSignInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if (player == null)
			return;
		
		if (!event.hasBlock())
			return;
		
		Block block = event.getClickedBlock();
		if (block == null)
			return;
		
		BlockState state = block.getState();
		if (state == null)
			return;
		
		if (state instanceof Sign)
		{
			Sign sign = (Sign)state;
			String line1 = sign.getLine(0);
			if (line1.isEmpty())
				return;
			
			if (line1.equalsIgnoreCase("[UltimateArena]"))
			{
				String line2 = sign.getLine(1);
				if (line2.isEmpty())
					return;
				
				joinArenaBySign(player, line1, sign);
			}
		}
	}
	
	public void joinArenaBySign(Player player, String name, Sign sign)
	{
		ArenaZone a = plugin.getArenaZone(name);
		if (plugin.getArena(name) != null)
		{
			plugin.updateSign(sign, plugin.getArena(name));
			if (plugin.getArena(name).starttimer < 1) 
			{
				player.sendMessage(ChatColor.RED + "This arena has already started!");
			}
			else
			{
				Arena tojoin = plugin.getArena(name);
				int maxplayers = tojoin.az.maxPlayers;
				int players = tojoin.amtPlayersInArena;
				if (players + 1 <= maxplayers)
				{
					plugin.getArena(name).addPlayer(player);
				}
				else
				{
					player.sendMessage(ChatColor.RED + "This arena is full, sorry!");
				}
			}
		}
		else
		{
			Arena ar = null;
			boolean disabled = false;
			for (Arena aar : plugin.activeArena)
			{
				if (aar.disabled && aar.az.equals(a))
				{
					disabled = true;
				}
			}
			for (ArenaZone aaz : plugin.loadedArena)
			{
				if (aaz.disabled && aaz.equals(a))
				{
					disabled = true;
				}
			}
			
			if (!disabled)
			{
				String arenaType = a.arenaType.toLowerCase();
				if (arenaType.equals("pvp"))
				{
					ar = new PVPArena(a);
				}
				else if (arenaType.equals("mob")) 
				{
					ar = new MOBArena(a);
				}
				else if (arenaType.equals("cq"))
				{
					ar = new CONQUESTArena(a);
				}
				else if (arenaType.equals("koth")) 
				{
					ar = new KOTHArena(a);
				}
				else if (arenaType.equals("bomb")) 
				{
					ar = new BOMBArena(a);
				}
				else if (arenaType.equals("ffa"))
				{
					ar = new FFAArena(a);
				}
				else if (arenaType.equals("hunger")) 
				{
					ar = new HUNGERArena(a);
				}
				else if (arenaType.equals("spleef")) 
				{
					ar = new SPLEEFArena(a);
				}
				else if (arenaType.equals("infect"))
				{
					ar = new INFECTArena(a);
				}
				else if (arenaType.equals("ctf"))
				{	
					ar = new CTFArena(a);
				}
				if (ar != null) 
				{
					plugin.activeArena.add(ar);
					ar.addPlayer(player);
					ar.announce();
				}
			}
			else
			{
				player.sendMessage(ChatColor.RED + "Error, This arena is disabled!");
			}
		}
	}
		
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player pl = event.getPlayer();
		if (pl != null) {
			if (plugin.isInArena(pl)) {
				ArenaPlayer apl = plugin.getArenaPlayer(pl);
				if (apl != null) {
					if (!apl.out) { 
						if (plugin.getArenaPlayer(pl).deaths < plugin.getArena(pl).maxDeaths) {
							Arena are = plugin.getArena(pl);
							if (are != null) {
								if (!are.stopped) {
									if (are.gametimer > 1) {
										if (are.getSpawn(apl) != null)
											event.setRespawnLocation(are.getSpawn(apl));
										new RemindTask(pl).runTaskLater(plugin, 20L);
									}
								}
							}
						}
					}
				}
				//plugin.getArena(pl).spawn(pl.getName());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		for (int i = 0; i < plugin.waiting.size(); i++) {
			if (plugin.waiting.get(i).player.getName().equals(p.getName())) {
				plugin.waiting.get(i).player.sendMessage(ChatColor.RED + "Cancelled!");
				plugin.waiting.get(i).cancel();
				plugin.waiting.remove(i);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String cmd = event.getMessage().toLowerCase();
		String[] check = cmd.split(" ");
		if (!cmd.contains("/ua") && plugin.isInArena(event.getPlayer()) && !plugin.wcmd.isAllowed(check)) {
			event.getPlayer().sendMessage(ChatColor.GRAY + "You cannot use non-ua commands in an arena!");
			event.getPlayer().sendMessage(ChatColor.GRAY + "If you wish to use commands again, use " + ChatColor.LIGHT_PURPLE + "/ua leave");
			event.setCancelled(true);
			return;
		}
	}	
	
	class RemindTask extends BukkitRunnable {
		Player event;
		public RemindTask(Player event) {
			this.event = event;
		}

		public void run() {
			if (event != null) {
				if (event.getName() != null) {
					Arena a = plugin.getArena(event);
					if (a != null) {
						a.spawn(event.getName(), false);
					}
				}
			}
		}
	}
}