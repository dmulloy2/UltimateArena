package net.dmulloy2.ultimatearena.events;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.FieldType;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class UltimateArenaKillEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	private final ArenaPlayer killer;
	private final ArenaPlayer killed;
	private final Arena arena;

	/**
	 * Called when a player kills another player
	 * 
	 * @param killer
	 *            - The ArenaPlayer that killed
	 * @param killed
	 *            - The ArenaPlayer that was killed
	 * @param arena
	 *            - The Arena the player joined
	 */
	public UltimateArenaKillEvent(final ArenaPlayer killed, final ArenaPlayer killer, final Arena arena)
	{
		this.killed = killed;
		this.killer = killer;
		this.arena = arena;
	}

	/**
	 * Get the ArenaPlayer instance of the killer
	 * 
	 * @return ArenaPlayer instance of the killer
	 */
	public ArenaPlayer getKiller()
	{
		return killer;
	}

	/**
	 * Get the ArenaPlayer instance of the dead
	 * 
	 * @return ArenaPlayer instance of the dead
	 */
	public ArenaPlayer getKilled()
	{
		return killed;
	}

	/**
	 * Get the arena the event occured in
	 * 
	 * @return Arena the event occured in
	 */
	public Arena getArena()
	{
		return arena;
	}

	/**
	 * Get the type of the arena the event occured in
	 * 
	 * @return Type of the arena the event occured in
	 */
	public FieldType getArenaType()
	{
		return arena.getType();
	}

	/**
	 * Get the Player instance of the killer
	 * 
	 * @return Player instance of the killer
	 */
	public Player getKillerPlayer()
	{
		return killer.getPlayer();
	}

	/**
	 * Get the Player instance of the dead
	 * 
	 * @return Player instance of the dead
	 */
	public Player getKilledPlayer()
	{
		return killed.getPlayer();
	}

	/**
	 * Get the weapon used to kill
	 * 
	 * @return Weapon used to kill
	 */
	public ItemStack getWeapon()
	{
		return killer.getPlayer().getItemInHand();
	}

	/**
	 * Get the hearts the killer had left
	 * 
	 * @return Hearts the killer had left
	 */
	public int getHeartsLeft()
	{
		return (int) (killer.getPlayer().getHealth() / 2);
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
}