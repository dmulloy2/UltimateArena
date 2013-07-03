package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaClass;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;

public class PCommandClass extends UltimateArenaCommand
{
	public PCommandClass(UltimateArena plugin)
	{
		super(plugin);
		this.name = "class";
		this.optionalArgs.add("class");
		this.description = "Switch UltimateArena classes";
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		if (! plugin.isInArena(player))
		{
			sendMessage("&cYou are not in an arena!");
			return;
		}
		
		ArenaPlayer ap = plugin.getArenaPlayer(player);
		
		if (args.length == 0)
		{
			if (ap.getArenaClass() == null)
			{
				sendMessage("&cYou do not have a class!");
				return;
			}
			
			sendMessage("&eYour current class is: &a{0}&e!", ap.getArenaClass().getName());
			return;
		}
		else if (args.length == 1)
		{
			for (ArenaClass cl : plugin.classes)
			{
				if (cl.getName().equalsIgnoreCase(args[0]))
				{
					ap.setClass(cl, true);
					sendMessage("&eYou have successfully set your class to: &a{0}&e!", cl.getName());
					return;
				}
			}
			
			sendMessage("&cInvalid class \"{0}\"!", args[0]);
			return;
		}
		else
		{
			sendMessage("&cInvalid input! Try /ua class <class>");
			return;
		}
	}
}