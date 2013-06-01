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

/**
 * @author dmulloy2
 */

public class PluginBlockListener implements Listener 
{
	public UltimateArena plugin;
	public PluginBlockListener(UltimateArena plugin) 
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
					/**If the arena is hunger, allow blocks to be placed**/
					if (arena.type.equals("Hunger"))
					{
						event.setCancelled(false);
					}
					/**If any other arena, disallow**/
					else
					{
						player.sendMessage(ChatColor.RED + "You can't break this!");
						event.setCancelled(true);
					}
				}
				/**If the player is not in an arena, but the block is**/
				else
				{
					/**If the player has correct perms, allow them to build**/
					if (player.hasPermission("ultimatearena.admin"))
					{
						event.setCancelled(false);
					}
					/**If not, disallow**/
					else
					{
						event.setCancelled(true);
						player.sendMessage(ChatColor.RED + "You cannot break this!");
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
					/**If the arena is hunger, allow blocks to be placed**/
					if (arena.type.equals("Hunger"))
					{
						event.setCancelled(false);
					}
					/**If any other arena, disallow**/
					else
					{
						event.setCancelled(true);
					}
				}
				/**If the player is not in an arena, but the block is**/
				else
				{
					/**If the player has correct perms, allow them to build**/
					if (player.hasPermission("ultimatearena.admin"))
					{
						event.setCancelled(false);
					}
					/**If not, disallow**/
					else
					{
						event.setCancelled(true);
					}
				}
			}
		}
	}
}