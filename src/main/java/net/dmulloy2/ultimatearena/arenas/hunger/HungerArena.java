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
	public Location getSpawn(ArenaPlayer ap)
	{
		if (isInLobby())
		{
			return super.getSpawn(ap);
		}

		return getRandomSpawn(ap);
	}

	@Override
	public void onSpawn(ArenaPlayer ap)
	{
		ap.decideHat(true);
	}

	@Override
	public void check()
	{
		if (isInGame())
		{
			if (isEmpty())
			{
				setWinningTeam(-1);

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
	public void announceWinner()
	{
		if (winner != null)
			tellAllPlayers("&e{0} &3is the victor!", winner.getName());
	}

	@Override
	public void onPlayerDeath(ArenaPlayer pl)
	{
		super.onPlayerDeath(pl);

		pl.getPlayer().getWorld().strikeLightningEffect(pl.getPlayer().getLocation());
		tellPlayers("&3Tribute &e{0} &3has fallen!", pl.getName());
	}
}