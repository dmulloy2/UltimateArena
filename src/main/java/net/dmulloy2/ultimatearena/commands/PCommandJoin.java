package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;

public class PCommandJoin extends UltimateArenaCommand
{
	public PCommandJoin(UltimateArena plugin) 
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
		String name = args[0];
		plugin.fight(player, name);
	}
}
