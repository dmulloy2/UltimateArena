package net.dmulloy2.ultimatearena.arenas;

import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.entity.Player;
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
		this.startTimer = 80;
		this.maxGameTime = (60 * 2) + 10;
		this.maxDeaths = 99;
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
			ArenaPlayer apl = arenaPlayers.get(Util.random(arenaPlayers.size()));
			if (apl != null && apl.getPlayer().isOnline())
			{
				apl.setTeam(2);
				apl.sendMessage("&3You have been chosen for the infected!");
				onSpawn(apl);
				tellPlayers("&e{0} &3is the zombie!", apl.getPlayer().getName());
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
	public void onSpawn(ArenaPlayer apl)
	{
		if (apl.getTeam() == 2)
		{
			Player pl = apl.getPlayer();
			apl.clearInventory();

			spawn(pl, true);
			
			pl.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 2400, 10));
			pl.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2400, 3));
			pl.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2400, 1));
			pl.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2400, 2));

			apl.clearInventory();
			apl.decideHat();
		}
	}

	@Override
	public void check()
	{
		if (startTimer <= 0)
		{
			if (! simpleTeamCheck(false))
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
			else
			{
				if (startingAmount <= 1)
				{
					tellPlayers("&3Not enough people to play!");

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