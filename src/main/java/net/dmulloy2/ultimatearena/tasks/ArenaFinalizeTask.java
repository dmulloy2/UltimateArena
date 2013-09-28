package net.dmulloy2.ultimatearena.tasks;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;

import org.apache.commons.lang.WordUtils;
import org.bukkit.scheduler.BukkitRunnable;

public final class ArenaFinalizeTask extends BukkitRunnable
{
	private final Arena arena;
	private final UltimateArena plugin;
	public ArenaFinalizeTask(Arena arena)
	{
		this.arena = arena;
		this.plugin = arena.getPlugin();
	}

	@Override
	public void run()
	{
		plugin.getActiveArenas().remove(arena);

		plugin.broadcast("&e{0} &3arena has concluded!", WordUtils.capitalize(arena.getName()));

		arena.finalize();
	}
}