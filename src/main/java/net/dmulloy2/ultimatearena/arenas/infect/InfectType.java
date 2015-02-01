/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.infect;

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

public class InfectType extends ArenaType
{
	@Override
	public ArenaCreator newCreator(Player player, String name)
	{
		return new InfectCreator(player, name, this);
	}

	@Override
	public Arena newArena(ArenaZone az)
	{
		return new InfectArena(az);
	}

	@Override
	public ArenaConfig newConfig()
	{
		String name = getName().toLowerCase();
		return new InfectConfig(getPlugin(), name, new File(getDataFolder(), "config.yml"));
	}

	@Override
	public ArenaConfig newConfig(ArenaZone az)
	{
		return new InfectConfig(az);
	}

	protected ArenaDescription description;

	@Override
	public ArenaDescription getDescription()
	{
		if (description == null)
			description = new InfectDescription();

		return description;
	}

	public class InfectDescription extends ArenaDescription
	{
		public InfectDescription()
		{
			this.name = "infect";
			this.main = "net.dmulloy2.arenas.infect.InfectType";
			this.stylized = "Infect";
			this.version = "1.0";
			this.author = "dmulloy2";
		}
	}
}