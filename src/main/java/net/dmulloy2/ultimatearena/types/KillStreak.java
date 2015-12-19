/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2012 - 2015 MineSworn
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.ultimatearena.types;

import lombok.Getter;
import net.dmulloy2.ultimatearena.arenas.Arena;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

/**
 * @author dmulloy2
 */

@Getter
public final class KillStreak
{
	public static enum Type
	{
		ITEM, MOB;
	}

	private final int kills;
	private final String message;
	private final Type type;

	// Mob Stuff
	private EntityType mobType;
	private int mobAmount;

	// Item
	private ItemStack item;

	// Base constructor
	private KillStreak(int kills, String message, Type type)
	{
		Validate.notNull(message, "message cannot be null!");
		Validate.notNull(type, "type cannot be null!");

		this.kills = kills;
		this.message = message;
		this.type = type;
	}

	// Mob Constructor
	public KillStreak(int kills, String message, EntityType mobType, int mobAmount)
	{
		this(kills, message, Type.MOB);

		Validate.notNull(mobType, "mobType cannot be null!");
		this.mobType = mobType;
		this.mobAmount = mobAmount;
	}

	// Item Constructor
	public KillStreak(int kills, String message, ItemStack item)
	{
		this(kills, message, Type.ITEM);

		Validate.notNull(item, "item cannot be null!");
		this.item = item;
	}

	/**
	 * Performs this KillStreak for an {@link ArenaPlayer}.
	 *
	 * @param ap Player to perform this streak for
	 */
	public final void perform(ArenaPlayer ap)
	{
		if (type == Type.ITEM)
		{
			ap.giveItem(item.clone());
			ap.sendMessage(message);
		}
		else if (type == Type.MOB)
		{
			MetadataValue meta = ap.getPlugin().getUAIdentifier();

			World world = ap.getPlayer().getWorld();
			Location location = ap.getPlayer().getLocation();

			LivingEntity target = getTarget(ap);
			for (int i = 0; i < mobAmount; i++)
			{
				Entity entity = world.spawnEntity(location, mobType);
				if (entity instanceof Tameable)
				{
					Tameable tame = (Tameable) entity;
					tame.setTamed(true);
					tame.setOwner(ap.getPlayer());

					// Used in the entity listener
					entity.setMetadata("ua_attack_dog_" + ap.getName(), meta);
				}

				if (target != null && entity instanceof Monster)
				{
					((Monster) entity).setTarget(target);
				}
			}

			ap.sendMessage(message);
		}
		else
		{
			throw new UnsupportedOperationException("Type " + type);
		}
	}

	private final LivingEntity getTarget(ArenaPlayer ap)
	{
		Arena arena = ap.getArena();
		for (ArenaPlayer target : arena.getLeaderboard())
		{
			if (arena.isAllowTeamKilling() || target.getTeam() != ap.getTeam())
				return target.getPlayer();
		}

		return null;
	}
}
