package net.dmulloy2.ultimatearena.types;

import lombok.Delegate;

import org.bukkit.entity.Player;

/**
 * Used for {@link Player} methods
 * 
 * @author dmulloy2
*/

public class PlayerExtension
{
	@Delegate(types = { Player.class })
	private Player base;

	public PlayerExtension(Player base)
	{
		this.base = base;
	}
}