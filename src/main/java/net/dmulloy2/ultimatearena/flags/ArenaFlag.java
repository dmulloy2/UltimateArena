package net.dmulloy2.ultimatearena.flags;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;

/**
 * @author dmulloy2
 */

@Getter
@Setter
public class ArenaFlag extends FlagBase
{
	protected int owningTeam;
	protected int cappingTeam;
	protected int power;

	protected boolean capped;

	public ArenaFlag(Arena arena, ArenaLocation location, UltimateArena plugin)
	{
		super(arena, location, plugin);

		Wool wool = new Wool();
		wool.setColor(getColor(8));
		Util.setData(notify, wool);
	}

	@Override
	public void checkNear(List<ArenaPlayer> arenaPlayers)
	{
		int team1 = 0;
		int team2 = 0;

		List<ArenaPlayer> players = new ArrayList<ArenaPlayer>();

		for (ArenaPlayer ap : arenaPlayers)
		{
			Player player = ap.getPlayer();
			if (player.getHealth() < 0.0D)
				continue;

			if (Util.pointDistance(player.getLocation(), location) < 4.5)
			{
				players.add(ap);

				if (ap.getTeam() == 1)
					team1++;
				else
					team2++;
			}
		}

		// FIXME: Capping messages are displayed twice

		cappingTeam = team1 > team2 ? 1 : 2;

		// The other team is trying to cap
		if (cappingTeam != owningTeam)
		{
			if (cappingTeam == 1)
			{
				int added = (team1 - team2) * 5;
				if (power - added < 0)
					power = 0;
				else
					power -= added;

				for (ArenaPlayer ap : players)
				{
					ap.sendMessage("&3Capping! &e{0}&3%", power);
				}

				if (power == 0)
				{
					setOwningTeam(1);
				}
			}
			else
			{
				int added = (team2 - team1) * 5;
				if (power - added < 0)
					power = 0;
				else
					power -= added;

				for (ArenaPlayer ap : players)
				{
					ap.sendMessage("&3Capping! &e{0}&3%", power);
				}

				if (power == 0)
				{
					setOwningTeam(2);
				}
			}
		}
		else
		{
			if (owningTeam == 1 && !capped)
			{
				int added = (team1 - team2) * 5;
				if (power + added > 100)
					power = 100;
				else
					power += added;

				for (ArenaPlayer ap : players)
				{
					ap.sendMessage("&3Capping! &e{0}&3%", power);
				}

				if (power == 100)
				{
					for (ArenaPlayer ap : players)
					{
						ap.sendMessage("&3Capped!");
					}

					capped = true;
					setOwningTeam(1);
				}
			}
			else if (owningTeam == 2 && !capped)
			{
				int added = (team2 - team1) * 5;
				if (power + added > 100)
					power = 100;
				else
					power += added;

				for (ArenaPlayer ap : players)
				{
					ap.sendMessage("&3Capping! &e{0}&3%", power);
				}

				if (power == 100)
				{
					for (ArenaPlayer ap : players)
					{
						ap.sendMessage("&3Capped!");
					}

					capped = true;
					setOwningTeam(1);
				}
			}
		}
	}

	private final void setOwningTeam(int team)
	{
		this.owningTeam = team;

		Wool wool = new Wool();
		wool.setColor(getColor(team == 1 ? 14 : 11));
		Util.setData(notify, wool);
	}

	private final DyeColor getColor(int color)
	{
		if (color == 8)
			return DyeColor.SILVER;
		else if (color == 11)
			return DyeColor.BLUE;
		else if (color == 14)
			return DyeColor.RED;
		else
			return DyeColor.WHITE;
	}
}