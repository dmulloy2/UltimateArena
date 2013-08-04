package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaClass;
import net.dmulloy2.ultimatearena.util.FormatUtil;

import org.apache.commons.lang.WordUtils;
import org.bukkit.inventory.ItemStack;

public class CmdClassList extends UltimateArenaCommand
{
	public CmdClassList(UltimateArena plugin)
	{
		super(plugin);
		this.name = "classlist";
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
			String name = WordUtils.capitalize(ac.getName());
			sendMessage("&4===[ &6{0} &4]===", name);
			for (ItemStack weapon : ac.getWeapons())
			{
				name = FormatUtil.getFriendlyName(weapon.getType());
				sendMessage("&6- {0} x {1}", name, weapon.getAmount());
			}
		}
	}
}