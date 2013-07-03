package net.dmulloy2.ultimatearena.commands;

import org.bukkit.ChatColor;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.permissions.PermissionType;

public class CmdForceJoin extends UltimateArenaCommand
{
	public CmdForceJoin(UltimateArena plugin)
	{
		super(plugin);
		this.name = "forcejoin";
		this.aliases.add("fj");
		this.requiredArgs.add("arena");
		this.mode = "admin";
		this.description = "force join an arena";
		this.permission = PermissionType.CMD_FORCE_JOIN.permission;
	}
	
	@Override
	public void perform() 
	{
		String name = args[0];
		player.sendMessage(ChatColor.GOLD + "Attempthing to join arena: " + name);
		plugin.joinBattle(true, player, name);
	}
}
