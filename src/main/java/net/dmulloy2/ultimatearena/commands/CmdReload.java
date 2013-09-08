package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.Permission;

import org.bukkit.plugin.PluginManager;

public class CmdReload extends UltimateArenaCommand
{
	public CmdReload(UltimateArena plugin)
	{
		super(plugin);
		this.name = "reload";
		this.aliases.add("rl");
		this.description = "reload UltimateArena";
		this.permission = Permission.RELOAD;

		this.mustBePlayer = false;
	}

	@Override
	public void perform()
	{
		sendMessage("&aReloading UltimateArena...");

		long start = System.currentTimeMillis();

		PluginManager pm = plugin.getServer().getPluginManager();
		pm.disablePlugin(plugin);
		pm.enablePlugin(plugin);

		long finish = System.currentTimeMillis();

		sendMessage("&aReload Complete! Took {0} ms!", finish - start);
	}
}