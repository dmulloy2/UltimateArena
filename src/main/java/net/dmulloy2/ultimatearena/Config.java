/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.config.ConfigParser;
import net.dmulloy2.config.Key;
import net.dmulloy2.config.ValueOptions;
import net.dmulloy2.config.ValueOptions.ValueOption;
import net.dmulloy2.util.ListUtil;

/**
 * @author dmulloy2
 */

public class Config
{
	public static void load(SwornPlugin plugin)
	{
		ConfigParser.parse(plugin, Config.class);
	}

	@Key("moneyRewards")
	public static boolean moneyRewards = true;

	@Key("joinTimer.enabled")
	public static boolean joinTimerEnabled = true;

	@Key("joinTimer.wait")
	public static int joinTimerWait = 3;

	@Key("saveInventories")
	public static boolean saveInventories = true;

	@Key("timerXPBar")
	public static boolean timerXPBar = true;

	@Key("classSelector.title")
	@ValueOptions(ValueOption.FORMAT)
	public static String classSelectorTitle = "         &l&nSelect a class!&r";

	@Key("classSelector.automatic")
	public static boolean classSelectorAutomatic = true;

	@Key("restrictCommands")
	public static boolean restrictCommands = true;

	@Key("whitelistedCommands")
	public static List<String> whitelistedCommands = new ArrayList<>();

	@Key("globalMessages")
	public static boolean globalMessages = true;

	@Key("forceRespawn")
	public static boolean forceRespawn = false;

	@Key("showUnavailableClasses")
	public static boolean showUnavailableClasses = false;

	@Key("scoreboard.enabled")
	public static boolean scoreboardEnabled = true;

	@Key("spectator.flight")
	public static boolean spectatorFlight = true;

	@Key("spectator.invisible")
	public static boolean spectatorInvisible = true;

	@Key("persistentEntities")
	@ValueOptions(ValueOption.LIST_UPPER_CASE)
	public static List<String> persistentEntities = ListUtil.toList(
			"PAINTING", "ITEM_FRAME", "VILLAGER"
	);

	@Key("integration.useWorldEdit")
	public static boolean useWorldEdit = true;

	@Key("debug")
	public static boolean debug = false;

	@Key("ignoreOverlap")
	public static boolean ignoreOverlap = false;
}