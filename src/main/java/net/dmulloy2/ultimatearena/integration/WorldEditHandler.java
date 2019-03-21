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
package net.dmulloy2.ultimatearena.integration;

import java.util.logging.Level;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

import net.dmulloy2.integration.DependencyProvider;
import net.dmulloy2.ultimatearena.Config;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.Tuple;
import net.dmulloy2.util.Util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Handles WorldEdit selections and such
 *
 * @author dmulloy2
 */

public final class WorldEditHandler extends DependencyProvider<WorldEditPlugin>
{
	public WorldEditHandler(UltimateArena plugin)
	{
		super(plugin, "WorldEdit", 7);
	}

	public boolean isEnabled()
	{
		return Config.useWorldEdit && super.isEnabled();
	}

	/**
	 * Whether or not a given player has a cuboid selection.
	 *
	 * @param player {@link Player} to check
	 */
	public boolean hasCuboidSelection(Player player)
	{
		if (! isEnabled())
			return false;

		try
		{
			LocalSession session = getDependency().getSession(player);
			Region region = session.getSelection(session.getSelectionWorld());
			return region instanceof CuboidRegion;
		}
		catch (Throwable ex)
		{
			handler.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "hasSelection(" + player.getName() + ")"));
			return false;
		}
	}

	private Location blockVectorToLocation(BlockVector3 vector, World world)
	{
		return new Location(world, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
	}

	/**
	 * Gets a given player's world edit region/selection
	 *
	 * @param player {@link Player} to get selection for
	 */
	public Tuple<Location, Location> getSelection(Player player)
	{
		if (! isEnabled())
			return null;

		try
		{
			LocalSession session = getDependency().getSession(player);
			Region region = session.getSelection(session.getSelectionWorld());
			World world = player.getWorld();
			BlockVector3 max = region.getMaximumPoint(), min = region.getMinimumPoint();
			return new Tuple<>(blockVectorToLocation(max, world), blockVectorToLocation(min, world));
		}
		catch (Throwable ex)
		{
			handler.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "getSelection(" + player.getName() + ")"));
			return null;
		}
	}
}
