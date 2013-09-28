package net.dmulloy2.ultimatearena.tasks;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public final class EntityClearTask extends BukkitRunnable
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
		for (Entity entity : world.getEntities())
		{
			if (entity instanceof Player)
				continue;
			
			if (plugin.isInArena(entity))
			{
				if (plugin.getArenaInside(entity) == arena)
				{
					entity.remove();
				}
			}
		}
	}
}