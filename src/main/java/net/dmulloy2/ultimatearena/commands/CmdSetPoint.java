package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.permissions.Permission;

public class CmdSetPoint extends UltimateArenaCommand
{	
	public CmdSetPoint(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "setpoint";
		this.aliases.add("sp");
		this.description = "set a point of your field";
		this.permission = Permission.CMD_SET_POINT;
		
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform() 
	{
		plugin.setPoint(player);
	}
}
