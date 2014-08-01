/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.koth;

import java.io.File;

import net.dmulloy2.ultimatearena.api.ArenaDescription;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaCreator;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class KOTHType extends ArenaType
{
	@Override
	public ArenaCreator newCreator(Player player, String name)
	{
		return new KOTHCreator(player, name, this);
	}

	@Override
	public Arena newArena(ArenaZone az)
	{
		return new KOTHArena(az);
	}

	@Override
	public ArenaConfig newConfig()
	{
		String name = getName().toLowerCase();
		return new KOTHConfig(getPlugin(), name, new File(getDataFolder(), "config.yml"));
	}

	@Override
	public ArenaConfig newConfig(ArenaZone az)
	{
		return new KOTHConfig(az);
	}

	protected ArenaDescription description;

	@Override
	public ArenaDescription getDescription()
	{
		if (description == null)
			description = new KOTHDescription();

		return description;
	}

	public class KOTHDescription extends ArenaDescription
	{
		public KOTHDescription()
		{
			this.name = "koth";
			this.main = "net.dmulloy2.arenas.koth.KOTHType";
			this.stylized = "KOTH";
			this.version = "1.0";
			this.author = "dmulloy2";
		}
	}
}