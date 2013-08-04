package net.dmulloy2.ultimatearena.arenas.objects;

import java.util.ArrayList;
import java.util.List;

public class WhiteListCommands
{
	private List<String> allowedCommand;
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
			
			StringBuilder check1 = new StringBuilder();
			for (int i = 0; i < check.length; i++)
			{
				check1.append(check[i]);
			}
			
			if (cmd.equalsIgnoreCase(check1.toString()))
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
	
	public int size()
	{
		return allowedCommand.size();
	}
}