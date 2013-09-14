package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.ultimatearena.util.FormatUtil;

/**
 * @author dmulloy2
 */

public class CmdClass extends UltimateArenaCommand
{
	public CmdClass(UltimateArena plugin)
	{
		super(plugin);
		this.name = "class";
		this.aliases.add("cl");
		this.optionalArgs.add("class");
		this.description = "Switch UltimateArena classes";
		this.permission = Permission.CLASS;

		this.mustBeInArena = true;
	}

	@Override
	public void perform()
	{
		ArenaPlayer ap = plugin.getArenaPlayer(player);

		if (args.length == 0)
		{
			if (ap.getArenaClass() == null)
			{
				err("You do not have a class!");
				return;
			}

			sendpMessage("&3Your current class is: &e{0}", ap.getArenaClass().getName());
		}
		else if (args.length == 1)
		{
			ArenaClass cl = plugin.getArenaClass(args[0]);
			if (cl == null)
			{
				err("You do not have permissions for this class.");
				return;
			}
			
			if (! cl.checkPermission(player))
			{
				err("You do not have permissions for this class.");
				return;
			}

			ap.setClass(cl);
	
			String name = cl.getName();
			String article = FormatUtil.getArticle(name);
	
			if (ap.getArena().isInLobby())
			{
				sendpMessage("&3You will spawn as {0}: &e{1}", article, name);
			}
			else
			{
				sendpMessage("&3You will respawn as {0}: &e{1}", article, name);
			}
		}
	}
}