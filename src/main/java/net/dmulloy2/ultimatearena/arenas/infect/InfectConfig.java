/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.infect;

import java.io.File;

import lombok.NonNull;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaZone;

/**
 * @author dmulloy2
 */

public class InfectConfig extends ArenaConfig
{
	public InfectConfig(@NonNull UltimateArena plugin, @NonNull String type, @NonNull File file)
	{
		super(plugin, type, file);
	}

	public InfectConfig(@NonNull ArenaZone az)
	{
		super(az);
	}

	@Override
	protected void setCustomDefaults()
	{
		this.rewardBasedOnXp = false;
	}
}