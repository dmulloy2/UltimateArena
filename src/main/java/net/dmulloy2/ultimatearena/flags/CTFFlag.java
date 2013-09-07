package net.dmulloy2.ultimatearena.flags;

import java.util.List;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CTFFlag
{
	private Player riding;

	private String flagType = "";

	private Arena arena;

	private Location returnto;
	private Location myloc;
	private Location toloc;
	private Location lastloc;

	private int lastBlockType;
	private int team;
	private int timer = 15;

	private boolean pickedUp;
	private boolean stopped;

	private byte color;
	private byte lastBlockDat;

	public CTFFlag(Arena a, Location loc, int team)
	{
		this.setTeam(team);
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
		setPickedUp(false);
		setRiding(null);
		toloc = getReturnto().clone();
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
		arena.tellPlayers("&e{0} &3seconds left until &e{1} &3flag returns!", timer, getFlagType());
	}

	@SuppressWarnings("deprecation")
	public void setup()
	{
		final Block current = myloc.getBlock();
		lastBlockDat = current.getData();
		lastBlockType = current.getTypeId();
		colorize();
	}

	public void colorize()
	{
		Block current = myloc.getBlock();
		if (getTeam() == 1)
		{
			color = 14; // Red team
			setFlagType(ChatColor.RED + "RED");
		}
		else
		{
			color = 11; // Blue team
			setFlagType(ChatColor.BLUE + "BLUE");
		}

		setFlagBlock(current);
	}

	public void fall()
	{
		arena.tellPlayers("&e{0} &3has dropped the &e{1} &3flag!", getRiding().getName(), getFlagType());
		timer = 15;
		toloc = getRiding().getLocation();
		setPickedUp(false);
		setRiding(null);

		myloc = toloc.clone();

		int count = 0;
		boolean can = true;
		for (int i = 1; i < 128; i++)
		{
			if (can)
			{
				Block BlockUnder = ((myloc.clone()).subtract(0, i, 0)).getBlock();
				if (BlockUnder != null)
				{
					if (BlockUnder.getType().equals(Material.AIR) || BlockUnder.getType().equals(Material.WATER))
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

		toloc = myloc.clone().subtract(0, count, 0);
		setFlag();
	}

	public void checkNear(List<ArenaPlayer> arenaplayers)
	{
		if (isStopped())
			return;

		if (!isPickedUp())
		{
			for (int i = 0; i < arenaplayers.size(); i++)
			{
				ArenaPlayer pl = arenaplayers.get(i);
				if (pl != null && !pl.isOut())
				{
					if (Util.pointDistance(pl.getPlayer().getLocation(), myloc) < 1.75 && pl.getPlayer().getHealth() > 0.0D)
					{
						if (pl.getTeam() != getTeam())
						{
							// If the guy is on the other team
							this.pickedUp = true;
							this.riding = pl.getPlayer();

							pl.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * (60 * 4), 1));
							pl.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * (60 * 4), 1));
							arena.tellPlayers("&e{0} &3picked up the &e{1} &3flag!", pl.getName(), getFlagType());
							return;
						}
						else
						{
							if (!myloc.equals(getReturnto()))
							{
								// If the flag is not at its flagstand
								pl.sendMessage("&aFlag Returned! &c+50 XP");
								arenaplayers.get(i).setGameXP(arenaplayers.get(i).getGameXP() + 50);
								arena.tellPlayers("&e{0} &3returned the &e{1} &3flag!", pl.getName(), getFlagType());
								respawn();
								return;
							}
						}
					}
				}
			}
		}

		if (isPickedUp())
		{
			if (riding.isOnline() && !riding.isDead())
			{
				// if player is alive
				toloc = getRiding().getLocation().clone().add(0, 3, 0);
			}
			else
			{
				fall();
			}

			myloc = toloc.clone();
			setFlag();
		}
	}

	@SuppressWarnings("deprecation")
	public void despawn()
	{
		setStopped(true);

		Block last = lastloc.getBlock();
		last.setTypeIdAndData(lastBlockType, lastBlockDat, false);
	}

	public void tick()
	{
		if (isStopped())
			return;

		if (!pickedUp)
		{
			if (!myloc.equals(getReturnto()))
			{
				// if the flag is not at its flagstand
				timer--;
				if (timer <= 0)
				{
					respawn();

					arena.tellPlayers("&3The &e{0} &3flag has respawned!", getFlagType());
				}
				else
				{
					notifyTime();
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void setFlag()
	{
		if (isStopped())
			return;

		Block last = lastloc.getBlock();
		Block current = myloc.getBlock();

		if (!Util.checkLocation(lastloc, myloc))
		{
			last.setTypeIdAndData(lastBlockType, lastBlockDat, true);
			lastBlockDat = current.getData();
			lastBlockType = current.getTypeId();
			lastloc = myloc.clone();
			setFlagBlock(current);
		}
	}

	private void setFlagBlock(Block c)
	{
		if (color == 11)
			c.setType(Material.LAPIS_BLOCK);
		if (color == 14)
			c.setType(Material.NETHERRACK);
	}

	public int getTeam()
	{
		return team;
	}

	public void setTeam(int team)
	{
		this.team = team;
	}

	public boolean isPickedUp()
	{
		return pickedUp;
	}

	public void setPickedUp(boolean pickedUp)
	{
		this.pickedUp = pickedUp;
	}

	public Player getRiding()
	{
		return riding;
	}

	public void setRiding(Player riding)
	{
		this.riding = riding;
	}

	public String getFlagType()
	{
		return flagType;
	}

	public void setFlagType(String flagType)
	{
		this.flagType = flagType;
	}

	public boolean isStopped()
	{
		return stopped;
	}

	public void setStopped(boolean stopped)
	{
		this.stopped = stopped;
	}

	public Location getReturnto()
	{
		return returnto;
	}

	public void setReturnto(Location returnto)
	{
		this.returnto = returnto;
	}
}