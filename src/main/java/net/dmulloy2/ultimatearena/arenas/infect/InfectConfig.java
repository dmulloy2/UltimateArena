/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.infect;

import java.io.File;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaZone;

/**
 * @author dmulloy2
 */

public class InfectConfig extends ArenaConfig
{
	public InfectConfig(UltimateArena plugin, String type, File file)
	{
		super(plugin, type, file);
	}

	public InfectConfig(ArenaZone az)
	{
		super(az);
	}

	@Override
	protected void setCustomDefaults()
	{
		this.rewardBasedOnXp = false;
	}
}