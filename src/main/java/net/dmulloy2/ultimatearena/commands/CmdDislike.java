package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdDislike extends UltimateArenaCommand
{
	public CmdDislike(UltimateArena plugin)
	{
		super(plugin);
		this.name = "dislike";
		this.aliases.add("d");
		this.requiredArgs.add("arena");
		this.description = "dislike an arena";
		this.permission = Permission.DISLIKE;

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

		sendpMessage("&cYou have disliked: " + az.getName());

		az.setDisliked(az.getDisliked() + 1);
		az.getVoted().add(player.getName());
	}
}