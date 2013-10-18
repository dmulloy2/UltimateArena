package net.dmulloy2.ultimatearena.arenas;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
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
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class MOBArena extends Arena
{
	private int mobtimer, mobspawn, mobPerWave;

	private List<LivingEntity> mobs = new ArrayList<LivingEntity>();
	private List<String> spawning = new ArrayList<String>();

	public MOBArena(ArenaZone az)
	{
		super(az);

		this.type = FieldType.MOB;
		this.startTimer = 80;
		this.maxGameTime = 60 * 10;
		this.maxDeaths = 1;
		this.mobspawn = 0;
		this.mobtimer = 0;
		this.wave = 0;
		this.winningTeam = -1;

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

		wave++;
		this.mobPerWave = 4 + ((int) (wave * 1.5)) + (active.size() * 3);
		this.mobtimer = (wave * 4) + 20;

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
	public void endPlayer(ArenaPlayer ap, boolean end)
	{
		super.endPlayer(ap, end);

		reward(ap);
	}

	@Override
	public void reward(ArenaPlayer p)
	{
		int amtGold = (int) Math.round(p.getGameXP() / 500.0);
		int amtSlime = (int) Math.round(p.getGameXP() / 550.0);
		int amtGlowStone = (int) Math.round(p.getGameXP() / 450.0);
		int amtGunPowder = (int) Math.round(p.getGameXP() / 425.0);
		int amtCash = (int) Math.round(p.getGameXP() / 10.0);

		Player pl = p.getPlayer();

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

	@Override
	public void onOutOfTime()
	{
		setWinningTeam(-1);
		rewardTeam(winningTeam);
	}

	@Override
	public void onStop()
	{
//		synchronized (mobs)
//		{
//			for (LivingEntity entity : mobs)
//			{
//				if (entity != null)
//					entity.remove();
//			}
//		}
	}

	@Override
	public void doKillStreak(ArenaPlayer ap)
	{
		Player pl = ap.getPlayer();

		if (ap.getKillStreak() == 8)
			givePotion(pl, "strength", 1, 1, false, "&e8 &3kills! Unlocked strength potion!");

		if (ap.getKillStreak() == 12)
			givePotion(pl, "speed", 1, 1, false, "&e12 &3kills! Unlocked swiftness potion!");

		if (ap.getKillStreak() == 16)
			givePotion(pl, "fireres", 1, 1, false, "&e16 &3kills! Unlocked antifire!");

		if (ap.getKillStreak() == 24)
		{
			givePotion(pl, "heal", 1, 1, false, "&e24 &3kills! Unlocked health potion!");
			giveItem(pl, Material.GRILLED_PORK, 2, (short) 0, "&e24 &3kills! Unlocked food!");
		}

		if (ap.getKillStreak() == 32)
		{
			ap.sendMessage("&e32 &3kills! Unlocked attackdogs!");
			for (int i = 0; i < 3; i++)
			{
				Wolf wolf = (Wolf) pl.getLocation().getWorld().spawnEntity(pl.getLocation(), EntityType.WOLF);
				wolf.setOwner(pl);
			}
		}

		if (ap.getKillStreak() == 40)
		{
			givePotion(pl, "regen", 1, 1, false, "&e40 &3kills! Unlocked regen potion!");
			giveItem(pl, Material.GRILLED_PORK, 2, (short) 0, "&e40 kills! Unlocked food!");
		}

		if (ap.getKillStreak() == 72)
			giveItem(pl, Material.GOLDEN_APPLE, 2, (short) 0, "&e72 &3kills! Unlocked Golden Apples!");

		if (ap.getKillStreak() == 112)
			giveItem(pl, Material.GOLDEN_APPLE, 2, (short) 0, "&e112 &3kills! Unlocked Golden Apples!");
	}

	@Override
	public void check()
	{
		if (startTimer <= 0)
		{
			mobtimer--;
			mobspawn--;
			if (mobspawn < 0)
			{
				if (mobtimer < 0)
				{
					newWave();
					synchronized (mobs)
					{
						for (int i = 0; i < mobPerWave; i++)
						{
							Location loc = az.getSpawns().get(Util.random(az.getSpawns().size()));
							String mob = spawning.get(Util.random(spawning.size()));
							LivingEntity newMob = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.valueOf(mob));

							if (newMob instanceof Skeleton)
							{
								if (Util.random(2) == 0 && getWave() >= 12)
								{
									// Wither skeletons! >:3
									((Skeleton) newMob).setSkeletonType(Skeleton.SkeletonType.WITHER);
								}
							}

							if (newMob instanceof Zombie)
							{
								if (Util.random(5) == 0 && wave >= 7)
								{
									// Baby zombies! >:3
									((Zombie) newMob).setBaby(true);
								}
							}

							mobs.add(newMob);
						}
					}
				}
			}

			if (active.size() <= 0)
			{
				stop();
			}

			if (wave > maxWave)
			{
				setWinningTeam(-1);

				stop();

				rewardTeam(-1);
			}
		}
	}

	@Override
	public void announceWinner()
	{
		if (winningTeam == -1)
		{
			if (wave > maxWave)
			{
				tellPlayers("&3You have beat the MobArena!");
			}
			else
			{
				tellPlayers("&3You survived the MobArena!");
			}
		}
	}
}