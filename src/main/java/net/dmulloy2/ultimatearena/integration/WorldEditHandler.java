package net.dmulloy2.ultimatearena.integration;

import lombok.Getter;
import net.dmulloy2.integration.IntegrationHandler;
import net.dmulloy2.ultimatearena.UltimateArena;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

/**
 * Handles WorldEdit selections and such
 *
 * @author dmulloy2
 */

public class WorldEditHandler extends IntegrationHandler
{
	private @Getter boolean enabled;
	private @Getter WorldEditPlugin worldEdit;

	private final UltimateArena plugin;
	public WorldEditHandler(UltimateArena plugin)
	{
		this.plugin = plugin;
		this.setup();
	}

	@Override
	public final void setup()
	{
		try
		{
			PluginManager pm = plugin.getServer().getPluginManager();
			if (pm.getPlugin("WorldEdit") != null)
			{
				worldEdit = (WorldEditPlugin) pm.getPlugin("WorldEdit");
				enabled = true;

				plugin.getLogHandler().log("Integration with WorldEdit successful!");
			}
		} catch (Throwable ex) { }
	}

	/**
	 * Whether or not a given player has a selection
	 *
	 * @param player - {@link Player} to check
	 */
	public final boolean hasSelection(Player player)
	{
		try
		{
			return getSelection(player) != null;
		} catch (Throwable ex) { }
		return false;
	}

	/**
	 * Gets a given player's {@link Selection}
	 *
	 * @param player - {@link Player} to get selection for
	 */
	public final Selection getSelection(Player player)
	{
		try
		{
			return worldEdit.getSelection(player);
		} catch (Throwable ex) { }
		return null;
	}

	/**
	 * Whether or not a given selection is a Cubiod Selection
	 *
	 * @param sel - {@link Selection} to check
	 */
	public final boolean isCuboidSelection(Selection sel)
	{
		return sel instanceof CuboidSelection;
	}

	public boolean useWorldEdit()
	{
		return enabled;
	}
}