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
package net.dmulloy2.ultimatearena.arenas.conquest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaFlag;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.Team;
import net.dmulloy2.swornapi.util.ListUtil;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class ConquestFlag extends ArenaFlag
{
	private static final double RADIUS = 4.5D;
	private static final double RADIUS_SQUARED = RADIUS * RADIUS;

	public ConquestFlag(Arena arena, ArenaLocation location, UltimateArena plugin)
	{
		super(arena, location, plugin);
	}

	@Override
	public void checkNear(ArenaPlayer[] arenaPlayers)
	{
		int team1 = 0;
		int team2 = 0;

		Set<ArenaPlayer> players = new HashSet<>();
		for (ArenaPlayer ap : arenaPlayers)
		{
			Player player = ap.getPlayer();
			if (player.getHealth() > 0.0D && player.getWorld().equals(location.getWorld())
					&& player.getLocation().distanceSquared(location) < RADIUS_SQUARED)
			{
				players.add(ap);

				if (ap.getTeam() == Team.RED)
					team1++;
				else
					team2++;
			}
		}

		cappingTeam = team1 > team2 ? Team.RED : Team.BLUE;

		// The other team is trying to cap
		if (cappingTeam != owningTeam)
		{
			if (cappingTeam == Team.RED)
			{
				int added = (team1 - team2) * 5;
				if (power - added < 0)
					power = 0;
				else
					power -= added;

				for (ArenaPlayer apl : players)
				{
					apl.sendMessage(getMessage("capping"), power);
				}

				if (power == 0)
				{
					capped = false;
					setOwningTeam(Team.RED);
				}
			}
			else
			{
				int added = (team2 - team1) * 5;
				if (power - added < 0)
					power = 0;
				else
					power -= added;

				for (ArenaPlayer apl : players)
				{
					apl.sendMessage(getMessage("capping"), power);
				}

				if (power == 0)
				{
					capped = false;
					setOwningTeam(Team.BLUE);
				}
			}
		}
		else
		{
			if (owningTeam == Team.RED && ! capped)
			{
				int added = (team1 - team2) * 5;
				if (power + added > 100)
					power = 100;
				else
					power += added;

				if (power == 100)
				{
					for (ArenaPlayer apl : players)
					{
						apl.sendMessage(getMessage("capped"));
					}

					capped = true;
					setOwningTeam(Team.RED);
				}
				else
				{
					for (ArenaPlayer apl : players)
					{
						apl.sendMessage(getMessage("capping"), power);
					}
				}
			}
			else if (owningTeam == Team.BLUE && ! capped)
			{
				int added = (team2 - team1) * 5;
				if (power + added > 100)
					power = 100;
				else
					power += added;

				if (power == 100)
				{
					for (ArenaPlayer apl : players)
					{
						apl.sendMessage(getMessage("capped"));
					}

					capped = true;
					setOwningTeam(Team.BLUE);
				}
				else
				{
					for (ArenaPlayer apl : players)
					{
						apl.sendMessage(getMessage("capping"), power);
					}
				}
			}
		}
	}
}
