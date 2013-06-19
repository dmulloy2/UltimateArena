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
		/**Checks to make sure the block is not null**/
		Block block = event.getBlock();
		if (block == null)
			return;
		
		/**Checks to make sure the player is not null**/
		Player player = event.getPlayer();
		if (player == null)
			return;
		
		/**Check to make sure the event is not cancelled**/
		if (event.isCancelled())
			return;
		
		/**Checks to make sure the block is in an arena**/
		if (plugin.isInArena(block))
		{
			/**Checks to make sure the arena is not null**/
			Arena arena = plugin.getArenaInside(block);
			if (arena != null)
			{
				/**Check to make sure the player is in an arena**/
				if (plugin.isInArena(player))
				{
					/**If the arena isn't hunger, don't allow blocks to be placed**/
					if (!arena.type.equalsIgnoreCase("Hunger"))
					{
						player.sendMessage(ChatColor.RED + "You cannot break this!");
						event.setCancelled(true);
					}
				}
				/**If the player is not in an arena, but the block is**/
				else
				{
					/**If the player does not have perms, don't allow them to build**/
					if (!plugin.getPermissionHandler().hasPermission(player, PermissionType.ARENA_BUILD.permission))
					{
						player.sendMessage(ChatColor.RED + "You cannot break this!");
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) 
	{
		/**Checks to make sure the block is not null**/
		Block block = event.getBlock();
		if (block == null)
			return;
		
		/**Checks to make sure the player is not null**/
		Player player = event.getPlayer();
		if (player == null)
			return;
		
		/**Check to make sure the event is not cancelled**/
		if (event.isCancelled())
			return;
		
		/**Checks to make sure the block is in an arena**/
		if (plugin.isInArena(block))
		{
			/**Checks to make sure the arena is not null**/
			Arena arena = plugin.getArenaInside(block);
			if (arena != null)
			{
				/**Check to make sure the player is in an arena**/
				if (plugin.isInArena(player))
				{
					/**If the arena isn't hunger, don't allow blocks to be placed**/
					if (!arena.type.equalsIgnoreCase("Hunger"))
					{
						player.sendMessage(ChatColor.RED + "You cannot place this!");
						event.setCancelled(true);
					}
				}
				/**If the player is not in an arena, but the block is**/
				else
				{
					/**If the player does not have perms, don't allow them to build**/
					if (!plugin.getPermissionHandler().hasPermission(player, PermissionType.ARENA_BUILD.permission))
					{
						player.sendMessage(ChatColor.RED + "You cannot place this!");
						event.setCancelled(true);
					}
				}
			}
		}
	}
}