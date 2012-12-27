package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;
import com.orange451.UltimateArena.PermissionInterface.PermissionInterface;
import com.orange451.UltimateArena.util.Util;

public class PCommandKick extends PBaseCommand {
	
	public PCommandKick(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("kick");
		aliases.add("k");
		
		mode = "admin";
		
		desc = ChatColor.WHITE + "[playername] " + ChatColor.YELLOW + "to kick a player from an arena";
	}
	
	@Override
	public void perform() {
		if (PermissionInterface.checkPermission(player, plugin.uaAdmin)) {
			if (parameters.size() == 2) {
				Player p = Util.MatchPlayer(parameters.get(1));
				if (p != null) {
					ArenaPlayer ap = plugin.getArenaPlayer(p);
					if (ap != null) {
						Arena a = plugin.getArena(p);
						if (a != null) {
							a.endPlayer(ap, false);
							ap.out = true;
							ap.deaths = 999999999;
							ap.points = 0;
							ap.kills = 0;
							ap.XP = 0;
							player.sendMessage(ChatColor.GRAY + "Kicked player: " + ChatColor.GOLD + p.getName() + ChatColor.GRAY + " from arena: " + ChatColor.GOLD + a.name);
						}
					}else{
						player.sendMessage(ChatColor.GRAY + "Player: " + ChatColor.GOLD + p.getName() + ChatColor.GRAY + " is not in an Arena");
					}
				}else{
					player.sendMessage(ChatColor.GRAY + "Player: " + ChatColor.GOLD + parameters.get(1) + ChatColor.GRAY + " is not online");
				}
			}
		}
	}
}
