package net.dmulloy2.ultimatearena.flags;

import java.util.List;

import lombok.Data;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author dmulloy2
 */

@Data
public class CTFFlag
{
	protected Player riding;

	protected String flagType = "";

	protected Arena arena;

	protected Location returnto;
	protected Location myloc;
	protected Location toloc;
	protected Location lastloc;

	protected int team;
	protected int timer = 15;

	protected boolean pickedUp;
	protected boolean stopped;
	
	protected Material lastBlockType;
	
	protected DyeColor color;
	protected MaterialData lastBlockDat;

	public CTFFlag(Arena a, Location loc, int team)
	{
		this.team = team;
		this.arena = a;

		this.returnto = loc.clone();
		this.myloc = loc.clone();
		this.lastloc = loc.clone();
		this.toloc = loc.clone();

		loc.getBlock().setType(Material.AIR);

		setup();
	}

	public void respawn()
	{
		timer = 15;
		pickedUp = false;
		riding = null;
		toloc = returnto.clone();
		myloc = toloc.clone();
		setFlag();
	}

	public void notifyTime()
	{
		if (timer % 5 == 0 || timer < 10)
		{
			sayTimeLeft();
		}
	}

	public void sayTimeLeft()
	{
		arena.tellPlayers("&e{0} &3seconds left until &e{1} &3flag returns!", timer, flagType);
	}

	public void setup()
	{
		Block current = myloc.getBlock();
		lastBlockDat = current.getState().getData();
		lastBlockType = current.getType();

		colorize();
	}

	public void colorize()
	{
		Block current = myloc.getBlock();
		if (team == 1)
		{
			this.color = DyeColor.RED;
			this.flagType = ChatColor.RED + "RED";
		}
		else
		{
			this.color = DyeColor.BLUE;
			this.flagType = ChatColor.BLUE + "BLUE";
		}

		setFlagBlock(current);
	}

	public void fall()
	{
		arena.tellPlayers("&e{0} &3has dropped the &e{1} &3flag!", riding.getName(), flagType);

		this.timer = 15;
		this.toloc = riding.getLocation();
		this.myloc = toloc.clone();
		this.pickedUp = false;
		this.riding = null;

		int count = 0;
		boolean can = true;
		for (int i = 1; i < 128; i++)
		{
			if (can)
			{
				Block under = myloc.clone().subtract(0, i, 0).getBlock();
				if (under != null)
				{
					if (under.getType().equals(Material.AIR) || under.getType().equals(Material.WATER))
					{
						count++;
					}
					else
					{
						can = false;
					}
				}
			}
		}

		this.toloc = myloc.clone().subtract(0, count, 0);

		setFlag();
	}

	public void checkNear(List<ArenaPlayer> arenaPlayers)
	{
		if (stopped)
			return;

		if (! pickedUp)
		{
			for (ArenaPlayer pl : arenaPlayers)
			{
				if (Util.pointDistance(pl.getPlayer().getLocation(), myloc) < 1.75 && pl.getPlayer().getHealth() > 0.0D)
				{
					if (pl.getTeam() != team)
					{
						// If the guy is on the other team
						this.pickedUp = true;
						this.riding = pl.getPlayer();

						pl.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * (60 * 4), 1));
						pl.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * (60 * 4), 1));
						arena.tellPlayers("&e{0} &3picked up the &e{1} &3flag!", pl.getName(), flagType);
						return;
					}
					else
					{
						if (! myloc.equals(returnto))
						{
							// If the flag is not at its flagstand
							pl.sendMessage("&aFlag Returned! &c+50 XP");
							pl.setGameXP(pl.getGameXP() + 50);
							arena.tellPlayers("&e{0} &3returned the &e{1} &3flag!", pl.getName(), flagType);
							respawn();
							return;
						}
					}
				}
			}
		}
		else
		{
			if (riding.isOnline() && !riding.isDead())
			{
				// if player is alive
				toloc = riding.getLocation().clone().add(0, 3, 0);
			}
			else
			{
				fall();
			}

			this.myloc = toloc.clone();

			setFlag();
		}
	}

	public void despawn()
	{
		this.stopped = true;

		Block last = lastloc.getBlock();
		last.setType(lastBlockType);
		last.getState().setData(lastBlockDat);
		last.getState().update();
	}

	public void tick()
	{
		if (stopped)
			return;

		if (! pickedUp)
		{
			if (! myloc.equals(returnto))
			{
				// if the flag is not at its flagstand
				timer--;
				if (timer <= 0)
				{
					respawn();

					arena.tellPlayers("&3The &e{0} &3flag has respawned!", flagType);
				}
				else
				{
					notifyTime();
				}
			}
		}
	}

	public void setFlag()
	{
		if (stopped) 
			return;

		Block last = lastloc.getBlock();
		Block current = myloc.getBlock();

		if (! Util.checkLocation(lastloc, myloc))
		{
			last.setType(lastBlockType);
			last.getState().setData(lastBlockDat);
			last.getState().update();
			
			this.lastBlockType = current.getType();
			this.lastBlockDat = current.getState().getData();
			
			this.lastloc = myloc.clone();

			setFlagBlock(current);
		}
	}

	private void setFlagBlock(Block c)
	{
		if (color == DyeColor.BLUE)
			c.setType(Material.LAPIS_BLOCK);
		else if (color == DyeColor.RED)
			c.setType(Material.NETHERRACK);
		else
			c.setType(Material.WOOL);
	}
}