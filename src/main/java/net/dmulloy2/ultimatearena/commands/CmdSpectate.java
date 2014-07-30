package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
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
		ArenaPlayer ap = plugin.getArenaPlayer(player);
		if (ap != null)
		{
			err("You cannot spectate while in an arena!");
			return;
		}

		if (plugin.getSpectatingHandler().isSpectating(player))
		{
			plugin.getSpectatingHandler().removeSpectator(player);
			sendpMessage("&eYou are no longer spectating!");
			return;
		}

		Arena arena = getArena(0);
		if (arena == null)
		{
			err("You must specify a valid arena!");
			return;
		}

		plugin.getSpectatingHandler().addSpectator(arena, player);

		sendpMessage("&eYou are now spectating &e{0}", arena.getName());
		sendpMessage("&3To stop spectating, use &e/ua spectate &3again");
	}
}