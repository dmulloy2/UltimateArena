package net.dmulloy2.ultimatearena.types;

import lombok.Data;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.util.FormatUtil;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

/**
 * Represents a player who is spectating.
 * 
 * @author dmulloy2
 */

@Data
public class ArenaSpectator
{
	private int baseLevel;

	private Player player;

	private String name;

	private Arena arena;
	private Location spawnBack;

	private final UltimateArena plugin;

	private ItemStack[] inventoryContents;
	private ItemStack[] armorContents;

	/**
	 * Creates a new ArenaSpectator instance
	 * 
	 * @param player
	 *            - Base {@link Player} to create the ArenaSpectator around
	 * @param arena
	 *            - {@link Arena} the player is in
	 * @param plugin
	 *            - {@link UltimateArena} plugin instance
	 */
	public ArenaSpectator(Player player, Arena arena, UltimateArena plugin)
	{
		this.player = player;
		this.name = player.getName();
		this.spawnBack = player.getLocation();

		this.arena = arena;
		this.plugin = plugin;
	}
	
	/**
	 * Spawns the player
	 */
	public void spawn()
	{
		arena.teleport(player, arena.getSpawn(arena.getValidPlayers().get(0)));
		
		saveInventory();
		clearInventory();

		baseLevel = player.getLevel();

		player.setGameMode(GameMode.SURVIVAL);

		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.setHealth(20);

		player.setAllowFlight(true);
		player.setFlySpeed(0.1F);
		player.setFlying(false);
		
		for (ArenaPlayer ap : arena.getValidPlayers())
		{
			ap.getPlayer().hidePlayer(player);
		}

		clearPotionEffects();
	}

	public void endPlayer()
	{
		player.setExp(0.0F);
		player.setLevel(baseLevel);

		clearInventory();
		returnInventory();

		clearPotionEffects();

		for (ArenaPlayer ap : arena.getArenaPlayers())
		{
			ap.getPlayer().showPlayer(player);
		}

		arena.teleport(player, spawnBack);
	}

	/**
	 * Saves the player's inventory
	 */
	public void saveInventory()
	{
		if (plugin.getConfig().getBoolean("saveInventories", true))
		{
			this.inventoryContents = player.getInventory().getContents();
			this.armorContents = player.getInventory().getArmorContents();
		}
	}

	/**
	 * Clears the player's inventory
	 */
	public void clearInventory()
	{
		PlayerInventory inv = player.getInventory();
		
		player.closeInventory();

		inv.setHelmet(null);
		inv.setChestplate(null);
		inv.setLeggings(null);
		inv.setBoots(null);
		inv.clear();
	}

	/**
	 * Returns the player's inventory
	 */
	public void returnInventory()
	{
		if (plugin.getConfig().getBoolean("saveInventories", true))
		{
			player.getInventory().setContents(inventoryContents);
			player.getInventory().setArmorContents(armorContents);
		}
	}

	/**
	 * Clears a player's potion effects
	 */
	public void clearPotionEffects()
	{
		for (PotionEffect effect : player.getActivePotionEffects())
		{
			player.removePotionEffect(effect.getType());
		}
	}

	public void sendMessage(String string, Object... objects)
	{
		player.sendMessage(plugin.getPrefix() + FormatUtil.format(string, objects));
	}
}