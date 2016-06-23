/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2012 - 2015 MineSworn
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.ultimatearena.commands;

import org.apache.commons.lang.WordUtils;
import org.bukkit.inventory.ItemStack;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaClass;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.util.MaterialUtil;

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
		sendMessage(getMessage("genericHeader"), "UltimateArena Classes");

		for (ArenaClass ac : plugin.getClasses())
		{
			String name = WordUtils.capitalize(ac.getName());
			sendMessage(getMessage("classHeader"), name);
			for (ItemStack weapon : ac.getTools().values())
			{
				name = MaterialUtil.getName(weapon);
				sendMessage(getMessage("classItem"), name, weapon.getAmount());
			}
		}
	}
}
