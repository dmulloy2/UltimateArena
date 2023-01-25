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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.dmulloy2.swornapi.types.StringJoiner;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.swornapi.util.NumberUtil;
import net.dmulloy2.swornapi.util.Util;

/**
 * @author dmulloy2
 */

public class CmdOption extends UltimateArenaCommand
{
	private static List<Field> options;

	public CmdOption(UltimateArena plugin)
	{
		super(plugin);
		this.name = "option";
		this.addRequiredArg("type");
		this.addRequiredArg("name");
		this.addRequiredArg("option");
		this.addRequiredArg("value");
		this.description = "change configuration options";
		this.permission = Permission.OPTION;

		this.mustBePlayer = false;
	}

	@Override
	public void perform()
	{
		Field option = getOption(args[2]);
		if (option == null)
		{
			StringJoiner joiner = new StringJoiner("&4, &c");

			for (Field field : options)
			{
				joiner.append(field.getName());
			}

			err("Unknown option \"&c{0}&4\"!", args[2]);
			err("Available options: &c{0}", joiner.toString());
			return;
		}

		// Convert to the proper type
		Object value = args[3];
		if (option.getType().equals(int.class))
		{
			if (! NumberUtil.isInt(value))
			{
				err("\"&c{0}&4\" is not a valid integer!", value);
				return;
			}

			value = NumberUtil.toInt(value);
		}
		else if (option.getType().equals(double.class))
		{
			if (! NumberUtil.isDouble(args[3]))
			{
				err("\"&c{0}&4\" is not a valid double!", value);
				return;
			}

			value = NumberUtil.toDouble(value);
		}
		else if (option.getType().equals(boolean.class))
		{
			value = Util.toBoolean(value);
		} // else it's a string and we're ok

		if (args[0].equalsIgnoreCase("arena"))
		{
			ArenaZone az = plugin.getArenaZone(args[1]);
			if (az == null)
			{
				err("Could not find an Arena by the name of \"&c{0}&4\"!", args[1]);
				return;
			}

			ArenaConfig config = az.getConfig();

			try
			{
				boolean accessible = option.isAccessible();
				option.setAccessible(true);

				option.set(config, value);

				option.setAccessible(accessible);
			}
			catch (Throwable ex)
			{
				err("Failed to set option &c{0} &4to &c{1}&4! Check console!", option, value);
				plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "setting {0} to {1}", option, value));
			}

			az.saveToDisk();
			az.reload();

			sendpMessage("&3You have set \"&e{0}&3\" to \"&e{1}&3\" for arena &e{2}", option.getName(), value, az.getName());
		}
		else if (args[0].equalsIgnoreCase("config"))
		{
			ArenaType type = plugin.getArenaTypeHandler().getArenaType(args[1]);
			if (type == null)
			{
				err("\"&c{0}&4\" is not a valid arena type!", args[1]);
				return;
			}

			ArenaConfig config = type.getConfig();

			try
			{
				boolean accessible = option.isAccessible();
				option.setAccessible(true);

				option.set(config, value);

				option.setAccessible(accessible);
			}
			catch (Throwable ex)
			{
				err("Failed to set option &c{0} &4to &c{1}&4! Check console!", option, value);
				plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "setting {0} to {1}", option, value));
			}

			config.save();
			config.reload();

			sendpMessage("&3You have set \"&e{0}&3\" to \"&e{1}&3\" for type &e{2}", option.getName(), value, type.getName());
		}
		else
		{
			err("Please specify either \"&carena&4\" or \"&cconfig&4\"!");
		}
	}

	private Field getOption(String option)
	{
		for (Field field : options)
		{
			if (field.getName().equalsIgnoreCase(option))
			{
				return field;
			}
		}

		return null;
	}

	static
	{
		options = new ArrayList<>();

		Field[] fields = ArenaConfig.class.getDeclaredFields();
		for (Field field : fields)
		{
			// Skip transient, final, and static
			int modifiers = field.getModifiers();
			if (Modifier.isTransient(modifiers) || Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers))
				continue;

			// We only support the following types: boolean, integer, string,double
			Class<?> type = field.getType();
			if (type.equals(int.class) || type.equals(boolean.class) || type.equals(String.class) || type.equals(double.class))
				options.add(field);
		}
	}
}
