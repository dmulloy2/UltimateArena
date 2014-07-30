package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.LeaveReason;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdKick extends UltimateArenaCommand
{
	public CmdKick(UltimateArena plugin)
	{
		super(plugin);
		this.name = "kick";
		this.aliases.add("k");
		this.requiredArgs.add("player");
		this.description = "kick a player from an arena";
		this.permission = Permission.KICK;

		this.mustBePlayer = false;
	}

	@Override
	public void perform()
	{
		Player player = Util.matchPlayer(args[0]);
		if (player == null)
		{
			err("Player \"&c{0}&4\" not found!", args[0]);
			return;
		}

		ArenaPlayer ap = plugin.getArenaPlayer(player);
		if (ap == null)
		{
			err("That player is not in an arena!");
			return;
		}

		ap.leaveArena(LeaveReason.KICK);
	}
}