/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.conquest;

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

public class ConquestType extends ArenaType
{
	@Override
	public ArenaCreator newCreator(Player player, String name)
	{
		return new ConquestCreator(player, name, this);
	}

	@Override
	public Arena newArena(ArenaZone az)
	{
		return new ConquestArena(az);
	}

	@Override
	public ArenaConfig newConfig()
	{
		String name = getName().toLowerCase();
		return new ConquestConfig(getPlugin(), name, new File(getDataFolder(), "config.yml"));
	}

	@Override
	public ArenaConfig newConfig(ArenaZone az)
	{
		return new ConquestConfig(az);
	}

	private ArenaDescription description;

	@Override
	public ArenaDescription getDescription()
	{
		if (description == null)
			description = new ConquestDescription();

		return description;
	}

	public class ConquestDescription extends ArenaDescription
	{
		public ConquestDescription()
		{
			this.name = "cq";
			this.main = "net.dmulloy2.arenas.conquest.Conquest";
			this.stylized = "Conquest";
			this.version = "1.0";
			this.author = "dmulloy2";
		}
	}
}