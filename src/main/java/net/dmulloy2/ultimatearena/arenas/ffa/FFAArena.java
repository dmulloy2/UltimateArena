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
package net.dmulloy2.ultimatearena.arenas.ffa;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;

import org.bukkit.Location;

/**
 * @author dmulloy2
 */

public class FFAArena extends Arena
{
	protected ArenaPlayer winner;

	public FFAArena(ArenaZone az)
	{
		super(az);
		spawns.addAll(az.getSpawns());
	}

	@Override
	public void announceWinner()
	{
		if (winner != null)
			tellAllPlayers(getMessage("winner"), winner.getName(), name);
	}

	@Override
	protected String decideColor(ArenaPlayer ap)
	{
		return "&d";
	}

	@Override
	public void decideHat(ArenaPlayer ap)
	{
		ap.decideHat(true);
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
	public void onPlayerEnd(ArenaPlayer ap)
	{
		if (isInGame())
		{
			// Last player standing wins
			if (active.size() == 1)
			{
				this.winner = active.get(0);

				setWinningTeam(null);
				stop();
				rewardTeam(null);
			}
		}
	}

	@Override
	public void onReload()
	{
		// Two players required
		this.minPlayers = Math.max(2, minPlayers);
	}

	/*@Override
	public void onStop()
	{
		// Reward the leader if we run out of time
		// TODO: Make this configurable?
		if (active.size() > 1)
		{
			List<ArenaPlayer> lb = getLeaderboard();
			if (! lb.isEmpty())
				this.winner = lb.get(0);

			reward(winner);
		}
	}*/
}