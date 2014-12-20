/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.conquest;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaFlag;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Team;
import net.dmulloy2.util.ListUtil;
import net.dmulloy2.util.Util;

import org.bukkit.Location;

/**
 * @author dmulloy2
 */

public class ConquestArena extends Arena
{
	private int blueTeamPower;
	private int redTeamPower;

	public ConquestArena(ArenaZone az)
	{
		super(az);
		this.redTeamPower = 1;
		this.blueTeamPower = 1;

		for (ArenaLocation loc : az.getFlags())
		{
			flags.add(new ConquestFlag(this, loc, plugin));
		}
	}

	@Override
	public void check()
	{
		for (ArenaPlayer ap : getActivePlayers())
		{
			if (blueTeamPower <= 0)
			{
				if (ap.getTeam() == Team.BLUE)
					endPlayer(ap, false);
			}
			else if (redTeamPower <= 0)
			{
				if (ap.getTeam() == Team.RED)
					endPlayer(ap, false);
			}
		}

		if (blueTeamPower <= 0)
			setWinningTeam(Team.RED);

		if (redTeamPower <= 0)
			setWinningTeam(Team.BLUE);

		int red = 0;
		int blue = 0;

		for (ArenaFlag flag : ListUtil.newList(flags))
		{
			flag.checkNear(getActivePlayers());

			if (flag.isCapped())
			{
				if (flag.getOwningTeam() == Team.RED)
					red++;
				else
					blue++;
			}
		}

		if (red == flags.size())
		{
			setWinningTeam(Team.RED);

			stop();

			rewardTeam(Team.RED);
		}
		else if (blue == flags.size())
		{
			setWinningTeam(Team.BLUE);

			stop();

			rewardTeam(Team.BLUE);
		}

		if (startTimer <= 0)
		{
			if (! simpleTeamCheck())
			{
				setWinningTeam(null);

				stop();

				rewardTeam(null);
			}
		}
	}

	@Override
	public List<String> getExtraInfo()
	{
		List<String> ret = new ArrayList<>();

		ret.add("&3Red Team Power: &e" + redTeamPower);
		ret.add("&3Blue Team Power: &e" + blueTeamPower);

		return ret;
	}

	@Override
	public Location getSpawn(ArenaPlayer ap)
	{
		if (isInLobby())
			return super.getSpawn(ap);

		List<ArenaFlag> spawnto = new ArrayList<ArenaFlag>();
		for (ArenaFlag flag : ListUtil.newList(flags))
		{
			if (flag.getOwningTeam() == ap.getTeam())
			{
				if (flag.isCapped())
					spawnto.add(flag);
			}
		}

		if (! spawnto.isEmpty())
		{
			int rand = Util.random(spawnto.size());
			ArenaFlag flag = spawnto.get(rand);
			if (flag != null)
				return flag.getLocation();
		}
		else
		{
			return super.getSpawn(ap);
		}

		return null;
	}

	@Override
	public Team getTeam()
	{
		return getBalancedTeam();
	}

	@Override
	public void onPlayerDeath(ArenaPlayer pl)
	{
		int majority = 0;
		int red = 0;
		int blu = 0;

		for (ArenaFlag flag : ListUtil.newList(flags))
		{
			if (flag.getOwningTeam() == Team.RED)
			{
				if (flag.isCapped())
					red++;
			}
			else if (flag.getOwningTeam() == Team.BLUE)
			{
				if (flag.isCapped())
					blu++;
			}
		}

		majority = blu > red ? 1 : 2;

		if (majority == 1)
			redTeamPower--;
		else
			blueTeamPower--;

		if (pl.getTeam() == Team.RED)
		{
			redTeamPower--;
			for (ArenaPlayer ap : active)
			{
				if (ap.getTeam() == Team.RED)
					ap.sendMessage("&3Your power is now: &e{0}", redTeamPower);
				else
					ap.sendMessage("&3The other team''s power is now: &e{0}", redTeamPower);
			}
		}
		else if (pl.getTeam() == Team.BLUE)
		{
			blueTeamPower--;
			for (ArenaPlayer ap : active)
			{
				if (ap.getTeam() == Team.BLUE)
					ap.sendMessage("&3Your power is now: &e{0}", blueTeamPower);
				else
					ap.sendMessage("&3The other team''s power is now: &e{0}", blueTeamPower);
			}
		}
	}

	@Override
	public void onStart()
	{
		this.redTeamPower = Math.max(4, Math.min(150, active.size() * 4));
		this.blueTeamPower = redTeamPower;
	}
}