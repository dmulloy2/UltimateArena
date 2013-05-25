package com.orange451.UltimateArena.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.orange451.UltimateArena.UltimateArena;

public class PBaseCommand {
	public List<String> aliases;
	public CommandSender sender;
	public Player player;
	public String desc;
	public List<String> parameters;
	public UltimateArena plugin;
	public String mode = "";
	
	public PBaseCommand() {
		aliases = new ArrayList<String>();
	}

	public void execute(CommandSender sender, List<String> parameters) {
		this.sender = sender;
		this.parameters = parameters;
		
		if (sender instanceof Player) {
			this.player = (Player)sender;
		}
		
		perform();
	}
	
	public String getdesc() {
		return desc;
	}
	
	public void perform() {
		
	}
	
	public List<String> getAliases() {
		return aliases;
	}
	
	protected final void sendMessage(String message)
	{
		message = ChatColor.translateAlternateColorCodes('&', message);
		sender.sendMessage(message);
	}
	
	public void sendMessage(List<String> messages) {
		for(String message : messages) {
			this.sendMessage(message);
		}
	}
}