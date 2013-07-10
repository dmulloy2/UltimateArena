package net.dmulloy2.ultimatearena.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class UltimateArenaRewardEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private final Player player;
	private List<ItemStack> rewards;
	
	/**
	 * Called when a player is rewarded
	 * @param arenaPlayer - The arena player to be rewarded
	 * @param rewards - The rewards to be given
	 */
	public UltimateArenaRewardEvent(final Player player, List<ItemStack> rewards)
	{
		this.player = player;
		this.rewards = rewards;
	}
	
	/**
	 * Get the player who was rewarded
	 * @return Player who was rewarded
	 */
	public final Player getPlayer()
	{
		return player;
	}
	
	/**
	 * Get the rewards
	 * @return Rewards
	 */
	public final List<ItemStack> getRewards()
	{
		return rewards;
	}
	
	/**
	 * Add a reward
	 * @param stack - ItemStack to add
	 */
	public void addReward(ItemStack stack)
	{
		rewards.add(stack);
	}
	
	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) 
	{
		this.cancelled = cancelled;
	}
}