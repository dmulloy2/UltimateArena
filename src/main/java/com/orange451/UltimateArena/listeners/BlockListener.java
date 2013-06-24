package com.orange451.UltimateArena.listeners;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaSign;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;
import com.orange451.UltimateArena.permissions.PermissionType;
import com.orange451.UltimateArena.util.FormatUtil;

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
				if (!plugin.getPermissionHandler().hasPermission(player, PermissionType.BUILD.permission))
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
				if (!plugin.getPermissionHandler().hasPermission(player, PermissionType.BUILD.permission))
				{
					player.sendMessage(ChatColor.RED + "You cannot place this!");
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSignChange(SignChangeEvent event)
	{
		if (event.getLine(0).equalsIgnoreCase("[UltimateArena]"))
		{
			if (plugin.getPermissionHandler().hasPermission(event.getPlayer(), PermissionType.BUILD.permission))
			{
				if (event.getLine(1).equalsIgnoreCase("Click to join"))
				{
					Sign s = (Sign)event.getBlock().getState();
					if (event.getLine(2).equalsIgnoreCase("Auto assign"))
					{
						ArenaSign sign = new ArenaSign(plugin, s, s.getLocation());
						plugin.joinSigns.add(sign);
					}
					else
					{
						ArenaZone az = plugin.getArenaZone(event.getLine(2));
						if (az != null)
						{
							ArenaSign sign = new ArenaSign(plugin, s, s.getLocation(), az);
							plugin.joinSigns.add(sign);
							sign.update();
						}
						else
						{
							event.setLine(0, FormatUtil.format("[UltimateArena]"));
							event.setLine(1, FormatUtil.format("&4Invalid Arena"));
							event.setLine(2, "");
							event.setLine(3, "");
						}
					}
				}
			}
			else
			{
				event.setLine(0, FormatUtil.format("[UltimateArena]"));
				event.setLine(1, FormatUtil.format("&4No permission"));
				event.setLine(2, "");
				event.setLine(3, "");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignBreak(BlockBreakEvent event)
	{
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if (block.getState() instanceof Sign)
		{
			Sign s = (Sign)block.getState();
			if (s.getLine(0).equalsIgnoreCase("[UltimateArena]"))
			{
				ArenaSign sign = plugin.getArenaSign(block.getLocation());
				if (sign != null)
				{
					if (plugin.getPermissionHandler().hasPermission(player, PermissionType.BUILD.permission))
					{
						plugin.deleteSign(sign);
						player.sendMessage(FormatUtil.format("&eDeleted join sign!"));
					}
					else
					{
						event.setCancelled(true);
						player.sendMessage(FormatUtil.format("&cPermission denied!"));
					}
				}
			}
		}
	}
}