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
import com.orange451.UltimateArena.permissions.PermissionType;

/**
 * @author dmulloy2
 */

public class BlockListener implements Listener 
{
	public UltimateArena plugin;
	public BlockListener(UltimateArena plugin) 
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) 
	{
		Block block = event.getBlock();
		if (block == null)
			return;

		Player player = event.getPlayer();
		if (player == null)
			return;
		
		if (event.isCancelled())
			return;
		
		if (plugin.isInArena(block))
		{
			/** The player is in an arena **/
			if (plugin.isInArena(player) && plugin.getArena(player) != null)
			{
				Arena arena = plugin.getArena(player);
				if (!arena.type.equalsIgnoreCase("Hunger"))
				{
					player.sendMessage(ChatColor.RED + "You cannot break this!");
					event.setCancelled(true);
				}
				// TODO: Hunger games block logging?
			}
			else
			{
				/** The player is at the site of the arena, but not in it **/
				if (!plugin.getPermissionHandler().hasPermission(player, PermissionType.ARENA_BUILD.permission))
				{
					player.sendMessage(ChatColor.RED + "You cannot break this!");
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) 
	{
		Block block = event.getBlock();
		if (block == null)
			return;

		Player player = event.getPlayer();
		if (player == null)
			return;
		
		if (event.isCancelled())
			return;
		
		if (plugin.isInArena(block))
		{
			/** The player is in an arena **/
			if (plugin.isInArena(player) && plugin.getArena(player) != null)
			{
				Arena arena = plugin.getArena(player);
				if (!arena.type.equalsIgnoreCase("Hunger"))
				{
					player.sendMessage(ChatColor.RED + "You cannot place this!");
					event.setCancelled(true);
				}
				// TODO: Hunger games block logging?
			}
			else
			{
				/** The player is at the site of the arena, but not in it **/
				if (!plugin.getPermissionHandler().hasPermission(player, PermissionType.ARENA_BUILD.permission))
				{
					player.sendMessage(ChatColor.RED + "You cannot place this!");
					event.setCancelled(true);
				}
			}
		}
	}
}