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
public class ArenaSpectator extends PlayerExtension
{
	private int baseLevel;

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
	 *            - Base {@link Player} to create the ArenaSpectator around
	 * @param arena
	 *            - {@link Arena} the player is in
	 * @param plugin
	 *            - {@link UltimateArena} plugin instance
	 */
	public ArenaSpectator(Player player, Arena arena, UltimateArena plugin)
	{
		super(player);
		this.name = getName();
		this.spawnBack = getLocation();

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

		baseLevel = getLevel();

		setGameMode(GameMode.SURVIVAL);

		setFoodLevel(20);
		setFireTicks(0);
		setHealth(20);

		setAllowFlight(true);
		setFlySpeed(0.1F);
		setFlying(false);
		
		getInventory().addItem(new ItemStack(Material.COMPASS));
		
		for (ArenaPlayer ap : arena.getActivePlayers())
		{
			ap.hidePlayer(getPlayer());
		}

		clearPotionEffects();

		this.active = true;
	}

	public void endPlayer()
	{
		setExp(0.0F);
		setLevel(baseLevel);
		
		setAllowFlight(false);
		setFlying(false);

		clearInventory();
		returnInventory();

		clearPotionEffects();

		for (ArenaPlayer ap : arena.getActivePlayers())
		{
			ap.showPlayer(getPlayer());
		}

		for (ArenaPlayer ap : arena.getInactivePlayers())
		{
			if (ap != null && ap.getPlayer().isOnline())
			{
				ap.showPlayer(getPlayer());
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
			this.inventoryContents = getInventory().getContents();
			this.armorContents = getInventory().getArmorContents();
		}
	}

	/**
	 * Clears the player's inventory
	 */
	public void clearInventory()
	{
		PlayerInventory inv = getInventory();
		
		closeInventory();

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
			getInventory().setContents(inventoryContents);
			getInventory().setArmorContents(armorContents);
		}
	}

	/**
	 * Clears a player's potion effects
	 */
	public void clearPotionEffects()
	{
		for (PotionEffect effect : getActivePotionEffects())
		{
			removePotionEffect(effect.getType());
		}
	}

	public void sendMessage(String string, Object... objects)
	{
		sendMessage(plugin.getPrefix() + FormatUtil.format(string, objects));
	}

	/**
	 * Teleports the player to a given location. Will attempt to teleport the
	 * player to the center of the block.
	 * 
	 * @param location
	 *        - {@link Location} to teleport the player to
	 */
	@Override
	public final boolean teleport(Location location)
	{
		return super.teleport(location.clone().add(0.5D, 1.0D, 0.5D));
	}
}