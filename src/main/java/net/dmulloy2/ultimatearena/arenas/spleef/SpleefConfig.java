/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.spleef;

import java.io.File;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaZone;

/**
 * @author dmulloy2
 */

public class SpleefConfig extends ArenaConfig
{
	public SpleefConfig(UltimateArena plugin, String type, File file)
	{
		super(plugin, type, file);
	}

	public SpleefConfig(ArenaZone az)
	{
		super(az);
	}

	@Override
	protected void setCustomDefaults()
	{
		this.unlimitedAmmo = false;
		this.rewardBasedOnXp = false;
	}
}