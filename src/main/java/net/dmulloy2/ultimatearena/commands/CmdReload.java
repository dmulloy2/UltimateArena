package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdReload extends UltimateArenaCommand
{
	public CmdReload(UltimateArena plugin)
	{
		super(plugin);
		this.name = "reload";
		this.aliases.add("rl");
		this.description = "reload UltimateArena";
		this.permission = Permission.RELOAD;

		this.mustBePlayer = false;
	}

	@Override
	public void perform()
	{
		long start = System.currentTimeMillis();
		
		sendMessage("&aReloading UltimateArena...");

		plugin.reload();

		sendMessage("&aReload Complete! Took {0} ms!", System.currentTimeMillis() - start);
	}
}