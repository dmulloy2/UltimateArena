package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdLike extends UltimateArenaCommand
{
	public CmdLike(UltimateArena plugin)
	{
		super(plugin);
		this.name = "like";
		this.requiredArgs.add("arena");
		this.description = "like an arena";
		this.permission = Permission.LIKE;

		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		ArenaZone az = plugin.getArenaZone(args[0]);
		if (az == null)
		{
			err("This arena doesn't exist!");
			return;
		}

		if (! az.canLike(player))
		{
			err("You already voted for this arena!");
			return;
		}

		sendpMessage("&aYou have voted for: {0}!", az.getName());

		az.setLiked(az.getLiked() + 1);
		az.getVoted().add(player.getName());
	}
}