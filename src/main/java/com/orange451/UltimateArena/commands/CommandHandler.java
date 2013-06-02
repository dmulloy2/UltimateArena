package com.orange451.UltimateArena.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import com.orange451.UltimateArena.UltimateArena;

/**
 * @author dmulloy2
 */

public class CommandHandler implements CommandExecutor 
{
	private final UltimateArena plugin;
	private String commandPrefix;
	private List<UltimateArenaCommand> registeredCommands;
	
	public CommandHandler(UltimateArena plugin)
	{
		this.plugin = plugin;
		registeredCommands = new ArrayList<UltimateArenaCommand>();
	}
	
	public void registerCommand(UltimateArenaCommand command) 
	{
		PluginCommand pluginCommand = plugin.getCommand(command.getName());
		if (pluginCommand != null)
		{
			pluginCommand.setExecutor(command);
			registeredCommands.add(command);
		} 
		else
		{
			plugin.getLogger().warning("Entry for command \"" + command.getName() + "\" is missing in plugin.yml");
		}
	}

	public List<UltimateArenaCommand> getRegisteredCommands() 
	{
		return registeredCommands;
	}

	public String getCommandPrefix() 
	{
		return commandPrefix;
	}

	public void setCommandPrefix(String commandPrefix) 
	{
		this.commandPrefix = commandPrefix;
		plugin.getCommand(commandPrefix).setExecutor(this);
	}

	public boolean usesCommandPrefix() 
	{
		return commandPrefix != null;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		List<String> argsList = new ArrayList<String>();
		
		if (args.length > 0) 
		{
			String commandName = args[0];
			for (int i = 1; i < args.length; i++)
				argsList.add(args[i]);
			
			for (UltimateArenaCommand command : registeredCommands) 
			{
				if (commandName.equalsIgnoreCase(command.getName()) || command.getAliases().contains(commandName.toLowerCase()))
				{
					command.execute(sender, argsList.toArray(new String[0]));
					return true;
				}
			}
			sender.sendMessage(ChatColor.YELLOW + "Unknown UltimateArena command \"" + args[0] + "\". Try /ua help!");
		} 
		else 
		{
			new PCommandHelp(plugin).execute(sender, args);
		}
		
		return true;
	}
}