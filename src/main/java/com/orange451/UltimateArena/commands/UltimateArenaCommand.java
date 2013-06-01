package com.orange451.UltimateArena.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.Permission;

public abstract class UltimateArenaCommand
{
	public List<String> aliases;
	public CommandSender sender;
	public Player player;
	public String desc;
	public List<String> parameters;
	public UltimateArena plugin;
	public String mode = "";
	
	protected Permission permission;
	
	public UltimateArenaCommand()
	{
		aliases = new ArrayList<String>();
	}

	public void execute(CommandSender sender, List<String> parameters) 
	{
		this.sender = sender;
		this.parameters = parameters;
		
		if (sender instanceof Player)
		{
			this.player = (Player)sender;
		}
		
		if (hasPermission())
			perform();
		else
			sendMessage("&cYou do not have permission to do this!");
	}
	
	private final boolean hasPermission()
	{
		return (plugin.getPermissionHandler().hasPermission(sender, permission));
	}

	public String getdesc() 
	{
		return desc;
	}
	
	public abstract void perform();
	
	public List<String> getAliases() 
	{
		return aliases;
	}
	
	protected final void sendMessage(String message)
	{
		message = ChatColor.translateAlternateColorCodes('&', message);
		sender.sendMessage(message);
	}
	
	public void sendMessage(List<String> messages)
	{
		for(String message : messages) 
		{
			this.sendMessage(message);
		}
	}
}