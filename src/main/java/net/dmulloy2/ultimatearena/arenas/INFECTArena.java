package net.dmulloy2.ultimatearena.arenas;

import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author dmulloy2
 */

public class INFECTArena extends PVPArena
{
	public INFECTArena(ArenaZone az)
	{
		super(az);
		this.type = FieldType.INFECT;
	}

	@Override
	public int getTeam()
	{
		return 1; // blue team = default
	}

	@Override
	public void onStart()
	{
		chooseInfected(0);
	}

	public void chooseInfected(int tries)
	{
		if (tries < 16)
		{
			ArenaPlayer ap = active.get(Util.random(active.size()));
			if (ap != null && ap.isOnline())
			{
				ap.setTeam(2);
				ap.sendMessage("&3You have been chosen for the infected!");
				onSpawn(ap);
				tellPlayers("&e{0} &3is the zombie!", ap.getName());
			}
			else
			{
				chooseInfected(tries++);
			}
		}
		else
		{
			// Shouldn't happen...
			tellPlayers("&cCould not choose a zombie! Aborting...");
			stop();
		}
	}

	@Override
	public void onSpawn(ArenaPlayer ap)
	{
		if (ap.getTeam() == 2)
		{
			ap.clearInventory();

			spawn(ap, true);

			ap.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 10));
			ap.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 3));
			ap.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1));
			ap.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));

			ap.clearInventory();
			ap.decideHat();
		}
	}

	@Override
	public void check()
	{
		if (startTimer <= 0)
		{
			if (startingAmount <= 1)
			{
				tellPlayers("&3Not enough people to play!");
				stop();
				return;
			}

			if (! simpleTeamCheck())
			{
				if (team1size == 0)
				{
					setWinningTeam(2);
					stop();
					rewardTeam(2);
				}
				else if (team2size == 0)
				{
					setWinningTeam(1);
					stop();
					rewardTeam(1);
				}
				else
				{
					tellPlayers("&3One team is empty! game ended!");
					stop();
				}
			}
		}
	}

	@Override
	public void onPreOutOfTime()
	{
		setWinningTeam(1);
	}

	@Override
	public void onOutOfTime()
	{
		rewardTeam(1);
	}

	@Override
	public void onPlayerDeath(ArenaPlayer pl)
	{
		super.onPlayerDeath(pl);

		if (pl.getTeam() == 1)
		{
			pl.sendMessage("&3You have joined the Infected!");
		}

		pl.setTeam(2);
	}

	@Override
	public void announceWinner()
	{
		if (winningTeam == 2)
		{
			tellAllPlayers("&3The infected win!");
		}
		else if (winningTeam == 1)
		{
			tellAllPlayers("&3The humans have survived!");
		}
	}
}