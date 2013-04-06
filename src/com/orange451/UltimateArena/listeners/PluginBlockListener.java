package com.orange451.UltimateArena.listeners;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.PermissionInterface.PermissionInterface;

public class PluginBlockListener implements Listener {
	UltimateArena plugin;
	public PluginBlockListener(UltimateArena plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.isCancelled()) {
			Player player = event.getPlayer();
			if (player != null) {
				Block block = event.getBlock();
				if (plugin.isInArena(block)) {
					Arena arena = plugin.getArenaInside(block);
					if (arena.type == "Hunger" && arena.az.arena.isInside(block.getLocation())) {
						event.setCancelled(false);
					} else {
						event.setCancelled(true);
					}
					
					if (PermissionInterface.checkPermission(player, plugin.uaAdmin)) {
						if (!(plugin.isInArena(player))) {
							event.setCancelled(false);
						}else{
							player.sendMessage(ChatColor.RED + "You cannot break this!");
						}
					}else{
						player.sendMessage(ChatColor.RED + "You cannot break this!");
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!event.isCancelled()) {
			Player player = event.getPlayer();
			if (player != null) {
				Block block = event.getBlock();
				if (plugin.isInArena(block)) {
					event.setCancelled(true);
					if (PermissionInterface.checkPermission(player, plugin.uaAdmin)) {
						if (!(plugin.isInArena(player))) {
							event.setCancelled(false);
						}else{
							player.sendMessage(ChatColor.RED + "You cannot place this here!");
						}
					}else{
						player.sendMessage(ChatColor.RED + "You cannot place this here!");
					}
				}
			}
		}
	}
}
