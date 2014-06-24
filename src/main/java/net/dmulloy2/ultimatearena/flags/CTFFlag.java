package net.dmulloy2.ultimatearena.flags;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
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

@Getter @Setter
public class CTFFlag
{
	protected ArenaPlayer riding;

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

	public CTFFlag(Arena arena, Location location, int team)
	{
		this.team = team;
		this.arena = arena;

		this.returnto = location.clone();
		this.myloc = location.clone();
		this.lastloc = location.clone();
		this.toloc = location.clone();

		location.getBlock().setType(Material.AIR);

		setup();
	}

	public final void respawn()
	{
		timer = 15;
		pickedUp = false;
		riding = null;
		toloc = returnto.clone();
		myloc = toloc.clone();
		setFlag();
	}

	private final void notifyTime()
	{
		if (timer % 5 == 0 || timer < 10)
		{
			sayTimeLeft();
		}
	}

	private final void sayTimeLeft()
	{
		arena.tellPlayers("&e{0} &3seconds left until &e{1} &3flag returns!", timer, flagType);
	}

	private final void setup()
	{
		Block current = myloc.getBlock();
		lastBlockDat = current.getState().getData();
		lastBlockType = current.getType();

		colorize();
	}

	public final void colorize()
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

	private final void fall()
	{
		arena.tellPlayers("&e{0} &3has dropped the &e{1} &3flag!", riding.getName(), flagType);

		this.timer = 15;
		this.toloc = riding.getPlayer().getLocation();
		this.myloc = toloc.clone();
		this.pickedUp = false;
		this.riding = null;

		int count = 0;
		for (int i = 1; i < 128; i++)
		{
			Block under = myloc.clone().subtract(0, i, 0).getBlock();
			if (under != null)
			{
				if (under.getType().equals(Material.AIR) || under.getType().equals(Material.WATER))
					count++;
				else
					break;
			}
		}

		this.toloc = myloc.clone().subtract(0, count, 0);

		setFlag();
	}

	public final void checkNear(List<ArenaPlayer> arenaPlayers)
	{
		if (stopped)
			return;

		if (! pickedUp)
		{
			for (ArenaPlayer ap : arenaPlayers)
			{
				Player player = ap.getPlayer();
				if (player.getHealth() > 0.0D && player.getWorld().getUID().equals(myloc.getWorld())
						&& player.getLocation().distance(myloc) < 1.75D)
				{
					if (ap.getTeam() != team)
					{
						// If the guy is on the other team
						this.pickedUp = true;
						this.riding = ap;

						ap.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 60 * 4, 1));
						ap.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 60 * 4, 1));
						arena.tellPlayers("&e{0} &3picked up the &e{1} &3flag!", ap.getName(), flagType);
						return;
					}
					else
					{
						if (! myloc.equals(returnto))
						{
							// If the flag is not at its flagstand
							ap.sendMessage("&aFlag Returned! &c+50 XP");
							ap.setGameXP(ap.getGameXP() + 50);
							arena.tellPlayers("&e{0} &3returned the &e{1} &3flag!", ap.getName(), flagType);
							respawn();
							return;
						}
					}
				}
			}
		}
		else
		{
			if (riding.getPlayer().isOnline() && ! riding.isDead())
			{
				// if player is alive
				toloc = riding.getPlayer().getLocation().clone().add(0, 5, 0);
			}
			else
			{
				fall();
			}

			this.myloc = toloc.clone();

			setFlag();
		}
	}

	public final void despawn()
	{
		this.stopped = true;

		Block last = lastloc.getBlock();
		last.setType(lastBlockType);
		last.getState().setData(lastBlockDat);
		last.getState().update();
	}

	public final void tick()
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

	private final void setFlag()
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

	private final void setFlagBlock(Block c)
	{
		if (color == DyeColor.BLUE)
			c.setType(Material.LAPIS_BLOCK);
		else if (color == DyeColor.RED)
			c.setType(Material.NETHERRACK);
		else
			c.setType(Material.WOOL);
	}
}