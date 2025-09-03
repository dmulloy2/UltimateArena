/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Particle;
import org.bukkit.entity.EntityType;

import net.dmulloy2.swornapi.SwornPlugin;
import net.dmulloy2.swornapi.config.*;

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
	@TransformValue(Transform.FORMAT)
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
	@TransformRegistry(KnownRegistry.ENTITY_TYPE)
	public static List<EntityType> persistentEntities = List.of(
		EntityType.PAINTING, EntityType.ITEM_FRAME, EntityType.VILLAGER
	);

	@Key("integration.useWorldEdit")
	public static boolean useWorldEdit = true;

	@Key("debug")
	public static boolean debug = false;

	@Key("fixInvisibility")
	public static boolean fixInvisibility = false;

	@Key("clearEntities")
	public static boolean clearEntities = true;

	@Key("graceParticle")
	@TransformRegistry(KnownRegistry.PARTICLE)
	public static Particle graceParticle = Particle.GLOW;
}
