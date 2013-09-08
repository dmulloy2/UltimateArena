package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Permission;

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
		String arenaname = args[0];
		ArenaZone az = plugin.getArenaZone(arenaname);
		if (az != null)
		{
			if (az.canLike(player))
			{
				sendpMessage("&aYou have voted for: {0}!", az.getArenaName());

				az.setLiked(az.getLiked() + 1);
				az.getVoted().add(player.getName());
			}
			else
			{
				err("You already voted for this arena!");
			}
		}
		else
		{
			err("This arena doesn't exist!");
		}
	}
}