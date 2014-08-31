package net.dmulloy2.ultimatearena.commands;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.util.FormatUtil;

import org.apache.commons.lang.WordUtils;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class CmdClassList extends UltimateArenaCommand
{
	public CmdClassList(UltimateArena plugin)
	{
		super(plugin);
		this.name = "classlist";
		this.aliases.add("classes");
		this.description = "list available classes";
		this.permission = Permission.CLASSLIST;
	}

	@Override
	public void perform()
	{
		sendMessage("&3====[ &eUltimateArena Classes &3]====");

		// TODO: Some sort of filter
		for (ArenaClass ac : plugin.getClasses())
		{
			String name = WordUtils.capitalize(ac.getName());
			sendMessage("&3[ &e{0} &3]", name);
			for (ItemStack weapon : ac.getTools())
			{
				name = FormatUtil.getFriendlyName(weapon.getType());
				sendMessage("&b- &e{0} &bx &e{1}", name, weapon.getAmount());
			}
		}
	}
}