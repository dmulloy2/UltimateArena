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
	private boolean useWorldEdit;

	private final UltimateArena plugin;
	public WorldEditHandler(UltimateArena plugin)
	{
		this.plugin = plugin;
	}

	/**
	 * Whether or not to use WorldEdit for arena creation
	 */
	public final boolean useWorldEdit()
	{
		return useWorldEdit;
	}

	/**
	 * Sets whether or not to use WorldEdit for arena creation
	 * 
	 * @param useWorldEdit
	 *        - whether or not to use WorldEdit for arena creation
	 */
	public final void setUseWorldEdit(boolean useWorldEdit)
	{
		this.useWorldEdit = useWorldEdit;
	}

	/**
	 * Whether or not a given player has a selection
	 * 
	 * @param player
	 *        - {@link Player} to check
	 */
	public final boolean hasSelection(Player player)
	{
		return getSelection(player) != null;
	}

	/**
	 * Gets a given player's {@link Selection}
	 * 
	 * @param player
	 *        - {@link Player} to get selection for
	 */
	public final Selection getSelection(Player player)
	{
		return plugin.getWorldEdit().getSelection(player);
	}

	/**
	 * Whether or not a given selection is a Cubiod Selection
	 * 
	 * @param sel
	 *        - {@link Selection} to check
	 */
	public final boolean isCuboidSelection(Selection sel)
	{
		return sel.getRegionSelector() instanceof CuboidRegionSelector;
	}
}