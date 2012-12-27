package com.orange451.UltimateArena.Arenas.Objects;

import java.util.ArrayList;
import java.util.List;

public class WhiteListCommands {
	public List<String> allowedCommand;
	
	public WhiteListCommands() {
		this.allowedCommand = new ArrayList<String>();
	}
	
	public void addCommand(String str) {
		allowedCommand.add(str);
	}
	
	public boolean isAllowed(String str) {
		for (int i = 0; i < allowedCommand.size(); i++) {
			if (allowedCommand.get(i).contains(str)) {
				return true;
			}
		}
		return false;
	}
	
	public void clear() {
		this.allowedCommand.clear();
	}
}
