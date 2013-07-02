package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.permissions.PermissionType;

public class PCommandSetPoint extends UltimateArenaCommand
{	
	public PCommandSetPoint(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "setpoint";
		this.aliases.add("sp");
		this.mode = "build";
		this.description = "set a point of your field";
		this.permission = PermissionType.CMD_SET_POINT.permission;
	}
	
	@Override
	public void perform() 
	{
		plugin.setPoint(player);
	}
}
