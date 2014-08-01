/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.mob;

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

public class MobType extends ArenaType
{
	@Override
	public ArenaCreator newCreator(Player player, String name)
	{
		return new MobCreator(player, name, this);
	}

	@Override
	public Arena newArena(ArenaZone az)
	{
		return new MobArena(az);
	}

	@Override
	public ArenaConfig newConfig()
	{
		String name = getName().toLowerCase();
		return new MobConfig(getPlugin(), name, new File(getDataFolder(), "config.yml"));
	}

	@Override
	public ArenaConfig newConfig(ArenaZone az)
	{
		return new MobConfig(az);
	}

	protected ArenaDescription description;

	@Override
	public ArenaDescription getDescription()
	{
		if (description == null)
			description = new MobDescription();

		return description;
	}

	public class MobDescription extends ArenaDescription
	{
		public MobDescription()
		{
			this.name = "mob";
			this.main = "net.dmulloy2.arenas.mob.MobType";
			this.stylized = "Mob";
			this.version = "1.0";
			this.author = "dmulloy2";
		}
	}
}