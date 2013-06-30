package com.orange451.UltimateArena.commands;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaClass;
import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;

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
			if (ap.mclass == null)
			{
				sendMessage("&cYou do not have a class!");
				return;
			}
			
			sendMessage("&eYour current class is: &a{0}&e!", ap.mclass.name);
			return;
		}
		else if (args.length == 1)
		{
			for (ArenaClass cl : plugin.classes)
			{
				if (cl.name.equalsIgnoreCase(args[0]))
				{
					ap.setClass(cl);
					sendMessage("&eYou have successfully set your class to: &a{0}&e!", cl.name);
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