package net.dmulloy2.ultimatearena.types;

import java.util.List;

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

import com.earth2me.essentials.Kit;
import com.earth2me.essentials.User;

/**
 * Represents a player inside an {@link Arena}.
 * <p>
 * Every player who has joined this arena will have an ArenaPlayer instance.
 * It is important to note, however, that players who are out will still have
 * arena player instances until the arena concludes.
 * Use {@link Arena#checkValid(ArenaPlayer)} to make sure the player is actually in
 * the arena.
 * 
 * @author dmulloy2
 */

@Getter
@Setter
public class ArenaPlayer
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
	public void decideHat()
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
			if (getTeam() == 2)
				teamColor = Color.BLUE;
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
	public void giveItem(ItemStack stack)
	{
		InventoryHelper.addItem(player, stack);
	}

	/**
	 * Gives the player armor
	 * 
	 * @param slot
	 *            - Armor slot to put. Must be between 0 and 3
	 * @param stack
	 *            - {@link ItemStack} to give as armor
	 */
	public void giveArmor(int slot, ItemStack stack)
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
	 * Readies the player for spawning
	 */
	public void spawn()
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
	 */
	public void setClass(ArenaClass ac)
	{
		this.arenaClass = ac;

		this.changeClassOnRespawn = true;
		
		clearPotionEffects();
	}

	/**
	 * Gives the player their class items
	 */
	public void giveClassItems()
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

		if (arenaClass.isUsesEssentials() && plugin.isUseEssentials())
		{
			try
			{
				User user = plugin.getEssentials().getUser(player);

				List<String> items = Kit.getItems(plugin.getEssentials(), user, arenaClass.getEssKitName(), arenaClass.getEssentialsKit());

				Kit.expandItems(plugin.getEssentials(), user, items);
			}
			catch (Throwable e)
			{
				sendMessage("&cCould not give Essentials kit: {0}",
						e instanceof ClassNotFoundException || e instanceof NoSuchMethodError ? "outdated Essentials!" : e.getMessage());
			}
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
	public void clearPotionEffects()
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
	public void sendMessage(String string, Object... objects)
	{
		player.sendMessage(plugin.getPrefix() + FormatUtil.format(string, objects));
	}

	/**
	 * Gives the player xp
	 * 
	 * @param xp
	 *            - XP to give the player
	 */
	public void addXP(int xp)
	{
		this.gameXP += xp;
	}

	/**
	 * Subtracts xp from the player
	 * 
	 * @param xp
	 *            - XP to subtract
	 */
	public void subtractXP(int xp)
	{
		this.gameXP -= xp;
	}

	/**
	 * Gets a player's KDR (Kill-Death Ratio)
	 * 
	 * @return KDR
	 */
	public double getKDR()
	{
		double k = kills;
		if (deaths == 0)
			return k;

		double d = deaths;
		return (k / d);
	}

	private long deathTime;

	/**
	 * Returns whether or not the player is dead
	 * 
	 * @return Whether or not the player is dead
	 */
	public boolean isDead()
	{
		return (System.currentTimeMillis() - deathTime) < 60L;
	}

	/**
	 * Handles the player's death
	 */
	public void onDeath()
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
	public void leaveArena(LeaveReason reason)
	{
		if (reason == LeaveReason.COMMAND)
		{
			arena.endPlayer(this, false);

			sendMessage("&3You have left the arena!");

			arena.tellPlayers("&e{0} &3has left the arena!", getName());
		}

		if (reason == LeaveReason.KICK)
		{
			arena.endPlayer(this, false);

			sendMessage("&cYou have been kicked from the arena!");

			arena.tellPlayers("&e{0} &3has been kicked from the arena!", getName());
		}

		if (reason == LeaveReason.QUIT)
		{
			arena.endPlayer(this, false);

			arena.tellPlayers("&e{0} &3has left the arena!", getName());
		}

		if (reason == LeaveReason.DEATHS)
		{
			arena.endPlayer(this, false);

			sendMessage("&3You have been eliminated!");

			arena.tellPlayers("&e{0} &3has been eliminated!", getName());
		}
	}
	
	public boolean isValid()
	{
		return arena.checkValid(this);
	}
}