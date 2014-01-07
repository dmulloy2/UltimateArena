package net.dmulloy2.ultimatearena.types;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.InventoryHelper;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

/**
 * Represents a player inside an {@link Arena}.
 * <p>
 * Every player who has joined this arena will have an ArenaPlayer instance. It
 * is important to note, however, that players who are out will still have arena
 * player instances until the arena concludes. Use
 * {@link Arena#checkValid(ArenaPlayer)} to make sure the player is actually in
 * the arena.
 * 
 * @author dmulloy2
 */

@Getter
@Setter
public class ArenaPlayer extends PlayerExtension
{
	private int kills;
	private int deaths;
	private int killStreak;
	private int gameXP;
	private int team = 1;
	private int points;
	private int baseLevel;
	private int amtKicked;
	private int healTimer;

	private boolean out;
	private boolean canReward;
	private boolean changeClassOnRespawn;

	private Player player;

	private String name;

	private Arena arena;
	private ArenaClass arenaClass;
	private Location spawnBack;

	private final UltimateArena plugin;

	private ItemStack[] inventoryContents;
	private ItemStack[] armorContents;

	/**
	 * Creates a new ArenaPlayer instance
	 * 
	 * @param player
	 *            - Base {@link Player} to create the arena player around
	 * @param arena
	 *            - {@link Arena} the player is in
	 * @param plugin
	 *            - {@link UltimateArena} plugin instance
	 */
	public ArenaPlayer(Player player, Arena arena, UltimateArena plugin)
	{
		this.player = player;
		this.name = player.getName();
		this.spawnBack = player.getLocation();

		this.arena = arena;
		this.plugin = plugin;
		this.arenaClass = plugin.getArenaClass(arena.getAz().getDefaultClass());
	}

	/**
	 * Decides the player's hat
	 */
	public final void decideHat()
	{
		if (arenaClass != null && ! arenaClass.isUsesHelmet())
		{
			player.getInventory().setHelmet(null);
			return;
		}

		if (player.getInventory().getHelmet() == null)
		{
			ItemStack itemStack = new ItemStack(Material.LEATHER_HELMET);
			LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
			Color teamColor = Color.RED;
			if (team == 2) teamColor = Color.BLUE;
			meta.setColor(teamColor);
			itemStack.setItemMeta(meta);
			player.getInventory().setHelmet(itemStack);
		}
	}

	/**
	 * Gives the player an item
	 * 
	 * @param stack
	 *            - {@link ItemStack} to give the player
	 */
	public final void giveItem(ItemStack stack)
	{
		InventoryHelper.addItem(player.getPlayer(), stack);
	}

	/**
	 * Gives the player armor
	 * 
	 * @param slot
	 *            - Armor slot to put. Must be between 0 and 3
	 * @param stack
	 *            - {@link ItemStack} to give as armor
	 */
	public final void giveArmor(int slot, ItemStack stack)
	{
		if (stack != null)
		{
			if (slot == 0)
			{
				player.getInventory().setChestplate(stack);
			}
			if (slot == 1)
			{
				player.getInventory().setLeggings(stack);
			}
			if (slot == 2)
			{
				player.getInventory().setBoots(stack);
			}
		}
	}

	/**
	 * Saves the player's inventory
	 */
	public final void saveInventory()
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
	public final void clearInventory()
	{
		// Close any open inventories
		player.closeInventory();

		// Clear their inventory
		PlayerInventory inv = player.getInventory();

		inv.setHelmet(null);
		inv.setChestplate(null);
		inv.setLeggings(null);
		inv.setBoots(null);
		inv.clear();
	}

	/**
	 * Returns the player's inventory
	 */
	public final void returnInventory()
	{
		if (plugin.getConfig().getBoolean("saveInventories", true))
		{
			player.getInventory().setContents(inventoryContents);
			player.getInventory().setArmorContents(armorContents);
		}
	}

	/**
	 * Readies the player for spawning
	 */
	public final void spawn()
	{
		if (amtKicked > 10)
		{
			leaveArena(LeaveReason.KICK);
			return;
		}

		clearInventory();
		clearPotionEffects();

		giveClassItems();
	}

