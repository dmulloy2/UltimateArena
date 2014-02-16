package net.dmulloy2.ultimatearena.commands;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Permission;
import net.dmulloy2.ultimatearena.util.NumberUtil;

/**
 * @author dmulloy2
 */

public class CmdOption extends UltimateArenaCommand
{
	private static Map<String, Type> options;
	public CmdOption(UltimateArena plugin)
	{
		super(plugin);
		this.name = "option";
		this.requiredArgs.add("type");
		this.requiredArgs.add("name");
		this.requiredArgs.add("option");
		this.requiredArgs.add("value");
		this.description = "Change an option for an arena/config";
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
				err("Please specify a valid integer!");
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
				err("Please specify a valid arena!");
				return;
			}

			switch (option)
			{
				case "gameTime":
					az.setGameTime(NumberUtil.toInt(value));
					break;
				case "lobbyTime":
					az.setLobbyTime(NumberUtil.toInt(value));
					break;
				case "maxDeaths":
					az.setMaxDeaths(NumberUtil.toInt(value));
					break;
				case "maxWave":
					az.setMaxWave(NumberUtil.toInt(value));
					break;
				case "cashReward":
					az.setCashReward(NumberUtil.toInt(value));
					break;
				case "maxPoints":
					az.setMaxPoints(NumberUtil.toInt(value));
					break;
				case "allowTeamKilling":
					az.setAllowTeamKilling((boolean) value);
					break;
				case "countMobKills":
					az.setCountMobKills((boolean) value);
					break;
				case "rewardBasedOnXp":
					az.setRewardBasedOnXp((boolean) value);
					break;
				case "giveRewards":
					az.setGiveRewards((boolean) value);
					break;
			}

			az.saveConfiguration();
			az.save();
			az.reload();

			sendpMessage("&3You have set \"&e{0}&3\" to \"&e{1}&3\" for arena &e{2}", option, value, az.getArenaName());
		}
		else if (args[0].equalsIgnoreCase("config"))
		{
			ArenaConfig ac = plugin.getConfig(args[0]);
			if (ac == null)
			{
				err("Please specify a valid arena type!");
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
				case "maxWave":
					ac.setMaxWave(NumberUtil.toInt(value));
					break;
				case "cashReward":
					ac.setCashReward(NumberUtil.toInt(value));
					break;
				case "maxPoints":
					ac.setMaxPoints(NumberUtil.toInt(value));
					break;
				case "allowTeamKilling":
					ac.setAllowTeamKilling((boolean) value);
					break;
				case "countMobKills":
					ac.setCountMobKills((boolean) value);
					break;
				case "rewardBasedOnXp":
					ac.setRewardBasedOnXp((boolean) value);
					break;
				case "giveRewards":
					ac.setGiveRewards((boolean) value);
					break;
			}

			ac.save();
			ac.reload();

			sendpMessage("&3You have set \"&e{0}&3\" to \"&e{1}&3\" for type &e{2}", option, value, ac.getArenaName());
		}
		else
		{
			err("Please specify either \"&carena&4\" or \"&cconfig&4\"");
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