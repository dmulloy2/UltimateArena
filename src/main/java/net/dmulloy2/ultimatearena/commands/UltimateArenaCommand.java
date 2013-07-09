package net.dmulloy2.ultimatearena.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.permissions.Permission;
import net.dmulloy2.ultimatearena.util.FormatUtil;

public abstract class UltimateArenaCommand implements CommandExecutor
{
	protected final UltimateArena plugin; 
	
	protected CommandSender sender;
	protected Player player;
	protected String args[];
	
	protected String name;
	protected String description;
	protected String mode = "";
	
	protected Permission permission;
	
	protected boolean mustBePlayer = true;
	protected List<String> requiredArgs;
	protected List<String> optionalArgs;
	protected List<String> aliases;
	
	public UltimateArenaCommand(UltimateArena plugin)
	{
		this.plugin = plugin;
		requiredArgs = new ArrayList<String>(2);
		optionalArgs = new ArrayList<String>(2);
		aliases = new ArrayList<String>(2);
	}
	
	public final boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		execute(sender, args);
		return true;
	}

	public final void execute(CommandSender sender, String[] args)
	{
		this.sender = sender;
		this.args = args;
		if (sender instanceof Player)
			player = (Player) sender;
		
		if (mustBePlayer && !isPlayer())
		{
			err("You must be a player to execute this command!");
			return;
		}
		
		if (requiredArgs.size() > args.length) 
		{
			invalidArgs();
			return;
		}
		
		if (hasPermission())
			perform();
		else
		{
			err("You do not have permission to perform this command!");
			log(Level.WARNING, sender.getName() + " was denied access to a command!");
		}
	}
	
	protected final boolean isPlayer() 
	{
		return (player != null);
	}
	
	private final boolean hasPermission()
	{
		return (plugin.getPermissionHandler().hasPermission(sender, permission));
	}

	public String getDescription()
	{
		return FormatUtil.format(description);
	}
	
	public abstract void perform();
	
	public List<String> getAliases() 
	{
		return aliases;
	}
	
	public final String getMode()
	{
		return mode;
	}
	
	public final String getName() 
	{
		return name;
	}

	public final String getUsageTemplate(final boolean displayHelp)
	{
		StringBuilder ret = new StringBuilder();
		ret.append("&c/ua ");

		ret.append(name);

		for (String s : optionalArgs)
		{
			ret.append(String.format(" &6[" + s + "]"));
		}
		
		for (String s : requiredArgs)
			ret.append(String.format(" &4<" + s + ">"));
		
		if (displayHelp)
			ret.append(" &e" + description);
		
		return FormatUtil.format(ret.toString());
	}
	
	protected final void sendpMessage(String message, Object... objects)
	{
		sender.sendMessage(plugin.getPrefix() + FormatUtil.format(message, objects));
	}
	
	protected final void sendMessage(String message, Object... objects)
	{
		sender.sendMessage(FormatUtil.format(message, objects));
	}
	
	protected final void log(Level level, String string, Object... objects)
	{
		plugin.outConsole(level, string, objects);
	}
	
	protected final void log(String string, Object... objects)
	{
		log(Level.INFO, string, objects);
	}
	
	protected final void debug(String string, Object... objects)
	{
		plugin.debug(string, objects);
	}
	
	protected final void err(String string, Object... objects)
	{
		sendpMessage("&c" + string, objects);
	}
	
	protected final void invalidArgs()
	{
		err("Invalid arguments! Try: " + getUsageTemplate(false));
	}
}