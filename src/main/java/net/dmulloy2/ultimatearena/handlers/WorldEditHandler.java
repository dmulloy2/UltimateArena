package net.dmulloy2.ultimatearena.handlers;

import net.dmulloy2.ultimatearena.UltimateArena;

import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegionSelector;

/**
 * Handles WorldEdit selections and such
 * 
 * @author dmulloy2
 */

public class WorldEditHandler
{
	private final UltimateArena plugin;
	public WorldEditHandler(UltimateArena plugin)
	{
		this.plugin = plugin;
	}

	public boolean useWorldEdit()
	{
		return plugin.getWorldEdit() != null;
	}

	public boolean hasSelection(Player player)
	{
		return getSelection(player) != null;
	}

	public Selection getSelection(Player player)
	{
		return plugin.getWorldEdit().getSelection(player);
	}

	public boolean isCuboidSelection(Selection sel)
	{
		return sel.getRegionSelector() instanceof CuboidRegionSelector;
	}
}