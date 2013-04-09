package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;

public class PCommandDislike extends PBaseCommand {
	
	public PCommandDislike(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("dislike");
		aliases.add("d");
		
		desc = ChatColor.DARK_RED + "<arena> " + ChatColor.YELLOW + " dislike an arena";
	}
	
	@Override
	public void perform() {
		if (parameters.size() == 2) {
			String arenaname = parameters.get(1);
			ArenaZone az = this.plugin.getArenaZone(arenaname);
			if (az != null) {
				if (az.canLike(player)) {
					az.disliked++;
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