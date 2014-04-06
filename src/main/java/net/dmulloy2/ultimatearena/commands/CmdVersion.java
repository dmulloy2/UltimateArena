package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdVersion extends UltimateArenaCommand
{
	public CmdVersion(UltimateArena plugin)
	{
		super(plugin);
		this.name = "version";
		this.aliases.add("v");
		this.description = "Displays version information";

		this.permission = Permission.VERSION;
	}

	@Override
	public void perform()
	{
		sendMessage("&3====[ &eUltimateArena &3]====");
		sendMessage("&bAuthor: &edmulloy2");
		sendMessage("&bVersion: &e{0}", plugin.getDescription().getVersion());
		sendMessage("&bBukkitDev Link:&e http://dev.bukkit.org/bukkit-plugins/ultimatearena");
	}
}