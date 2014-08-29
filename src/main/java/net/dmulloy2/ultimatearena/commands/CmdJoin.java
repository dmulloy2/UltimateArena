/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdJoin extends UltimateArenaCommand
{
	public CmdJoin(UltimateArena plugin)
	{
		super(plugin);
		this.name = "join";
		this.aliases.add("j");
		this.requiredArgs.add("arena");
		this.optionalArgs.add("player");
		this.description = "join/start an UltimateArena";
	}

	@Override
	public void perform()
	{
		Player join = player;
		if (args.length > 1 && hasPermission(Permission.JOIN_OTHERS))
			join = Util.matchPlayer(args[1]);

		if (join == null)
		{
			err("Player not found!");
			return;
		}

		plugin.attemptJoin(player, args[0]);
	}
}