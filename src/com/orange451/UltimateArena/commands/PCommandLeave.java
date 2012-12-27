package com.orange451.UltimateArena.commands;

import com.orange451.UltimateArena.UltimateArena;

public class PCommandLeave extends PBaseCommand {
	
	public PCommandLeave(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("leave");
		aliases.add("l");
		
		desc = "to leave an arena";
	}
	
	@Override
	public void perform() {
		plugin.leaveArena(player);
	}
}
