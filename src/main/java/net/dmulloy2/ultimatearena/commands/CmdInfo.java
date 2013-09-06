package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.permissions.Permission;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;

import org.apache.commons.lang.WordUtils;

public class CmdInfo extends UltimateArenaCommand
{
	public CmdInfo(UltimateArena plugin)
	{
		super(plugin);
		this.name = "info";
		this.optionalArgs.add("arena");
		this.description = "view info on the arena you are in";
		this.permission = Permission.INFO;

		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		if (args.length == 0)
		{
			if (plugin.isInArena(player))
			{
				Arena ar = plugin.getArena(player);
				if (ar != null)
				{
					sendMessage("&3====[ &e{0} &3]====", WordUtils.capitalize(ar.getName()));

					ArenaPlayer ap = plugin.getArenaPlayer(player);
					if (ap != null)
					{
						if (ap.isOut())
						{
							sendMessage("&3You are &cOUT&3!");
						}
						else
						{
							sendMessage("&3You are &aNOT OUT&3!");
						}
					}

					sendMessage(""); // Empty line

					sendMessage("&3Active Players:");
					for (String s : ar.buildLeaderboard(player))
					{
						sendMessage(s);
					}
				}
			}
			else
			{
				err("You are not in an arena!");
			}
		}
		else if (args.length == 1)
		{
			String arenaname = args[0];
			Arena ar = plugin.getArena(arenaname);
			if (ar != null)
			{
				sendMessage("&3====[ &e{0} &3]====", ar.getName());

				sendMessage("&3Type: &e{0}", ar.getType());

				sendMessage(""); // Empty line

				sendMessage("&3&lActive Players:");

				for (String s : ar.buildLeaderboard(player))
				{
					sendMessage(s);
				}
			}
			else
			{
				err("This arena isn't running!");
			}
		}
		else
		{
			err("Please supply an arena name");
		}
	}
}