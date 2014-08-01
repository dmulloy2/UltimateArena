/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.bomb;

import net.dmulloy2.ultimatearena.api.ArenaDescription;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaCreator;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class BombType extends ArenaType
{
	@Override
	public ArenaCreator newCreator(Player player, String name)
	{
		return new BombCreator(player, name, this);
	}

	@Override
	public Arena newArena(ArenaZone az)
	{
		return new BombArena(az);
	}

	protected ArenaDescription description;

	@Override
	public ArenaDescription getDescription()
	{
		if (description == null)
			description = new BombDescription();

		return description;
	}

	public class BombDescription extends ArenaDescription
	{
		public BombDescription()
		{
			this.name = "bomb";
			this.main = "net.dmulloy2.arenas.bomb.BombType";
			this.stylized = "Bomb";
			this.version = "1.0";
			this.author = "dmulloy2";
		}
	}
}