/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2012 - 2015 MineSworn
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
			tellAllPlayers(getMessage("infectedWon"));
		}
		else if (winningTeam == Team.RED)
		{
			tellAllPlayers(getMessage("humansWon"));
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
				ap.sendMessage(getMessage("patientZero"));
				onSpawn(ap);
				tellPlayers(getMessage("zombie"), ap.getName());
			}
			else
			{
				chooseInfected(tries++);
			}
		}
		else
		{
			// Shouldn't happen...
			tellPlayers(getMessage("couldntFindZombie"));
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
			pl.sendMessage(getMessage("joinedInfected"));
			pl.setTeam(Team.BLUE);
		}
	}

	@Override
	public void onPlayerEnd(ArenaPlayer ap)
	{
		if (isInGame())
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