/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.infect;

import net.dmulloy2.ultimatearena.arenas.pvp.PvPArena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Team;
import net.dmulloy2.util.Util;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author dmulloy2
 */

public class InfectArena extends PvPArena
{
	public InfectArena(ArenaZone az)
	{
		super(az);
	}

	@Override
	public void announceWinner()
	{
		if (winningTeam == Team.BLUE)
		{
			tellAllPlayers("&3The infected win!");
		}
		else if (winningTeam == Team.RED)
		{
			tellAllPlayers("&3The humans have survived!");
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
				if (redTeamSize == 0)
				{
					setWinningTeam(Team.BLUE);
					stop();
					rewardTeam(Team.BLUE);
				}
				else if (blueTeamSize == 0)
				{
					setWinningTeam(Team.RED);
					stop();
					rewardTeam(Team.RED);
				}
				else
				{
					tellPlayers("&3One team is empty! game ended!");
					stop();
				}
			}
		}
	}

	private final void chooseInfected(int tries)
	{
		if (tries < 16)
		{
			ArenaPlayer ap = active.get(Util.random(active.size()));
			if (ap != null && ap.isOnline())
			{
				ap.setTeam(Team.BLUE);
				ap.sendMessage("&3You are patient zero!");
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
	public InfectConfig getConfig()
	{
		return (InfectConfig) super.getConfig();
	}

	@Override
	public Team getTeam()
	{
		return Team.RED;
	}

	@Override
	public void onOutOfTime()
	{
		rewardTeam(Team.RED);
	}

	@Override
	public void onPlayerDeath(ArenaPlayer pl)
	{
		if (pl.getTeam() == Team.RED)
		{
			pl.sendMessage("&3You have joined the infected!");
			pl.setTeam(Team.BLUE);
		}
	}

	@Override
	public void onPreOutOfTime()
	{
		setWinningTeam(Team.RED);
	}

	@Override
	public void onSpawn(ArenaPlayer ap)
	{
		if (ap.getTeam() == Team.BLUE)
		{
			ap.clearInventory();

			spawn(ap, true);

			ap.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 10));
			ap.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 3));
			ap.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1));
			ap.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));

			ap.clearInventory();
			ap.decideHat(false);
		}
	}

	@Override
	public void onStart()
	{
		chooseInfected(0);
	}
}