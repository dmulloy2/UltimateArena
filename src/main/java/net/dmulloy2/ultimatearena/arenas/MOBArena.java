package net.dmulloy2.ultimatearena.arenas;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.util.InventoryHelper;
import net.dmulloy2.ultimatearena.util.Util;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

public class MOBArena extends Arena 
{
	private int mobtimer = 0;
	private int mobspawn, mobPerWave;
	
	private List<LivingEntity> mobs = new ArrayList<LivingEntity>();
	private List<String> spawning = new ArrayList<String>();
	
	public MOBArena(ArenaZone az) 
	{
		super(az);
		
		setType("Mob");
		setStarttimer(80);
		setMaxgametime(60 * 10);
		setMaxDeaths(0);
		mobspawn = 0;
		mobtimer = 0;
		setWave(0);
		setWinningTeam(-1);
		
		spawning.add("ZOMBIE");
		spawning.add("ZOMBIE");
		spawning.add("ZOMBIE");
		
		newWave();
	}
	
	public void newWave()
	{
		if (getWave() > 0)
		{
			tellPlayers("&aYou survived the wave!");
			tellPlayers("&aNow going to wave &c{0}&a!", getWave());
		}
		
		setWave(getWave() + 1);
		this.mobPerWave = 4 + ((int)(getWave() * 1.5)) + (this.getAmtPlayersInArena() * 3);
		mobtimer = (getWave()*4) + 20;
		if (getWave() <= 1) 
		{
			mobtimer = 1;
		}
		if (getWave() > 1)
		{
			spawning.add("ZOMBIE");
			spawning.add("ZOMBIE");
			spawning.add("SKELETON");
		}
		if (getWave() > 3) 
		{
			spawning.add("SPIDER");
		}
		if (getWave() > 6) 
		{
			spawning.add("BLAZE");
			spawning.add("BLAZE");
		}
		if (getWave() > 9) 
		{
			spawning.add("PIG_ZOMBIE");
			spawning.add("ENDERMAN");
		}
		if (getWave() > 12)
		{
			spawning.add("GHAST");
		}
	}
	
	@Override
	public void endPlayer(ArenaPlayer p, boolean end)
	{
		super.endPlayer(p, end);
		this.reward(p, p.getPlayer(), false);
	}
	
	@Override
	public void reward(ArenaPlayer p, Player pl, boolean half)
	{
		int amtGold = (int) Math.floor(p.getGameXP() / 500.0);
		int amtSlime = (int) Math.floor(p.getGameXP() / 550.0);
		int amtGlowStone = (int) Math.floor(p.getGameXP() / 450.0);
		int amtGunPowder = (int) Math.floor(p.getGameXP() / 425.0);
		int amtCash = (int) Math.floor(p.getGameXP() / 10.0);

		if (pl != null) 
		{
			if (amtGold > 0) 
				InventoryHelper.addItem(pl, new ItemStack(Material.GOLD_INGOT, amtGold));
			
			if (amtSlime > 0) 
				InventoryHelper.addItem(pl, new ItemStack(Material.SLIME_BALL, amtSlime));
			
			if (amtGlowStone > 0) 
				InventoryHelper.addItem(pl, new ItemStack(Material.GLOWSTONE_DUST, amtGlowStone));
			
			if (amtGunPowder > 0) 
				InventoryHelper.addItem(pl, new ItemStack(Material.SULPHUR, amtGunPowder));
			
			if (amtCash > 0 && plugin.getConfig().getBoolean("moneyrewards"))
			{ 
				Economy eco = plugin.getEconomy();
				if (eco != null)
				{
					eco.depositPlayer(pl.getName(), amtCash);
					
					String cash = eco.format(amtCash);
					p.sendMessage("&a{0} has been added to your balance!", cash);
				}
			}
		}
	}
	
	@Override
	public void onOutOfTime()
	{
		this.setWinningTeam(-1);
		this.rewardTeam(getWinningTeam(), "&9You won!", false);
	}
	
	@Override
	public void stop() 
	{
		super.stop();
		synchronized(mobs) 
		{
			for (LivingEntity entity : mobs)
			{
				if (entity != null)
					entity.remove();
			}
		}
	}
	
	@Override
	public void doKillStreak(ArenaPlayer ap)
	{
		Player pl = ap.getPlayer();
		if (pl != null) 
		{
			if (ap.getKillstreak() == 8)
				givePotion(pl, "strength", 1, 1, false, "8 kills! Unlocked strength potion!");
			
			if (ap.getKillstreak() == 12)
				givePotion(pl, "speed", 1, 1, false, "12 kills! Unlocked swiftness potion!");
				
			if (ap.getKillstreak() == 16)
				givePotion(pl, "fireres", 1, 1, false, "16 kills! Unlocked antifire!");
			
			if (ap.getKillstreak() == 24) 
			{
				givePotion(pl, "heal", 1, 1, false, "24 kills! Unlocked health potion!");
				giveItem(pl, Material.GRILLED_PORK.getId(), (byte)0, 2, "24 kills! Unlocked food!");
			}
				
			if (ap.getKillstreak() == 32) 
			{
				ap.sendMessage("32 kills! Unlocked attackdogs!");
				for (int i = 0; i < 3; i++)
				{
					Wolf wolf = (Wolf) pl.getLocation().getWorld().spawnEntity(pl.getLocation(), EntityType.WOLF);
					wolf.setOwner(pl);
				}
			}
				
			if (ap.getKillstreak() == 40) 
			{
				givePotion(pl, "regen", 1, 1, false, "40 kills! Unlocked regen potion!");
				giveItem(pl, Material.GRILLED_PORK.getId(), (byte)0, 2, "40 kills! Unlocked food!");
			}
				
			if (ap.getKillstreak() == 72)
				giveItem(pl, Material.GOLDEN_APPLE.getId(), (byte)0, 2, "72 kills! Unlocked Golden Apples!");

			if (ap.getKillstreak() == 112)
				giveItem(pl, Material.GOLDEN_APPLE.getId(), (byte)0, 2, "112 kills! Unlocked Golden Apples!");
		}
	}
	
	@Override
	public void check() 
	{
		if (getStarttimer() <= 0) 
		{
			mobtimer--;
			mobspawn--;
			if (mobspawn < 0) 
			{
				if (mobtimer < 0) 
				{
					newWave();
					synchronized(mobs)
					{
						for (int i = 0; i < mobPerWave; i++) 
						{
							Location loc = this.getArenaZone().getSpawns().get(Util.random(this.getArenaZone().getSpawns().size()));
							String mob = this.spawning.get(Util.random(spawning.size()));
							LivingEntity newMob = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.valueOf(mob));
							
							if (newMob.getType() == EntityType.SKELETON)
							{
								if (Util.random(2) == 0 && getWave() >= 12)
								{
									// Wither skeletons! >:D
									((Skeleton)newMob).setSkeletonType(Skeleton.SkeletonType.WITHER);
								}
							}
							
							mobs.add(newMob);
						}
					}
				}
			}
				
			if (getAmtPlayersInArena() == 0) 
			{
				plugin.outConsole("Stopping Mob arena");
				stop();
			}
			
			if (getWave() > getMaxwave()) 
			{
				setWinningTeam(-1);
				tellPlayers("&aYou have beat the mob arena!");
				stop();
				rewardTeam(-1, "&0You won!", false);
			}
		}
	}
}