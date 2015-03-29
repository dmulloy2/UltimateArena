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
package net.dmulloy2.ultimatearena.arenas.hunger;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import org.bukkit.Location;

/**
 * @author dmulloy2
 */

public class HungerArena extends Arena
{
	private ArenaPlayer winner;

	public HungerArena(ArenaZone az)
	{
		super(az);
		spawns.addAll(az.getSpawns());
	}

	@Override
	public void announceWinner()
	{
		if (winner != null)
			tellAllPlayers("&e{0} &3is the victor!", winner.getName());
	}

	@Override
	public void check()
	{
		if (isInGame())
		{
			if (isEmpty())
			{
				setWinningTeam(null);

				if (startingAmount > 1)
				{
					if (active.size() > 0)
					{
						this.winner = active.get(0);
					}
				}

				stop();

				if (startingAmount > 1)
				{
					rewardTeam(winningTeam);
				}
				else
				{
					tellPlayers("&3Not enough people to play!");
				}
			}
		}
	}

	@Override
	public Location getSpawn(ArenaPlayer ap)
	{
		if (isInLobby())
		{
			return super.getSpawn(ap);
		}

		return getRandomSpawn(ap);
	}

	@Override
	public void onPlayerDeath(ArenaPlayer pl)
	{
		super.onPlayerDeath(pl);

		pl.getPlayer().getWorld().strikeLightningEffect(pl.getPlayer().getLocation());
		tellPlayers("&3Tribute &e{0} &3has fallen!", pl.getName());
	}

	@Override
	public void onSpawn(ArenaPlayer ap)
	{
		ap.decideHat(true);
	}
}
