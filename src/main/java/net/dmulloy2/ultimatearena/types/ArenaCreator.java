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

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.types.StringJoiner;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public abstract class ArenaCreator
{
	protected int steps;
	protected String name;
	protected int stepNumber;
	protected ArenaZone target;

	protected final ArenaType type;
	protected final String playerName;
	protected final UltimateArena plugin;

	public ArenaCreator(Player player, String name, ArenaType type)
	{
		Validate.notNull(player, "player cannot be null!");
		Validate.notNull(name, "name cannot be null!");
		Validate.notNull(type, "type cannot be null!");

		this.playerName = player.getName();
		this.name = name;
		this.type = type;
		this.plugin = type.getPlugin();
		this.stepNumber = 1;
		this.start();
	}

	/**
	 * Starts the creation of the arena.
	 */
	public final void start()
	{
		this.initializeArena();
		this.setSteps();
		this.stepNumber = 1;
		this.stepInfo();
	}

	/**
	 * Initializes the arena.
	 *
	 * @throws IllegalStateException if the arena is already initialized
	 */
	public final void initializeArena()
	{
		Validate.isTrue(target == null, "Arena already initialized!");
		target = createArenaZone();
		target.setName(name);
	}

	/**
	 * Creates a new ArenaZone for this creator.
	 * <p>
	 * This <b>must</b> be overriden when custom ArenaZones are being used.
	 *
	 * @return The ArenaZone
	 */
	protected ArenaZone createArenaZone()
	{
		return new ArenaZone(type);
	}

	/**
	 * Sets a point in the Arena Creation process. This method should always
	 * provide some sort of response to the player.
	 * <p>
	 * Generally, you should return when an error is made or more points are
	 * needed in that particular step. We will increment the step if no errors
	 * occur
	 *
	 * @param args Command line arguments, only necessary in a few steps
	 */
	public abstract void setPoint(String[] args);

	/**
	 * Moves the creator to the next step in the creation process.
	 */
	public final void stepUp()
	{
		stepNumber++;
		if (stepNumber > steps)
		{
			complete();
			return;
		}

		stepInfo();
	}

	/**
	 * Gives the player instructions for the next step in the creation process.
	 */
	public abstract void stepInfo();

	/**
	 * Undoes the last step in the creation process.
	 */
	public final void undo()
	{
		if (stepNumber == 1)
		{
			sendMessage("&cYou cannot undo a nonexistant step!");
			return;
		}

		stepNumber--;
		stepInfo();
	}

	/**
	 * Completes the creation process.
	 */
	public final void complete()
	{
		// Set the world
		target.setWorld(target.getArena1().getWorld());

		// Save the arena
		target.saveToDisk();

		// Attempt to initialize
		if (target.initialize())
		{
			// Yay, it worked!
			sendMessage("&3Creation of Arena &e{0} &3complete!", name);
			sendMessage("&3Use &e/ua join {0} &3to play!", name);

			plugin.log("Successfully loaded and created arena {0}", name);
		}
		else
		{
			// Shouldn't happen...
			sendMessage("&cCreation of Arena {0} failed. Check console!", name);
		}

		plugin.getMakingArena().remove(this);
	}

	/**
	 * Sets the number of steps in the creation process.
	 */
	public abstract void setSteps();

	/**
	 * Gets the name of the arena being created.
	 *
	 * @return The arena name
	 */
	public final String getArenaName()
	{
		return name;
	}

	/**
	 * Gets the player creating the arena.
	 *
	 * @return Player creating the arena
	 */
	public final Player getPlayer()
	{
		return Util.matchPlayer(playerName);
	}

	/**
	 * Sends the creator a message.
	 *
	 * @param string Base message
	 * @param objects Objects to format in
	 */
	protected final void sendMessage(String string, Object... objects)
	{
		getPlayer().sendMessage(plugin.getPrefix() + FormatUtil.format(string, objects));
	}

	protected final void checkOverlap(Location loc1, Location loc2)
	{
		List<String> overlap = new ArrayList<>();
		for (ArenaZone az : plugin.getLoadedArenas())
		{
			if (az.isInside(loc1) || az.isInside(loc2))
				overlap.add(az.getName());
		}

		if (! overlap.isEmpty())
		{
			if (overlap.size() == 1)
				sendMessage("&4These points overlap an existing arena: &c{0}&4!", overlap.get(0));
			else
				sendMessage("&4These points overlap existing arenas: &c{0}&4!", new StringJoiner("&4, &c").appendAll(overlap));
			sendMessage("&4This is known to cause some errors! Type &c/ua undo &4to undo!");
		}
	}

	// ---- Generic Methods

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ArenaCreator)
		{
			ArenaCreator that = (ArenaCreator) obj;
			return that.target.equals(target);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		int hash = 37;
		hash *= target.hashCode();
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return "ArenaCreator { name = " + name + ", player = " + playerName + ", type = " + type.getName() + " }";
	}
}
