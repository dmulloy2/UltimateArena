/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.ffa;

import net.dmulloy2.ultimatearena.api.ArenaDescription;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaCreator;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class FFAType extends ArenaType
{
	@Override
	public ArenaCreator newCreator(Player player, String name)
	{
		return new FFACreator(player, name, this);
	}

	@Override
	public Arena newArena(ArenaZone az)
	{
		return new FFAArena(az);
	}

	protected ArenaDescription description;

	@Override
	public ArenaDescription getDescription()
	{
		if (description == null)
			description = new FFADescription();

		return description;
	}

	public class FFADescription extends ArenaDescription
	{
		public FFADescription()
		{
			this.name = "ffa";
			this.main = "net.dmulloy2.arenas.ffa.FFAType";
			this.stylized = "FFA";
			this.version = "1.0";
			this.author = "dmulloy2";
		}
	}
}