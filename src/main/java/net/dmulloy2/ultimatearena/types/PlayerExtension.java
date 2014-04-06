/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.types;

import lombok.Delegate;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.ServerOperator;

/**
 * @author dmulloy2
 */

public class PlayerExtension
{
	@Delegate(types =
	{
		Player.class, Entity.class, CommandSender.class, ServerOperator.class,
		HumanEntity.class, ConfigurationSerializable.class, LivingEntity.class,
		Permissible.class
	})
	protected Player base;

	public PlayerExtension(Player base)
	{
		this.base = base;
	}

	public Player getBase()
	{
		return base;
	}
}