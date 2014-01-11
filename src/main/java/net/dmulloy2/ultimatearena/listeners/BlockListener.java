package net.dmulloy2.ultimatearena.listeners;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaSign;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.ultimatearena.util.FormatUtil;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

/**
 * @author dmulloy2
 */

public class BlockListener implements Listener
{
	private final UltimateArena plugin;
	public BlockListener(UltimateArena plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		Block block = event.getBlock();
		if (plugin.isInArena(block.getLocation()))
		{
			/** The player is in an arena **/
			if (plugin.isInArena(player))
			{
				Arena arena = plugin.getArena(player);
				if (arena.getType() != FieldType.HUNGER)
				{
					player.sendMessage(plugin.getPrefix() + FormatUtil.format("&cYou cannot break this!"));
					event.setCancelled(true);
				}
			}
			else
			{
				/** The player is at the site of the arena, but not in it **/
				if (! plugin.getPermissionHandler().hasPermission(player, Permission.BUILD))
				{
					player.sendMessage(plugin.getPrefix() + FormatUtil.format("&cYou cannot break this!"));
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		Block block = event.getBlock();
		if (plugin.isInArena(block.getLocation()))
		{
			/** The player is in an arena **/
			if (plugin.isInArena(player))
			{
				Arena arena = plugin.getArena(player);
				if (arena.getType() != FieldType.HUNGER)
				{
					player.sendMessage(plugin.getPrefix() + FormatUtil.format("&cYou cannot place this!"));
					event.setCancelled(true);
				}
			}
			else
			{
				/** The player is at the site of the arena, but not in it **/
				if (! plugin.getPermissionHandler().hasPermission(player, Permission.BUILD))
				{
					player.sendMessage(plugin.getPrefix() + FormatUtil.format("&cYou cannot place this!"));
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
			if (plugin.getPermissionHandler().hasPermission(event.getPlayer(), Permission.BUILD))
			{
				if (event.getLine(1).equalsIgnoreCase("Click to Join"))
				{
					if (event.getLine(2).equalsIgnoreCase("Auto Assign"))
						return;

					ArenaZone az = plugin.getArenaZone(event.getLine(2));
					if (az != null)
					{
						int id = 0;
						
						// Make sure we get the highest id
						for (ArenaSign sign : plugin.getSignHandler().getSigns())
						{
							if (sign.getId() > id)
								id = sign.getId();
						}
						
						ArenaSign sign = new ArenaSign(plugin, event.getBlock().getLocation(), az, id);
						plugin.getSignHandler().addSign(sign);

						event.getPlayer().sendMessage(plugin.getPrefix() + 
								FormatUtil.format("&aCreated new Join Sign!"));
					}
					else
					{
						event.setLine(0, FormatUtil.format("[UltimateArena]"));
						event.setLine(1, FormatUtil.format("&4Invalid Arena"));
						event.setLine(2, "");
						event.setLine(3, "");
					}
				}
				else
				{
					ArenaZone az = plugin.getArenaZone(event.getLine(1));
					if (az != null)
					{
						int id = 0;
						
						// Make sure we get the highest id
						for (ArenaSign sign : plugin.getSignHandler().getSigns())
						{
							if (sign.getId() > id)
								id = sign.getId();
						}
						
						ArenaSign sign = new ArenaSign(plugin, event.getBlock().getLocation(), az, id);
						plugin.getSignHandler().addSign(sign);

						event.getPlayer().sendMessage(plugin.getPrefix() + 
								FormatUtil.format("&aCreated new Join Sign!"));
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
			Sign s = (Sign) block.getState();
			if (s.getLine(0).equalsIgnoreCase("[UltimateArena]"))
			{
				ArenaSign sign = plugin.getSignHandler().getSign(block.getLocation());
				if (sign != null)
				{
					if (plugin.getPermissionHandler().hasPermission(player, Permission.BUILD))
					{
						plugin.getSignHandler().deleteSign(sign);
						player.sendMessage(plugin.getPrefix() +
								FormatUtil.format("&cDeleted Join sign!"));
					}
					else
					{
						event.setCancelled(true);
						player.sendMessage(plugin.getPrefix() + 
								FormatUtil.format("&cPermission denied!"));
					}
				}
			}
		}
	}
}