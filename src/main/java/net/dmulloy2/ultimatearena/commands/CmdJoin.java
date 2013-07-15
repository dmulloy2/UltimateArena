package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.permissions.PermissionType;

public class CmdJoin extends UltimateArenaCommand
{
	public CmdJoin(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "join";
		this.aliases.add("j");
		this.requiredArgs.add("arena");
		this.description = "join/start an UltimateArena";
		
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform() 
	{
		boolean force = plugin.getPermissionHandler()
				.hasPermission(player, PermissionType.CMD_FORCE_JOIN.permission);
		plugin.fight(player, args[0], force);
	}
}
