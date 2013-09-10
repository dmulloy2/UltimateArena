package net.dmulloy2.ultimatearena.types;

import java.util.List;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.ess3.api.IEssentials;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;

import com.earth2me.essentials.Kit;
import com.earth2me.essentials.User;

/**
 * Represents a player inside an {@link Arena}
 * 
 * @author dmulloy2
 */

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
	private ArenaClass mclass;
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
	public ArenaPlayer(Player player, Arena arena, final UltimateArena plugin)
	{
		this.player = player;
		this.name = player.getName();
		this.spawnBack = player.getLocation();

		this.arena = arena;
		this.plugin = plugin;
		this.mclass = plugin.getArenaClass(arena.getArenaZone().getDefaultClass());
	}

	/**
	 * Decides the player's hat
	 */
	public void decideHat()
	{
		if (mclass != null && !mclass.usesHelmet())
		{
			player.getInventory().setHelmet(null);
			return;
		}

		if (player.getInventory().getHelmet() == null)
		{
			ItemStack itemStack = new ItemStack(Material.LEATHER_HELMET, 1);
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
	 * @param slot
	 *            - Slot to put the item in
	 * @param stack
	 *            - {@link ItemStack} to give the player
	 */
	public void giveItem(int slot, ItemStack stack)
	{
		player.getInventory().setItem(slot, stack);
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
		this.mclass = ac;
		
		this.changeClassOnRespawn = true;
	}

	/**
	 * Gives the player their class items
	 */
	public void giveClassItems()
	{
		decideHat();

		if (! arena.isInGame())
			return;

		if (mclass == null)
		{
			giveArmor(0, new ItemStack(Material.IRON_CHESTPLATE));
			giveArmor(1, new ItemStack(Material.IRON_LEGGINGS));
			giveArmor(2, new ItemStack(Material.IRON_BOOTS));
			giveItem(0, new ItemStack(Material.DIAMOND_SWORD));
			return;
		}

		if (mclass.usesEssentials())
		{
			try
			{
				PluginManager pm = plugin.getServer().getPluginManager();
				Plugin essPlugin = pm.getPlugin("Essentials");
				IEssentials ess = (IEssentials) essPlugin;
				User user = ess.getUser(player);

				List<String> items = Kit.getItems(ess, user, mclass.getEssentialsKit());

				Kit.expandItems(ess, user, items);
			}
			catch (Exception e)
			{
				sendMessage("&cCould not give Essentials kit: {0}", e instanceof ClassNotFoundException ? "outdated Essentials!" : e);
			}
		}

		for (int i = 0; i < mclass.getArmor().size(); i++)
		{
			ItemStack stack = mclass.getArmor(i);
			if (stack != null)
			{
				giveArmor(i, stack);
			}
		}

		for (int i = 0; i < mclass.getWeapons().size(); i++)
		{
			ItemStack stack = mclass.getWeapon(i);
			if (stack != null)
			{
				giveItem(i, stack);
			}
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

	public int getKills()
	{
		return kills;
	}

	public void setKills(int kills)
	{
		this.kills = kills;
	}

	public int getDeaths()
	{
		return deaths;
	}

	public void setDeaths(int deaths)
	{
		this.deaths = deaths;
	}

	public int getKillStreak()
	{
		return killStreak;
	}

	public void setKillStreak(int killStreak)
	{
		this.killStreak = killStreak;
	}

	public int getGameXP()
	{
		return gameXP;
	}

	public void setGameXP(int gameXP)
	{
		this.gameXP = gameXP;
	}

	public int getPoints()
	{
		return points;
	}

	public void setPoints(int points)
	{
		this.points = points;
	}

	public int getBaseLevel()
	{
		return baseLevel;
	}

	public int getHealTimer()
	{
		return healTimer;
	}

	public void setHealTimer(int healTimer)
	{
		this.healTimer = healTimer;
	}

	public boolean isOut()
	{
		return out;
	}

	public void setOut(boolean out)
	{
		this.out = out;
	}

	public boolean canReward()
	{
		return canReward;
	}

	public void setCanReward(boolean canReward)
	{
		this.canReward = canReward;
	}

	public Location getSpawnBack()
	{
		return spawnBack;
	}

	public int getTeam()
	{
		return team;
	}

	public void setTeam(int team)
	{
		this.team = team;
	}

	public int getAmtKicked()
	{
		return amtKicked;
	}

	public void setAmtKicked(int amtKicked)
	{
		this.amtKicked = amtKicked;
	}

	public ArenaClass getArenaClass()
	{
		return mclass;
	}

	public Arena getArena()
	{
		return arena;
	}

	public void sendMessage(String string, Object... objects)
	{
		player.sendMessage(plugin.getPrefix() + FormatUtil.format(string, objects));
	}

/*	@Override
	public void sendMessage(String string)
	{
		player.sendMessage(plugin.getPrefix() + FormatUtil.format(string));
	}
*/
	public void addXP(int xp)
	{
		setGameXP(getGameXP() + xp);
	}

	public void subtractXP(int xp)
	{
		setGameXP(getGameXP() - xp);
	}

	public void setBaseLevel(int baseLevel)
	{
		this.baseLevel = baseLevel;
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

//	@Override
	public boolean isDead()
	{
		return (System.currentTimeMillis() - deathTime) < 60L;
	}

	public void onDeath()
	{	
		this.deathTime = System.currentTimeMillis();
		this.killStreak = 0;
		this.deaths++;
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof ArenaPlayer)
		{
			ArenaPlayer op = (ArenaPlayer) o;
			return op.getName().equals(name);
		}

		return false;
	}

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
	
	public Player getPlayer()
	{
		return player;
	}
	
	public String getName()
	{
		return name;
	}

	public boolean changeClassOnRespawn()
	{
		return changeClassOnRespawn;
	}
}