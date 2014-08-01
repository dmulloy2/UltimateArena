package net.dmulloy2.ultimatearena.arenas.hunger;

import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.arenas.ffa.FFACreator;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class HungerCreator extends FFACreator
{
	public HungerCreator(Player player, String name, ArenaType type)
	{
		super(player, name, type);
	}
}