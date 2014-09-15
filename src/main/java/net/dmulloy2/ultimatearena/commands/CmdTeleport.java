/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Permission;

import org.bukkit.Location;

/**
 * @author dmulloy2
 */

public class CmdTeleport extends UltimateArenaCommand
{
	public CmdTeleport(UltimateArena plugin)
	{
		super(plugin);
		this.name = "teleport";
		this.aliases.add("tp");
		this.addRequiredArg("arena");
		this.description = "teleport to an arena";
		this.permission = Permission.TELEPORT;
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		if (plugin.isInArena(player))
		{
			err("You cannot teleport while in an arena!");
			return;
		}

		ArenaZone az = plugin.getArenaZone(args[0]);
		if (az == null)
		{
			err("Arena \"&c{0}&4\" not found!", args[0]);
			return;
		}

		Location loc = az.getLobby1().getLocation();
		player.teleport(loc.clone().add(0.0D, 1.0D, 0.0D));

		sendpMessage("&3You have been teleported to the spawn of &e{0}&3!", az.getName());
	}
}