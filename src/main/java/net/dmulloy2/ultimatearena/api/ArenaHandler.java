package net.dmulloy2.ultimatearena.api;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;

import lombok.NonNull;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.Reloadable;
import net.dmulloy2.ultimatearena.util.Util;

import com.google.common.collect.Maps;

/**
 * @author dmulloy2
 */

public class ArenaHandler implements Reloadable
{
	private final Map<String, ArenaType> arenaTypes;
	private final UltimateArena plugin;

	public ArenaHandler(UltimateArena plugin)
	{
		this.plugin = plugin;
		this.arenaTypes = Maps.newHashMap();
	}

	public final ArenaType getArenaType(@NonNull String name)
	{
		Validate.notEmpty(name, "Name cannot be empty!");

		for (ArenaType type : arenaTypes.values())
		{
			if (type.getName().equalsIgnoreCase(name))
				return type;
		}

		return null;
	}

	public final void loadArenaTypes()
	{
		File directory = new File(plugin.getDataFolder(), "arenas");
		if (! directory.exists())
			directory.mkdirs();

		File[] files = directory.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				return file.getName().endsWith(".jar");
			}
		});

		if (files == null || files.length == 0)
		{
			// TODO Generate default arenas
			return;
		}

		ArenaLoader loader = new ArenaLoader(plugin);

		for (File file : files)
		{
			try
			{
				ArenaType arenaType = loader.loadArenaType(file);
				arenaType.onLoad();

				arenaTypes.put(arenaType.getName(), arenaType);
			}
			catch (Throwable ex)
			{
				plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "loading arena type '" + file.getName() + "'"));
				continue;
			}
		}
	}

	public final void enableArenaTypes()
	{
		for (ArenaType type : arenaTypes.values())
		{
			try
			{
				type.onEnable();
			}
			catch (Throwable ex)
			{
				plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "enabling arena type '" + type.getName() + "'"));
			}
		}
	}

	public final void disableArenaTypes()
	{
		for (ArenaType type : arenaTypes.values())
		{
			try
			{
				type.onDisable();
			}
			catch (Throwable ex)
			{
				plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "disabling arena type '" + type.getName() + "'"));
			}
		}

		arenaTypes.clear();
	}

	@Override
	public void reload()
	{
		for (ArenaType type : arenaTypes.values())
		{
			try
			{
				type.onReload();
			}
			catch (Throwable ex)
			{
				plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "reloading arena type '" + type.getName() + "'"));
			}
		}
	}
}