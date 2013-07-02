package net.dmulloy2.ultimatearena.commands;

import java.util.List;

import net.dmulloy2.ultimatearena.UltimateArena;

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
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		String mmode = "";
		if (args.length >= 1) 
		{
			mmode = args[0];
			if (!mmode.equalsIgnoreCase("admin") && !mmode.equalsIgnoreCase("build"))
			{
				sendMessage("&cInvalid mode! Try: " + getUsageTemplate(false));
				return;
			}
		}
		sendMessage("&4==== &6{0} Help &4====", plugin.getName());
		List<UltimateArenaCommand> commands = plugin.getCommandHandler().getRegisteredCommands();
		for (int i=0; i<commands.size(); i++)
		{
			UltimateArenaCommand command;
			command = commands.get(i);
			
			if (command.getMode().equalsIgnoreCase(mmode))
			{
				sendMessage(command.getUsageTemplate(true));
			}
		}
	}
}