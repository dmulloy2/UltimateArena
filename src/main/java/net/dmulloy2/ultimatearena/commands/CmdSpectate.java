package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.handlers.SpectatingHandler;
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
		this.optionalArgs.add("arena");
		this.description = "Spectates an arena";
		this.permission = Permission.SPECTATE;

		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		SpectatingHandler spectatingHandler = plugin.getSpectatingHandler();
		if (spectatingHandler.isSpectating(player))
		{
			spectatingHandler.removeSpectator(player);
			sendpMessage("&3You are no longer spectating");
		}
		else
		{
			Arena arena = null;
			if (args.length != 0)
				arena = plugin.getArena(args[0]);

			if (arena == null)
			{
				err("Please secify a valid arena!");
				return;
			}

			spectatingHandler.addSpectator(arena, player);
			sendpMessage("&eYou are now spectating &e{0}", arena.getName());
			sendpMessage("&3To stop spectating, use &e/ua spectate &3again");
		}
	}
}