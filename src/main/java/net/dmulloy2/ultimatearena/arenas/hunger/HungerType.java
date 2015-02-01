/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.hunger;

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

public class HungerType extends ArenaType
{
	@Override
	public ArenaCreator newCreator(Player player, String name)
	{
		return new HungerCreator(player, name, this);
	}

	@Override
	public Arena newArena(ArenaZone az)
	{
		return new HungerArena(az);
	}

	@Override
	public ArenaConfig newConfig()
	{
		String name = getName().toLowerCase();
		return new HungerConfig(getPlugin(), name, new File(getDataFolder(), "config.yml"));
	}

	@Override
	public ArenaConfig newConfig(ArenaZone az)
	{
		return new HungerConfig(az);
	}

	protected ArenaDescription description;

	@Override
	public ArenaDescription getDescription()
	{
		if (description == null)
			description = new HungerDescription();

		return description;
	}

	public class HungerDescription extends ArenaDescription
	{
		public HungerDescription()
		{
			this.name = "hunger";
			this.main = "net.dmulloy2.arenas.hunger.HungerType";
			this.stylized = "Hunger";
			this.version = "1.0";
			this.author = "dmulloy2";
		}
	}
}