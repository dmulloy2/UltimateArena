package com.orange451.UltimateArena.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;

public class PCommandInfo extends PBaseCommand {
	
	public PCommandInfo(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("info");
		aliases.add("i");
		
		desc = "to view info on the arena you're in";
	}
	
	@Override
	public void perform() {
		if (parameters.size() == 1) {
			if (plugin.isInArena(player)) {
				Arena ar = plugin.getArena(player);
				if (ar != null) {
					sendMessage(ChatColor.AQUA + "~~~" + ChatColor.GOLD + "Arena: " + ChatColor.GRAY + ar.az.arenaName + ChatColor.AQUA + "~~~");
					ArenaPlayer ap = plugin.getArenaPlayer(player);
					if (ap != null) {
						String out = ChatColor.GREEN + "NOT OUT";
						if (ap.out) {
							out = ChatColor.RED + "OUT";
						}
						sendMessage(ChatColor.GRAY + "YOU ARE: " + out);
					}
					ArrayList<ArenaPlayer> arr = ar.arenaplayers;
					for (int i = 0; i < arr.size(); i++) {
						try{
							if (arr.get(i).out != true) {
								String playerName = ChatColor.BLUE + "[" + ChatColor.GRAY + arr.get(i).player.getName() + ChatColor.BLUE + "]";
								String playerHealth = ChatColor.BLUE + "[" + ChatColor.GRAY + ((arr.get(i).player.getHealth() / 20.0) * 100) + ChatColor.BLUE + "%]";
								sendMessage(playerName + playerHealth);
							}
						}catch(Exception e) {
							//
						}
					}
					
				}
			}else{
				sendMessage(ChatColor.RED + "You are not in an arena!");
			}
		}else{
			if (parameters.size() == 2) {
				String arenaname = parameters.get(1);
				Arena ar = this.plugin.getArena(arenaname);
				if (ar != null) {
					sendMessage(ChatColor.GRAY + "Arena: " + ChatColor.GOLD + arenaname);
					sendMessage(ChatColor.GRAY + "Type: " + ChatColor.GOLD + ar.az.arenaType);
					sendMessage(ChatColor.GRAY + "Players:");
					ArrayList<ArenaPlayer> arr = ar.arenaplayers;
					if (arr.size() > 0) {
						for (int i = 0; i < arr.size(); i++) {
							try{
								if (arr.get(i).out != true) {
									String playerName = ChatColor.BLUE + "[" + ChatColor.GRAY + arr.get(i).player.getName() + ChatColor.BLUE + "]";
									String playerHealth = ChatColor.BLUE + "[" + ChatColor.GRAY + ((arr.get(i).player.getHealth() / 20.0) * 100) + ChatColor.BLUE + "%]";
									sendMessage(playerName + playerHealth);
								}
							}catch(Exception e) {
								//
							}
						}
					}else{
						sendMessage(ChatColor.RED + "No players in the arena");
					}
				}else{
					sendMessage(ChatColor.GRAY + "This arena isn't running!");
				}
			}else{
				sendMessage(ChatColor.GRAY + "Please supply an arena name");
			}
		}
	}
}