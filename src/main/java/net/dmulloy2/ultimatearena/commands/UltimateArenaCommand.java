package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.commands.Command;
import net.dmulloy2.ultimatearena.UltimateArena;

/**
 * @author dmulloy2
 */

public abstract class UltimateArenaCommand extends Command
{
	protected final UltimateArena plugin;
	public UltimateArenaCommand(UltimateArena plugin)
	{
		super(plugin);
		this.plugin = plugin;
	}
}