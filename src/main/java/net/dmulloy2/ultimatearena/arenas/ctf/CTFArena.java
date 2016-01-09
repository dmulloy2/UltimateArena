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
package net.dmulloy2.ultimatearena.arenas.ctf;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import net.dmulloy2.types.CustomScoreboard;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Team;

import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author dmulloy2
 */

public class CTFArena extends Arena
{
	private @Getter CTFFlagBase redFlag;
	private @Getter CTFFlagBase blueFlag;

	private int redCap;
	private int blueCap;

	private BukkitTask moveTask;

	public CTFArena(ArenaZone az)
	{
		super(az);
		this.redFlag = new CTFFlagBase(this, az.getFlags().get(0), Team.RED, plugin);
		this.blueFlag = new CTFFlagBase(this, az.getFlags().get(1), Team.BLUE, plugin);

		this.redFlag.initialize();
		this.blueFlag.initialize();

		// 12 seems arbitrary. Why not 20 ticks?
		this.moveTask = new ExecuteMove().runTaskTimer(plugin, 12, 1);
	}

	// TODO: Move this to onMove()
	public class ExecuteMove extends BukkitRunnable
	{
		@Override
		public void run()
		{
			if (! isStopped())
			{
				ArenaPlayer[] check = getActivePlayers();

				redFlag.checkNear(check);
				blueFlag.checkNear(check);
			}
			else
			{
				onStop();
			}
		}
	}

	@Override
	public void addScoreboardEntries(CustomScoreboard board, ArenaPlayer player)
	{
		board.addEntry("Red", redCap);
		board.addEntry("Blue", blueCap);
	}

	public void capture(Team team)
	{
		if (team == Team.RED)
		{
			redCap++;
			tellPlayers(getMessage("teamCaptures"), team, redCap);

			if (redCap >= 3)
			{
				setWinningTeam(Team.RED);
				stop();
				rewardTeam(Team.RED);
			}
		}
		else if (team == Team.BLUE)
		{
			blueCap++;
			tellPlayers(getMessage("teamCaptures"), team, blueCap);

			if (blueCap >= 3)
			{
				setWinningTeam(Team.BLUE);
				stop();
				rewardTeam(Team.BLUE);
			}
		}
	}

	@Override
	public void check()
	{
		// Basically just timers
		redFlag.getFlag().tick();
		blueFlag.getFlag().tick();
	}

	@Override
	public List<String> getExtraInfo()
	{
		return Arrays.asList("&3Red: &e" + redCap, "&3Blue: &e" + blueCap);
	}

	@Override
	public Team getTeam()
	{
		return getBalancedTeam();
	}

	@Override
	public void onPlayerDeath(ArenaPlayer ap)
	{
		redFlag.getFlag().onPlayerDeath(ap);
		blueFlag.getFlag().onPlayerDeath(ap);
	}

	@Override
	public void onPlayerEnd(ArenaPlayer ap)
	{
		if (isInGame())
		{
			redFlag.getFlag().onPlayerEnd(ap);
			blueFlag.getFlag().onPlayerEnd(ap);

			if (redTeamSize == 0 || blueTeamSize == 0)
			{
				tellPlayers(getMessage("teamEmpty"));
				stop();
			}
		}
	}

	@Override
	public void onReload()
	{
		// 2 players are required to play
		this.minPlayers = Math.max(2, minPlayers);
	}

	@Override
	public void onStop()
	{
		redFlag.getFlag().setStopped(true);
		blueFlag.getFlag().setStopped(true);

		redFlag.getFlag().getReturnto().getBlock().setType(Material.AIR);
		blueFlag.getFlag().getReturnto().getBlock().setType(Material.AIR);
		redFlag.getFlag().despawn();
		blueFlag.getFlag().despawn();

		moveTask.cancel();
		moveTask = null;
	}
}