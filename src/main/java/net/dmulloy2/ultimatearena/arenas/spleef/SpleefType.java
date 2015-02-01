/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.spleef;

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

public class SpleefType extends ArenaType
{
	@Override
	public ArenaCreator newCreator(Player player, String name)
	{
		return new SpleefCreator(player, name, this);
	}

	@Override
	public Arena newArena(ArenaZone az)
	{
		return new SpleefArena(az);
	}

	@Override
	public ArenaZone getArenaZone(File file)
	{
		return new SpleefZone(this, file);
	}

	@Override
	public ArenaConfig newConfig()
	{
		String name = getName().toLowerCase();
		return new SpleefConfig(getPlugin(), name, new File(getDataFolder(), "config.yml"));
	}

	@Override
	public ArenaConfig newConfig(ArenaZone az)
	{
		return new SpleefConfig(az);
	}

	protected ArenaDescription description;

	@Override
	public ArenaDescription getDescription()
	{
		if (description == null)
			description = new SpleefDescription();

		return description;
	}

	public class SpleefDescription extends ArenaDescription
	{
		public SpleefDescription()
		{
			this.name = "spleef";
			this.main = "net.dmulloy2.arenas.spleef.SpleefType";
			this.stylized = "Spleef";
			this.version = "1.0";
			this.author = "dmulloy2";
		}
	}
}