package net.dmulloy2.ultimatearena.arenas.infect;

import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.arenas.pvp.PvPCreator;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class InfectCreator extends PvPCreator
{
	public InfectCreator(Player player, String name, ArenaType type)
	{
		super(player, name, type);
	}
}