	/**
	 * Sets a player's class
	 * 
	 * @param ac
	 *            - {@link ArenaClass} to set the player's class to
	 * 
	 * @return Whether or not the operation was successful
	 */
	public final boolean setClass(ArenaClass ac)
	{
		if (arena.isValidClass(ac))
		{
			this.arenaClass = ac;

			this.changeClassOnRespawn = true;

			clearPotionEffects();
			return true;
		}

		return false;
	}

	/**
	 * Gives the player their class items
	 */
	public final void giveClassItems()
	{
		if (! arena.isInGame())
			return;

		decideHat();

		if (arenaClass == null)
		{
			giveArmor(0, new ItemStack(Material.IRON_CHESTPLATE));
			giveArmor(1, new ItemStack(Material.IRON_LEGGINGS));
			giveArmor(2, new ItemStack(Material.IRON_BOOTS));
			giveItem(new ItemStack(Material.DIAMOND_SWORD));
			return;
		}

		if (arenaClass.isUsesEssentials() && plugin.getEssentialsHandler().useEssentials())
		{
			plugin.getEssentialsHandler().giveKitItems(this);
		}

		for (int i = 0; i < arenaClass.getArmor().size(); i++)
		{
			ItemStack stack = arenaClass.getArmor(i);
			if (stack != null)
				giveArmor(i, stack);
		}

		for (ItemStack weapon : arenaClass.getWeapons())
		{
			if (weapon != null)
				giveItem(weapon);
		}

		this.changeClassOnRespawn = false;
	}

	/**
	 * Clears a player's potion effects
	 */
	public final void clearPotionEffects()
	{
		for (PotionEffect effect : player.getActivePotionEffects())
		{
			player.removePotionEffect(effect.getType());
		}
	}

	/**
	 * Sends the player a message
	 * 
	 * @param string
	 *            - Base message
	 * @param objects
	 *            - Objects to format in
	 */
	public final void sendMessage(String string, Object... objects)
	{
		sendMessage(plugin.getPrefix() + FormatUtil.format(string, objects));
	}

	/**
	 * Gives the player xp
	 * 
	 * @param xp
	 *            - XP to give the player
	 */
	public final void addXP(int xp)
	{
		this.gameXP += xp;
	}

	/**
	 * Subtracts xp from the player
	 * 
	 * @param xp
	 *            - XP to subtract
	 */
	public final void subtractXP(int xp)
	{
		this.gameXP -= xp;
	}

	/**
	 * Gets a player's KDR (Kill-Death Ratio)
	 * 
	 * @return KDR
	 */
	public final double getKDR()
	{
		double k = (double) kills;
		if (deaths == 0)
			return k;

		double d = (double) deaths;
		return k / d;
	}

	private long deathTime;

	/**
	 * Returns whether or not the player is dead
	 * 
	 * @return Whether or not the player is dead
	 */
	public final boolean isDead()
	{
		return (System.currentTimeMillis() - deathTime) < 60L;
	}

	/**
	 * Handles the player's death
	 */
	public final void onDeath()
	{
		this.deathTime = System.currentTimeMillis();
		this.killStreak = 0;
		this.deaths++;

		arena.onPlayerDeath(this);
	}

	/**
	 * Makes the player leave their {@link Arena}
	 * 
	 * @param reason
	 *            - Reason the player is leaving
	 */
	public final void leaveArena(LeaveReason reason)
	{
		switch (reason)
		{
			case COMMAND:
				arena.endPlayer(this, false);

				sendMessage("&3You have left the arena!");

				arena.tellPlayers("&e{0} &3has left the arena!", name);
				break;
			case DEATHS:
				arena.endPlayer(this, true);
				break;
			case KICK:
				arena.endPlayer(this, false);

				sendMessage("&cYou have been kicked from the arena!");

				arena.tellPlayers("&e{0} &3has been kicked from the arena!", name);
				break;
			case QUIT:
				arena.endPlayer(this, false);

				arena.tellPlayers("&e{0} &3has left the arena!", name);
				break;
			default:
				arena.endPlayer(this, false);
				break;
		}
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
		teleport(location.clone().add(0.5D, 1.0D, 0.5D));
	}

	public final void teleport(ArenaLocation location)
	{
		 teleport(location.getLocation());
	}
}