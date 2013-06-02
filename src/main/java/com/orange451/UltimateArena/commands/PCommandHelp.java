package com.orange451.UltimateArena.commands;

import java.util.List;

import com.orange451.UltimateArena.UltimateArena;

public class PCommandHelp extends UltimateArenaCommand
{	
	public PCommandHelp(UltimateArena plugin)
	{
		super(plugin);
		this.name = "help";
		this.aliases.add("h");
		this.aliases.add("?");
		this.optionalArgs.add("build/admin");
		this.description = "display UA help";
	}
	
	@Override
	public void perform()
	{
		String mmode = "";
		if (args.length == 1) 
		{
			mmode = args[0];
		}
		sendMessage("&4==== &6{0} &4====", plugin.getDescription().getFullName());
		List<UltimateArenaCommand> commands = plugin.getCommandHandler().getRegisteredCommands();
		for (UltimateArenaCommand command : commands)
		{
			if (command.mode == mmode)
			{
				sendMessage(command.getUsageTemplate(true));
			}
		}
	}
}