package net.dmulloy2.ultimatearena.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class EntityClearTask extends BukkitRunnable
{
	private final Arena arena;
	private final World world;
	private final UltimateArena plugin;
	public EntityClearTask(Arena arena)
	{
		this.arena = arena;
		this.world = arena.getWorld();
		this.plugin = arena.getPlugin();
	}

	@Override
	public void run()
	{
		Iterator<Entity> iter = getEntities().iterator();
		while (iter.hasNext())
		{
			Entity next = iter.next();
			if (next instanceof LivingEntity)
				((LivingEntity) next).setHealth(0.0D);

			next.remove();
			iter.remove();
		}
	}

	private final List<EntityType> persistentEntities = Arrays.asList(new EntityType[]
	{
			EntityType.PLAYER, EntityType.PAINTING, EntityType.ITEM_FRAME, EntityType.VILLAGER
	});

	private List<Entity> getEntities()
	{
		List<Entity> ret = new ArrayList<Entity>();

		for (Entity entity : world.getEntities())
		{
			if (entity != null && entity.isValid())
			{
				if (plugin.isInArena(entity))
				{
					if (plugin.getArenaInside(entity).equals(arena))
					{
						if (! persistentEntities.contains(entity.getType()))
							ret.add(entity);
					}
				}
			}
		}

		return ret;
	}
}