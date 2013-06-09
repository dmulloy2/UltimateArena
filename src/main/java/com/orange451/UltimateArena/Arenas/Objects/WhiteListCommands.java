package com.orange451.UltimateArena.Arenas.Objects;

import java.util.ArrayList;
import java.util.List;

public class WhiteListCommands
{
	public List<String> allowedCommand;
	public WhiteListCommands() 
	{
		this.allowedCommand = new ArrayList<String>();
	}
	
	public void addCommand(String str)
	{
		allowedCommand.add(str);
	}
	
	public boolean isAllowed(String[] check)
	{
		for (String cmd : allowedCommand)
		{
			if (cmd.equalsIgnoreCase(check[0]))
			{
				return true;
			}
			else if (cmd.equalsIgnoreCase(check.toString()))
			{
				return true;
			}
		}
		return false;
	}
	
	public void clear() 
	{
		allowedCommand.clear();
	}
}