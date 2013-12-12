package net.dmulloy2.ultimatearena.creation;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.ultimatearena.util.FormatUtil;

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
	protected final UltimateArena plugin;

	public ArenaCreator(Player player, String name, UltimateArena plugin)
	{
		this.player = player;
		this.name = name;
		this.plugin = plugin;
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
	 * @throws IllegalStateException If the arena is already initialized
	 */
	public final void initializeArena()
	{
		if (target != null)
		{
			throw new IllegalStateException("Arena already initialized");
		}

		target = new ArenaZone(plugin);
		target.setType(getType());
		target.setArenaName(name);
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
	 * Un-does the last step in the creation process
	 */
	public abstract void undo();

	/**
	 * Completes the Arena Creation process
	 */
	public final void complete()
	{
		// Set the world
		target.setWorld(target.getArena1().getWorld());

		// Save the arena
		target.save();

		// Attempt to initialize
		if (target.initialize())
		{
			// Yay, it worked!
			sendMessage("&3Creation of Arena &e{0} &3complete!", name);
		}
		else
		{
			// Shouldn't happen...
			sendMessage("&cCreation of Arena {0} failed. Check console!", name);
		}

		plugin.getMakingArena().remove(this);
	}

	/**
	 * Gets the {@link FieldType} of this particular creator
	 * 
	 * @return The {@link FieldType} of this particular creator
	 */
	public abstract FieldType getType();

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
}