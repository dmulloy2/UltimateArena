package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdSpectate extends UltimateArenaCommand
{
	public CmdSpectate(UltimateArena plugin)
	{
		super(plugin);
		this.name = "spectate";
		this.requiredArgs.add("arena");
		this.description = "Spectates an arena";
		this.permission = Permission.SPECTATE;
		
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		Arena arena = plugin.getArena(args[0]);
		if (arena == null)
		{
			err("Could not find an active arena by the name of {0}", args[0]);
			return;
		}
		
		sendpMessage("&3Spectating arena: &e{0}", arena.getName());
		
		plugin.getSpectatingHandler().addSpectator(arena, player);
	}
}