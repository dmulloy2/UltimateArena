package com.orange451.UltimateArena.PermissionInterface;

import org.bukkit.entity.Player;

import com.orange451.UltimateArena.UltimateArena;

public class PermissionInterface {
	public static UltimateArena arena;
	
	public static boolean checkPermission(Player player, String command) {
		try{
			if (player.isOp() || player.hasPermission(command) || arena.isUaAdmin(player)) {
				return true;
			}else{
				return false;
			}
		}catch(Exception e) {
			//
		}
		return true;
	}
	
	public static void Initialize(UltimateArena a) {
		arena = a;
	}
}

