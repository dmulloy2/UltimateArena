package net.dmulloy2.ultimatearena.types;

import lombok.Getter;
import lombok.NonNull;
import net.dmulloy2.ultimatearena.arenas.Arena;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;

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
	private KillStreak(int kills, @NonNull String message, @NonNull Type type)
	{
		this.kills = kills;
		this.message = message;
		this.type = type;
	}

	// Mob Constructor
	public KillStreak(int kills, @NonNull String message, @NonNull EntityType mobType, int mobAmount)
	{
		this(kills, message, Type.MOB);
		this.mobType = mobType;
		this.mobAmount = mobAmount;
	}

	// Item Constructor
	public KillStreak(int kills, @NonNull String message, @NonNull ItemStack item)
	{
		this(kills, message, Type.ITEM);
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