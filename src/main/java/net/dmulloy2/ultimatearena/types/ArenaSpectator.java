package net.dmulloy2.ultimatearena.types;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.util.FormatUtil;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

/**
 * Represents a player who is spectating.
 * 
 * @author dmulloy2
 */

@Getter
@Setter
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

	private boolean active;

	/**
	 * Creates a new ArenaSpectator instance
	 * 
	 * @param player
	 *        - Base {@link Player} to create the ArenaSpectator around
	 * @param arena
	 *        - {@link Arena} the player is in
	 * @param plugin
	 *        - {@link UltimateArena} plugin instance
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
		teleport(arena.getSpawn(arena.getActivePlayers().get(0)));

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

		player.getInventory().addItem(new ItemStack(Material.COMPASS));

		for (ArenaPlayer ap : arena.getActivePlayers())
		{
			ap.getPlayer().hidePlayer(player);
		}

		clearPotionEffects();

		this.active = true;
	}

	public void endPlayer()
	{
		player.setExp(0.0F);
		player.setLevel(baseLevel);

		player.setAllowFlight(false);
		player.setFlying(false);

		clearInventory();
		returnInventory();

		clearPotionEffects();

		for (ArenaPlayer ap : arena.getActivePlayers())
		{
			ap.getPlayer().showPlayer(player);
		}

		for (ArenaPlayer ap : arena.getInactivePlayers())
		{
			if (ap != null && ap.getPlayer().isOnline())
			{
				ap.getPlayer().showPlayer(player);
			}
		}

		teleport(spawnBack);

		this.active = false;
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

	/**
	 * Teleports the player to a given location. Will attempt to teleport the
	 * player to the center of the block.
	 * 
	 * @param location
	 *        - {@link Location} to teleport the player to
	 */
	public final void teleport(Location location)
	{
		player.teleport(location.clone().add(0.5D, 1.0D, 0.5D));
	}
}