/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.hunger;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.NonNull;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.KillStreak;

/**
 * @author dmulloy2
 */

public class HungerConfig extends ArenaConfig
{
	public HungerConfig(@NonNull UltimateArena plugin, @NonNull String type, @NonNull File file)
	{
		super(plugin, type, file);
	}

	public HungerConfig(@NonNull ArenaZone az)
	{
		super(az);
	}

	@Override
	protected void initializeVariables()
	{
		super.initializeVariables();
		this.canModifyWorld = true;
		this.unlimitedAmmo = false;
		this.rewardBasedOnXp = false;
	}

	@Override
	public Map<Integer, List<KillStreak>> getDefaultKillStreak()
	{
		// No kill streaks
		return new LinkedHashMap<>();
	}
}