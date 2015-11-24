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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.util.NumberUtil;
import net.dmulloy2.util.Util;

/**
 * @author dmulloy2
 */

// TODO: Update this for 3.0
public class CmdOption extends UltimateArenaCommand
{
	private static Map<String, Type> options;
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
		String option = args[2];
		if (! options.containsKey(option))
		{
			StringBuilder line = new StringBuilder();
			line.append("Available options: ");
			for (String s : options.keySet())
			{
				line.append("&c" + s + "&4, ");
			}

			if (line.lastIndexOf(", ") != -1)
			{
				line.delete(line.lastIndexOf(","), line.length());
			}

			err(line.toString());
			return;
		}

		Object value = null;
		Type type = options.get(option);
		if (type == Integer.TYPE)
		{
			if (! NumberUtil.isInt(args[3]))
			{
				err("\"&c{0}&4\" is not a valid integer!");
				return;
			}

			value = NumberUtil.toInt(args[3]);
		}
		else if (type == Boolean.TYPE)
		{
			value = Boolean.parseBoolean(args[3]);
		}

		if (args[0].equalsIgnoreCase("arena"))
		{
			ArenaZone az = plugin.getArenaZone(args[1]);
			if (az == null)
			{
				err("Could not find an Arena by the name of \"&c{0}&4\"!", args[1]);
				return;
			}

			switch (option)
			{
				case "gameTime":
					az.getConfig().setGameTime(NumberUtil.toInt(value));
					break;
				case "lobbyTime":
					az.getConfig().setLobbyTime(NumberUtil.toInt(value));
					break;
				case "maxDeaths":
					az.getConfig().setMaxDeaths(NumberUtil.toInt(value));
					break;
//				case "maxWave":
//					az.getConfig().setMaxWave(NumberUtil.toInt(value));
//					break;
				case "cashReward":
					az.getConfig().setCashReward(NumberUtil.toInt(value));
					break;
//				case "maxPoints":
//					az.getConfig().setMaxPoints(NumberUtil.toInt(value));
//					break;
				case "allowTeamKilling":
					az.getConfig().setAllowTeamKilling(Util.toBoolean(value));
					break;
				case "countMobKills":
					az.getConfig().setCountMobKills(Util.toBoolean(value));
					break;
				case "rewardBasedOnXp":
					az.getConfig().setRewardBasedOnXp(Util.toBoolean(value));
					break;
				case "giveRewards":
					az.getConfig().setGiveRewards(Util.toBoolean(value));
					break;
				default:
					err("Unsupported option: &c{0}", option);
					break;
			}

			az.saveToDisk();
			az.reload();

			sendpMessage("&3You have set \"&e{0}&3\" to \"&e{1}&3\" for arena &e{2}", option, value, az.getName());
		}
		else if (args[0].equalsIgnoreCase("config"))
		{
			ArenaType at = plugin.getArenaTypeHandler().getArenaType(args[0]);
			ArenaConfig ac = at.getConfig();
			if (ac == null)
			{
				err("\"&c{0}&4\" is not a valid Field Type!");
				return;
			}

			switch (option)
			{
				case "gameTime":
					ac.setGameTime(NumberUtil.toInt(value));
					break;
				case "lobbyTime":
					ac.setLobbyTime(NumberUtil.toInt(value));
					break;
				case "maxDeaths":
					ac.setMaxDeaths(NumberUtil.toInt(value));
					break;
//				case "maxWave":
//					ac.setMaxWave(NumberUtil.toInt(value));
//					break;
				case "cashReward":
					ac.setCashReward(NumberUtil.toInt(value));
					break;
//				case "maxPoints":
//					ac.setMaxPoints(NumberUtil.toInt(value));
//					break;
				case "allowTeamKilling":
					ac.setAllowTeamKilling(Util.toBoolean(value));
					break;
				case "countMobKills":
					ac.setCountMobKills(Util.toBoolean(value));
					break;
				case "rewardBasedOnXp":
					ac.setRewardBasedOnXp(Util.toBoolean(value));
					break;
				case "giveRewards":
					ac.setGiveRewards(Util.toBoolean(value));
					break;
				default:
					err("Unsupported option: &c{0}", option);
					break;
			}

			ac.save();
			ac.reload();

			sendpMessage("&3You have set \"&e{0}&3\" to \"&e{1}&3\" for type &e{2}", option, value, ac.getType());
		}
		else
		{
			err("Please specify either \"&carena&4\" or \"&cconfig&4\"!");
		}
	}

	static
	{
		options = new HashMap<String, Type>();
		options.put("gameTime", Integer.TYPE);
		options.put("lobbyTime", Integer.TYPE);
		options.put("maxDeaths", Integer.TYPE);
		options.put("maxWave", Integer.TYPE);
		options.put("cashReward", Integer.TYPE);
		options.put("maxPoints", Integer.TYPE);
		options.put("allowTeamKilling", Boolean.TYPE);
		options.put("countMobKills", Boolean.TYPE);
		options.put("rewardBasedOnXp", Boolean.TYPE);
		options.put("giveRewards", Boolean.TYPE);
		// TODO Add more options
	}
}
