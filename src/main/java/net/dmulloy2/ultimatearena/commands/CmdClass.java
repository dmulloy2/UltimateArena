package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaClass;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;

public class CmdClass extends UltimateArenaCommand
{
	public CmdClass(UltimateArena plugin)
	{
		super(plugin);
		this.name = "class";
		this.aliases.add("cl");
		this.optionalArgs.add("class");
		this.description = "Switch UltimateArena classes";
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		if (! plugin.isInArena(player))
		{
			err("You are not in an arena!");
			return;
		}
		
		ArenaPlayer ap = plugin.getArenaPlayer(player);
		
		if (args.length == 0)
		{
			if (ap.getArenaClass() == null)
			{
				err("You do not have a class!");
				return;
			}
			
			sendpMessage("&eYour current class is: &a{0}&e!", ap.getArenaClass().getName());
			return;
		}
		else if (args.length == 1)
		{
			for (ArenaClass cl : plugin.classes)
			{
				if (cl.getName().equalsIgnoreCase(args[0]))
				{
					ap.setClass(cl, true);
					sendpMessage("&eYou have successfully set your class to: &a{0}&e!", cl.getName());
					return;
				}
			}
			
			err("Invalid class \"{0}\"!", args[0]);
			return;
		}
		else
		{
			err("Invalid input! Try /ua class <class>");
			return;
		}
	}
}