/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.ctf;

import net.dmulloy2.ultimatearena.api.ArenaDescription;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaCreator;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CTFType extends ArenaType
{
	@Override
	public ArenaCreator newCreator(Player player, String name)
	{
		return new CTFCreator(player, name, this);
	}

	@Override
	public Arena newArena(ArenaZone az)
	{
		return new CTFArena(az);
	}

	private ArenaDescription description;

	@Override
	public ArenaDescription getDescription()
	{
		if (description == null)
			description = new CTFDescription();

		return description;
	}

	public class CTFDescription extends ArenaDescription
	{
		public CTFDescription()
		{
			this.name = "ctf";
			this.main = "net.dmulloy2.arenas.ctf.CTFType";
			this.stylized = "CTF";
			this.version = "1.0";
			this.author = "dmulloy2";
		}
	}
}