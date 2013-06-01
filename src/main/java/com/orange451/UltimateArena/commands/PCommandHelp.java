package com.orange451.UltimateArena.commands;

import java.util.List;
import org.bukkit.ChatColor;
import com.orange451.UltimateArena.UltimateArena;

public class PCommandHelp extends UltimateArenaCommand
{	
	public PCommandHelp(UltimateArena plugin) {
		this.plugin = plugin;
		aliases.add("help");
		aliases.add("h");
		aliases.add("?");
		
		desc = ChatColor.DARK_RED + "<build/admin>" + ChatColor.YELLOW + " display UA help";
	}
	
	@Override
	public void perform() {
		String mmode = "";
		if (parameters.size() == 2) {
			mmode = parameters.get(1);
		}
		sendMessage(ChatColor.DARK_RED + "==== " + ChatColor.GOLD + plugin.getDescription().getFullName() + ChatColor.DARK_RED + " ====");
		List<UltimateArenaCommand> commands = plugin.getCommands();
		for (int i = 0; i < commands.size(); i++) {
			if (commands.get(i).mode.equals(mmode)) {
				String str = "";
				List<String> aliases = commands.get(i).getAliases();
				for (int ii = 0; ii < aliases.size(); ii++) {
					str += ChatColor.GOLD + aliases.get(ii);
					if (aliases.size() - ii > 1) {
						str += ChatColor.DARK_RED + ", ";
					}
				}
				sendMessage(ChatColor.RED + "/ua " + ChatColor.DARK_RED + str + " " + ChatColor.RESET + commands.get(i).getdesc());
			}
		}
	}
}
