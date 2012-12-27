package com.orange451.UltimateArena.commands;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.PermissionInterface.PermissionInterface;

public class PCommandSetPoint extends PBaseCommand {
	
	public PCommandSetPoint(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("setpoint");
		aliases.add("sp");
		
		mode = "build";
		
		desc = "to set a point of your field";
	}
	
	@Override
	public void perform() {
		if (PermissionInterface.checkPermission(player, plugin.uaBuilder) || PermissionInterface.checkPermission(player, plugin.uaAdmin)) {
			plugin.setPoint(player);
		}
	}
}
