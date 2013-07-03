package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;

public class CmdLeave extends UltimateArenaCommand
{	
	public CmdLeave(UltimateArena plugin)
	{
		super(plugin);
		this.name = "leave";
		this.aliases.add("l");
		this.description = "leave an arena";
	}
	
	@Override
	public void perform() 
	{
		plugin.leaveArena(player);
	}
}