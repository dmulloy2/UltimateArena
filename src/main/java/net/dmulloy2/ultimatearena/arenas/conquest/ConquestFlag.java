package net.dmulloy2.ultimatearena.arenas.conquest;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaFlag;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.Team;
import net.dmulloy2.util.ListUtil;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class ConquestFlag extends ArenaFlag
{
	public ConquestFlag(Arena arena, ArenaLocation location, UltimateArena plugin)
	{
		super(arena, location, plugin);
	}

	@Override
	public void checkNear(ArenaPlayer[] arenaPlayers)
	{
		int team1 = 0;
		int team2 = 0;

		List<ArenaPlayer> players = new ArrayList<>();
		for (ArenaPlayer ap : arenaPlayers)
		{
			Player player = ap.getPlayer();
			if (player.getHealth() > 0.0D && player.getWorld().getUID().equals(location.getWorld().getUID())
					&& player.getLocation().distance(location) < 4.5D)
			{
				players.add(ap);

				if (ap.getTeam() == Team.RED)
					team1++;
				else
					team2++;
			}
		}

		// Remove any duplicates
		players = ListUtil.removeDuplicates(players);
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
					apl.sendMessage("&3Capping! &e{0}&3%", power);
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
					apl.sendMessage("&3Capping! &e{0}&3%", power);
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
						apl.sendMessage("&3Capped!");
					}

					capped = true;
					setOwningTeam(Team.RED);
				}
				else
				{
					for (ArenaPlayer apl : players)
					{
						apl.sendMessage("&3Capping! &e{0}&3%", power);
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
						apl.sendMessage("&3Capped!");
					}

					capped = true;
					setOwningTeam(Team.BLUE);
				}
				else
				{
					for (ArenaPlayer apl : players)
					{
						apl.sendMessage("&3Capping! &e{0}&3%", power);
					}
				}
			}
		}
	}
}