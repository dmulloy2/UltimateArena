package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;
import com.orange451.UltimateArena.PermissionInterface.PermissionInterface;

public class PCommandEnable extends PBaseCommand {
	
	public PCommandEnable(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("enable");
		aliases.add("en");
		
		mode = "admin";
		
		desc = ChatColor.DARK_RED + "<arena>" + ChatColor.YELLOW + " enable an arena";
	}
	
	@Override
	public void perform() {
		if (PermissionInterface.checkPermission(player, plugin.uaAdmin) || player == null) {
			if (parameters.size() == 2) {
				String at = parameters.get(1);
				for (int ii = 0; ii < plugin.activeArena.size(); ii++) {
					Arena aa = plugin.activeArena.get(ii);
					if (aa.name.equals(at)) {
						aa.disabled = false;
						player.sendMessage(ChatColor.GRAY + "[UltimateArena] Enabled " + at);
					}else if (aa.az.arenaType.equals(at)) {
						aa.disabled = false;
						player.sendMessage(ChatColor.GRAY + "[ULTIMATEARENA] Enabled " + at);
					}
				}
				for (int ii = 0; ii < plugin.loadedArena.size(); ii++) {
					ArenaZone aa = plugin.loadedArena.get(ii);
					if (aa.arenaType.equals(at)) {
						aa.disabled = false;
						player.sendMessage(ChatColor.GRAY + "[UltimateArena] Enabled " + at);
					}else if (aa.arenaName.equals(at)) {
						aa.disabled = false;
						player.sendMessage(ChatColor.GRAY + "[UltimateArena] Enabled " + at);
					}
				}
			}else{
				for (int ii = 0; ii < plugin.activeArena.size(); ii++)
					plugin.activeArena.get(ii).disabled = false;
				for (int ii = 0; ii < plugin.loadedArena.size(); ii++)
					plugin.loadedArena.get(ii).disabled = false;
				player.sendMessage(ChatColor.GRAY + "[UltimateArena] Enabled ALL arenas");
			}
		}
	}
}
