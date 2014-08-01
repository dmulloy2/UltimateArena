package net.dmulloy2.ultimatearena.types;

import lombok.NonNull;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.util.FormatUtil;

import org.apache.commons.lang.Validate;
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

	protected final Player player;
	protected final ArenaType type;
	protected final UltimateArena plugin;

	public ArenaCreator(Player player, String name, ArenaType type)
	{
		this.player = player;
		this.name = name;
		this.type = type;
		this.plugin = type.getPlugin();
		this.stepNumber = 1;
		this.start();
	}

	/**
	 * Starts the creation of the arena
	 */
	public final void start()
	{
		this.initializeArena();
		this.setSteps();
		this.stepNumber = 1;
		this.stepInfo();
	}

	/**
	 * Initializes the arena
	 *
	 * @throws IllegalStateException
	 *         If the arena is already initialized
	 */
	public final void initializeArena()
	{
		Validate.isTrue(target == null, "Arena already initialized!");
		target = getArenaZone();
		target.setName(name);
	}

	/**
	 * Gets a new ArenaZone for this creator.
	 *
	 * @param plugin UltimateArena plugin instance
	 * @return The ArenaZone
	 */
	protected ArenaZone getArenaZone()
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
	 * @param args
	 *        - Command line arguments, only necessary in a few steps
	 */
	public abstract void setPoint(String[] args);

	/**
	 * Moves the creator to the next step in the creation process
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
	 * Gives the player instructions for the next step in the creation process
	 */
	public abstract void stepInfo();

	/**
	 * Undo the last step in the creation process
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
	 * Completes the Arena Creation process
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

			plugin.outConsole("Successfully loaded and created arena {0}", name);
		}
		else
		{
			// Shouldn't happen...
			sendMessage("&cCreation of Arena {0} failed. Check console!", name);
		}

		plugin.getMakingArena().remove(this);
	}

	/**
	 * Sets the number of steps in this
	 */
	public abstract void setSteps();

	/**
	 * @return The name of the arena being created
	 */
	public final String getArenaName()
	{
		return name;
	}

	/**
	 * @return The player creating the arena
	 */
	public final Player getPlayer()
	{
		return player;
	}

	/**
	 * Sends the creator a message
	 *
	 * @param string
	 *        - Base message
	 * @param objects
	 *        - Objects to format in
	 */
	protected final void sendMessage(String string, Object... objects)
	{
		player.sendMessage(plugin.getPrefix() + FormatUtil.format(string, objects));
	}

	// ---- Generic Methods

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(@NonNull Object obj)
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
		return "ArenaCreator { name = " + name + ", player = " + player.getName() + ", type = " + type.getName() + " }";
	}
}