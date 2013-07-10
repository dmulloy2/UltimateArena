package net.dmulloy2.ultimatearena.arenas.objects;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.Util;

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

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Kit;
import com.earth2me.essentials.User;

public class ArenaPlayer 
{
	private int kills = 0;
	private int deaths = 0;
	private int killstreak = 0;
	private int gameXP = 0;
	private int team = 1;
	private int points = 0;
	private int baselevel = 0;
	private int amtkicked = 0;
	private int healtimer = 0;
	
	private boolean out = false;
	private boolean canReward = false;
	
	private Player player;
	private String username;
	
	private Arena arena;
	private ArenaClass mclass;
	private Location spawnBack;
	
	private final UltimateArena plugin;
	
	private List<ItemStack> savedInventory = new ArrayList<ItemStack>();
	private List<ItemStack> savedArmor = new ArrayList<ItemStack>();
	
	public ArenaPlayer(Player player, Arena arena, final UltimateArena plugin)
	{
		this.player = player;
		this.username = player.getName();
		this.spawnBack = player.getLocation();
		this.baselevel = player.getLevel();
		
		this.arena = arena;
		this.plugin = plugin;
		this.mclass = plugin.getArenaClass(arena.getArenaZone().getDefaultClass());
	}
	
	public void decideHat(Player p)
	{
		if (mclass != null && !mclass.usesHelmet())
		{
			p.getInventory().setHelmet(null);
			return;
		}
		
		if (p.getInventory().getHelmet() == null)
		{
			ItemStack itemStack = new ItemStack(Material.LEATHER_HELMET, 1);
			LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
			Color teamColor = Color.RED;
			if (getTeam() == 2)
				teamColor = Color.BLUE;
			meta.setColor(teamColor);
			itemStack.setItemMeta(meta);
			p.getInventory().setHelmet(itemStack);
		}
	}
	
	private void giveItem(int slot, ItemStack stack) 
	{
		player.getInventory().setItem(slot, stack);
	}
	
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
	
	public void saveInventory()
	{
		PlayerInventory inv = getPlayer().getInventory();
		for (ItemStack itemStack : inv.getContents())
		{
			if (itemStack != null && itemStack.getType() != Material.AIR)
			{
				getSavedInventory().add(itemStack);
			}
		}
		
		for (ItemStack armor : inv.getArmorContents())
		{
			if (armor != null && armor.getType() != Material.AIR)
			{
				getSavedArmor().add(armor);
			}
		}
	}
	
	public void clearInventory()
	{
		PlayerInventory inv = getPlayer().getInventory();
		inv.setHelmet(null);
		inv.setChestplate(null);
		inv.setLeggings(null);
		inv.setBoots(null);
		inv.clear();
	}
	
	public void returnInventory()
	{
		PlayerInventory inv = getPlayer().getInventory();
		for (ItemStack itemStack : getSavedInventory())
		{
			inv.addItem(itemStack);
		}
		
		for (ItemStack armor : getSavedArmor())
		{
			String type = armor.getType().toString().toLowerCase();
			if (type.contains("helmet"))
			{
				inv.setHelmet(armor);
			}
				
			if (type.contains("chestplate"))
			{
				inv.setChestplate(armor);
			}
				
			if (type.contains("leggings"))
			{
				inv.setLeggings(armor);
			}
				
			if (type.contains("boots"))
			{
				inv.setBoots(armor);
			}
		}
	}
	
	public void spawn()
	{
		if (getAmtkicked() > 10)
		{
			plugin.leaveArena(getPlayer());
		}
			
		Player p = Util.matchPlayer(getPlayer().getName());
		p.getInventory().clear();
		
		giveClassItems(p);
	}
	
	public void setClass(ArenaClass ac, boolean command)
	{
		this.mclass = ac;
		
		clearInventory();
		clearPotionEffects();

		giveClassItems(getPlayer());
	}
	
	public void giveClassItems(Player p)
	{
		decideHat(p);
		
		if (! arena.isInGame())
			return;
			
		if (mclass == null) 
		{
			p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
			p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
			p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS, 1));
			p.getInventory().setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
			return;
		}
				
		if (mclass.usesEssentials())
		{
			try
			{
				PluginManager pm = plugin.getServer().getPluginManager();
				Plugin essPlugin = pm.getPlugin("Essentials");
				IEssentials ess = (IEssentials) essPlugin;
				User user = ess.getUser(p);
								
				List<String> items = Kit.getItems(user, mclass.getEssentialsKit());
					
				Kit.expandItems(ess, user, items);
				return;
			}
			catch (Exception e)
			{
				plugin.getLogger().severe("Error giving class items: " + e.getMessage());
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
	}

	public void clearPotionEffects()
	{
		for (PotionEffect effect : getPlayer().getActivePotionEffects())
		{
			getPlayer().removePotionEffect(effect.getType());
		}
	}

	public int getKills()
	{
		return kills;
	}

	public void setKills(int kills) {
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

	public int getKillstreak()
	{
		return killstreak;
	}

	public void setKillstreak(int killstreak)
	{
		this.killstreak = killstreak;
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

	public int getBaselevel()
	{
		return baselevel;
	}

	public int getHealtimer()
	{
		return healtimer;
	}

	public void setHealtimer(int healtimer)
	{
		this.healtimer = healtimer;
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

	public String getUsername() 
	{
		return username;
	}
	
	public Location getSpawnBack() 
	{
		return spawnBack;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public int getTeam() 
	{
		return team;
	}

	public void setTeam(int team) 
	{
		this.team = team;
	}

	public int getAmtkicked() 
	{
		return amtkicked;
	}

	public void setAmtkicked(int amtkicked) 
	{
		this.amtkicked = amtkicked;
	}

	public List<ItemStack> getSavedInventory() 
	{
		return savedInventory;
	}

	public List<ItemStack> getSavedArmor() 
	{
		return savedArmor;
	}
	
	public ArenaClass getArenaClass()
	{
		return mclass;
	}
	
	public Arena getArena() 
	{
		return arena;
	}
	
	public void sendMessage(String string, Object...objects)
	{
		player.sendMessage(plugin.getPrefix() + FormatUtil.format(string, objects));
	}
	
	public void addXP(int xp)
	{
		setGameXP(getGameXP() + xp);
	}
	
	public void subtractXP(int xp)
	{
		setGameXP(getGameXP() - xp);
	}
	
	public double getKDR()
	{
		if (deaths == 0)
		{
			return (double) kills;
		}

		return ((double) kills / (double) deaths);
	}
}