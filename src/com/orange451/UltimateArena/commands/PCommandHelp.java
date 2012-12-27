package com.orange451.UltimateArena.commands;

import java.util.List;
import org.bukkit.ChatColor;
import com.orange451.UltimateArena.UltimateArena;

public class PCommandHelp extends PBaseCommand {
	
	public PCommandHelp(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("help");
		aliases.add("h");
		aliases.add("?");
		
		desc = ChatColor.WHITE + "[build/admin]" + ChatColor.YELLOW + "to view UA help";
	}
	
	@Override
	public void perform() {
		String mmode = "";
		if (parameters.size() == 2) {
			mmode = parameters.get(1);
		}
		sendMessage(ChatColor.AQUA + "~~~" + ChatColor.GOLD + "ULTIMATEARENA HELP" + ChatColor.AQUA + "~~~");
		List<PBaseCommand> commands = plugin.getCommands();
		for (int i = 0; i < commands.size(); i++) {
			if (commands.get(i).mode.equals(mmode)) {
				String str = "";
				List<String> aliases = commands.get(i).getAliases();
				for (int ii = 0; ii < aliases.size(); ii++) {
					str += aliases.get(ii);
					if (aliases.size() - ii > 1) {
						str += ", ";
					}
				}
				sendMessage("/ua " + str + " " + ChatColor.YELLOW + commands.get(i).getdesc());
			}
		}
	}
}
