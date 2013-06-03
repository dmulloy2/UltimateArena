package com.orange451.UltimateArena.Arenas.Objects;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;

import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.util.Util;

public class ArenaPlayer 
{
	public int kills      = 0;
	public int deaths     = 0;
	public int killstreak = 0;
	public int XP         = 0;
	public int team       = 1;
	public int points     = 0;
	public int baselevel  = 0;
	public int amtkicked = 0;
	public float startxp  = 0;
	public boolean out    = false;
	public boolean canReward = false;
	public String username;
	public int healtimer = 0;
	public ArenaClass mclass;
	public Location spawnBack;
	public Arena inArena;
	public Player player;
	
	public ArenaPlayer(Player p, Arena a)
	{
		this.player = p;
		this.username = p.getName();
		this.inArena = a;
		this.spawnBack = p.getLocation().clone();
		this.mclass = a.az.plugin.getArenaClass(a.az.defaultClass);
		this.baselevel = p.getLevel();
		this.startxp = p.getExp();
	}
	
	public void decideHat(Player p)
	{
		if (p.getInventory().getHelmet() == null)
		{
			ItemStack itemStack = new ItemStack(Material.LEATHER_HELMET, 1);
			LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
			Color teamColor = Color.RED;
			if (team == 2)
				teamColor = Color.BLUE;
			meta.setColor(teamColor);
			itemStack.setItemMeta(meta);
			p.getInventory().setHelmet(itemStack);
		}
	}
	
	public void giveItem(Player p, int weapon1, byte dat, int amt, int slot, List<CompositeEnchantment> enchants)
	{
		if (weapon1 > 0)
		{
			Material mat = Material.getMaterial(weapon1);
			if (mat != null)
			{
				if (!mat.equals(Material.AIR)) 
				{
					ItemStack itemStack = new ItemStack(mat, amt);
					if (enchants != null && enchants.size() > 0)
					{
						for (CompositeEnchantment enchantment : enchants)
						{
							Enchantment ench = enchantment.getType();
							int level = enchantment.getLevel();
							try { itemStack.addUnsafeEnchantment(ench, level); }
							catch (Exception e) {}
						}
					}
					if (dat == 0)
					{
						p.getInventory().addItem(itemStack);
					}
					else
					{
						if (itemStack.getType() == Material.POTION)
						{
							PotionMeta meta = (PotionMeta)itemStack.getItemMeta();
							PotionEffectType effect = PotionEffectType.getById(dat);
							meta.setMainEffect(effect);
							p.getInventory().setItem(slot, itemStack);
						}
						MaterialData data = itemStack.getData();
						data.setData(dat);
						itemStack.setData(data);
						p.getInventory().setItem(slot, itemStack);
					}
				}
			}
		}
	}
	
	public void spawn()
	{
//		try
//		{
			if (this.amtkicked > 10)
			{
				this.inArena.az.plugin.leaveArena(this.player);
			}
			Player p = Util.matchPlayer(player.getName());
			p.getInventory().clear();
			decideHat(p);
			if (inArena.starttimer <= 0 && inArena.gametimer >= 2) 
			{
				if (mclass == null) 
				{
					p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
					p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
					p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS, 1));
					p.getInventory().setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
				}
				else
				{
//					try
//					{
						if (mclass.armor1 > 0) { p.getInventory().setChestplate(new ItemStack(Material.getMaterial(mclass.armor1), 1)); }
						if (mclass.armor2 > 0) { p.getInventory().setLeggings(new ItemStack(Material.getMaterial(mclass.armor2), 1)); }
						if (mclass.armor3 > 0) { p.getInventory().setBoots(new ItemStack(Material.getMaterial(mclass.armor3), 1)); }
						if (mclass.helmet == false) { p.getInventory().setHelmet(null); }
						
						giveItem(p, mclass.weapon1, mclass.special1, mclass.amt1, 0, mclass.enchant1);
						giveItem(p, mclass.weapon2, mclass.special2, mclass.amt2, 1, mclass.enchant2);
						giveItem(p, mclass.weapon3, mclass.special3, mclass.amt3, 2, mclass.enchant3);
						giveItem(p, mclass.weapon4, mclass.special4, mclass.amt4, 3, mclass.enchant4);
						giveItem(p, mclass.weapon5, mclass.special5, mclass.amt5, 4, mclass.enchant5);
						giveItem(p, mclass.weapon6, mclass.special6, mclass.amt6, 5, mclass.enchant6);
						giveItem(p, mclass.weapon7, mclass.special7, mclass.amt7, 6, mclass.enchant7);
						giveItem(p, mclass.weapon8, mclass.special8, mclass.amt8, 7, mclass.enchant8);
						giveItem(p, mclass.weapon9, mclass.special9, mclass.amt9, 8, mclass.enchant9);
//					}
//					catch(Exception e) 
//					{
//						inArena.az.plugin.getLogger().severe("Error giving player class items!");
//					}
				}
			}
//		}
//		catch(Exception e)
//		{
//			inArena.az.plugin.getLogger().severe("Error spawning: " + e.getMessage());
//		}
	}
}