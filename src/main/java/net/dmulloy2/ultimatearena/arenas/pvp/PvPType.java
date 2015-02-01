/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.pvp;

import net.dmulloy2.ultimatearena.api.ArenaDescription;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaCreator;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class PvPType extends ArenaType
{
	@Override
	public ArenaCreator newCreator(Player player, String name)
	{
		return new PvPCreator(player, name, this);
	}

	@Override
	public Arena newArena(ArenaZone az)
	{
		return new PvPArena(az);
	}

	protected ArenaDescription description;

	@Override
	public ArenaDescription getDescription()
	{
		if (description == null)
			description = new PvPDescription();

		return description;
	}

	public class PvPDescription extends ArenaDescription
	{
		public PvPDescription()
		{
			this.name = "pvp";
			this.main = "net.dmulloy2.arenas.pvp.PvPType";
			this.stylized = "PvP";
			this.version = "1.0";
			this.author = "dmulloy2";
		}
	}
}