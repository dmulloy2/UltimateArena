package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;

public class PCommandLike extends UltimateArenaCommand
{	
	public PCommandLike(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("like");
		aliases.add("l");
		
		desc = ChatColor.DARK_RED + "<arena>" + ChatColor.YELLOW + " like an arena";
	}
	
	@Override
	public void perform() {
		if (parameters.size() == 2) {
			String arenaname = parameters.get(1);
			ArenaZone az = this.plugin.getArenaZone(arenaname);
			if (az != null) {
				if (az.canLike(player)) {
					az.liked++;
					az.voted.add(player.getName());
				}else{
					sendMessage(ChatColor.RED + "You already voted for this arena!");
				}
			}else{
				sendMessage(ChatColor.RED + "This arena doesn't exist!");
			}
		}
	}
}