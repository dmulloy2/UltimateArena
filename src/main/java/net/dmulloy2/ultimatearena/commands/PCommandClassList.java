package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaClass;
import net.dmulloy2.ultimatearena.util.FormatUtil;

import org.bukkit.inventory.ItemStack;

public class PCommandClassList extends UltimateArenaCommand
{
	public PCommandClassList(UltimateArena plugin)
	{
		super(plugin);
		this.name = "classlist";
		this.aliases.add("cl");
		this.aliases.add("classes");
		this.description = "List UltimateArena classes";
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		sendMessage("&4====[ &6UltimateArena Classes &4]====");

		for (ArenaClass ac : plugin.classes)
		{
			sendMessage("&4===[ &6{0} &4]===", ac.getName());
			for (ItemStack weapon : ac.getWeapons())
			{
				String name = FormatUtil.getFriendlyName(weapon.getType());
				sendMessage("&6- {0}", name);
			}
		}
	}
}