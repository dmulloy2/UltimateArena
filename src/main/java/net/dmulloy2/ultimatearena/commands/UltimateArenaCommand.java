package net.dmulloy2.ultimatearena.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.Util;

import org.apache.commons.lang.WordUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public abstract class UltimateArenaCommand implements CommandExecutor
{
	protected final UltimateArena plugin;

	protected CommandSender sender;
	protected Player player;
	protected String args[];

	protected String name;
	protected String description;

	protected Permission permission;

	protected boolean mustBePlayer;
	protected boolean mustBeInArena;

	protected List<String> requiredArgs;
	protected List<String> optionalArgs;
	protected List<String> aliases;

	public UltimateArenaCommand(UltimateArena plugin)
	{
		this.plugin = plugin;
		this.requiredArgs = new ArrayList<String>(2);
		this.optionalArgs = new ArrayList<String>(2);
		this.aliases = new ArrayList<String>(2);
	}

	@Override
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

		if (mustBePlayer && ! isPlayer())
		{
			err("You must be a player to execute this command!");
			return;
		}

		if (mustBeInArena && ! isInArena())
		{
			err("You must be in an arena to execute this command!");
			return;
		}

		if (requiredArgs.size() > args.length)
		{
			invalidArgs();
			return;
		}

		if (! hasPermission())
		{
			err("You do not have permission to perform this command!");
			log(Level.WARNING, sender.getName() + " was denied access to a command!");
			return;
		}

		try
		{
			perform();
		}
		catch (Throwable e)
		{
			err("Error executing command: &c{0}&4: &c{1}", e.getClass().getName(), e.getLocalizedMessage());
			plugin.getLogHandler().debug(Util.getUsefulStack(e, "executing command " + name));
		}
	}

	public abstract void perform();

	protected final boolean isPlayer()
	{
		return player != null;
	}

	private final boolean hasPermission()
	{
		return plugin.getPermissionHandler().hasPermission(sender, permission);
	}

	private final boolean isInArena()
	{
		return player != null && plugin.isInArena(player);
	}

	public final String getDescription()
	{
		return FormatUtil.format(description);
	}

	public final List<String> getAliases()
	{
		return aliases;
	}

	public final String getName()
	{
		return name;
	}

	public final String getUsageTemplate(final boolean displayHelp)
	{
		StringBuilder ret = new StringBuilder();
		ret.append("&b/" + plugin.getCommandHandler().getCommandPrefix() + " ");

		ret.append(name);

		ret.append("&3 ");
		for (String s : requiredArgs)
			ret.append(String.format("<%s> ", s));

		for (String s : optionalArgs)
			ret.append(String.format("[%s] ", s));

		if (displayHelp)
			ret.append("&e" + description);

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

	protected final void err(String string, Object... objects)
	{
		// sendpMessage("&c" + string, objects);
		sendMessage("&cError: &4" + string, objects);
	}

	protected final void invalidArgs()
	{
		err("Invalid arguments! Try: " + getUsageTemplate(false));
	}

	protected final void log(Level level, String string, Object... objects)
	{
		plugin.outConsole(level, string, objects);
	}

	protected final void log(String string, Object... objects)
	{
		plugin.outConsole(Level.INFO, string, objects);
	}

	protected final void debug(String string, Object... objects)
	{
		plugin.debug(string, objects);
	}

	protected final String capitalize(String string)
	{
		return WordUtils.capitalize(string);
	}
}