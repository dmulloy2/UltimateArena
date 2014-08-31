/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.util.NumberUtil;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdForceJoin extends UltimateArenaCommand
{
	public CmdForceJoin(UltimateArena plugin)
	{
		super(plugin);
		this.name = "forcejoin";
		this.aliases.add("fj");
		this.requiredArgs.add("arena");
		this.requiredArgs.add("player");
		this.optionalArgs.add("team");
		this.description = "force a player into an arena";
		this.permission = Permission.JOIN_FORCE;
	}

	@Override
	public void perform()
	{
		Player player = Util.matchPlayer(args[1]);
		if (player == null)
		{
			err("Player \"&c{0}&4\" not found!", args[1]);
			return;
		}

		int team = -1;
		if (args.length > 3)
			team = NumberUtil.toInt(args[2]);

		plugin.attemptJoin(player, args[0], team);
	}
}