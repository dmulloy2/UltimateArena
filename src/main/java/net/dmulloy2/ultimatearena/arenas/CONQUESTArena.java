package net.dmulloy2.ultimatearena.arenas;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.ultimatearena.flags.ArenaFlag;
import net.dmulloy2.ultimatearena.flags.ConquestFlag;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.util.Util;

import org.bukkit.Location;

/**
 * @author dmulloy2
 */

public class CONQUESTArena extends Arena
{
	private int redTeamPower;
	private int blueTeamPower;

	public CONQUESTArena(ArenaZone az)
	{
		super(az);
		this.type = FieldType.CONQUEST;

		this.redTeamPower = 1;
		this.blueTeamPower = 1;

		for (ArenaLocation loc : az.getFlags())
		{
			flags.add(new ConquestFlag(this, loc, plugin));
		}
	}

	@Override
	public void onStart()
	{
		this.redTeamPower = active.size() * 4;
		this.blueTeamPower = redTeamPower;
		if (redTeamPower < 4)
		{
			this.redTeamPower = 4;
		}
		if (redTeamPower > 150)
		{
			this.redTeamPower = 150;
		}
		if (blueTeamPower < 4)
		{
			this.blueTeamPower = 4;
		}
		if (blueTeamPower > 150)
		{
			this.blueTeamPower = 150;
		}
	}

	@Override
	public Location getSpawn(ArenaPlayer ap)
	{
		if (isInLobby())
		{
			return super.getSpawn(ap);
		}

		List<ArenaFlag> spawnto = new ArrayList<ArenaFlag>();
		for (int i = 0; i < flags.size(); i++)
		{
			ArenaFlag flag = flags.get(i);
			if (flag.getOwningTeam() == ap.getTeam())
			{
				if (flag.isCapped())
				{
					spawnto.add(flag);
				}
			}
		}

		if (! spawnto.isEmpty())
		{
			int rand = Util.random(spawnto.size());
			ArenaFlag flag = spawnto.get(rand);
			if (flag != null)
			{
				return flag.getLocation();
			}
		}
		else
		{
			return super.getSpawn(ap);
		}

		return null;
	}

	@Override
	public void onPlayerDeath(ArenaPlayer pl)
	{
		super.onPlayerDeath(pl);

		int majority = 0;
		int red = 0;
		int blu = 0;

		for (int i = 0; i < flags.size(); i++)
		{
			ArenaFlag flag = flags.get(i);
			if (flag.getOwningTeam() == 1)
			{
				if (flag.isCapped())
				{
					red++;
				}
			}
			else if (flag.getOwningTeam() == 2)
			{
				if (flag.isCapped())
				{
					blu++;
				}
			}
		}

		if (blu > red)
		{
			majority = 1;
		}
		if (red > blu)
		{
			majority = 2;
		}

		if (majority == 1)
		{
			redTeamPower--;
		}
		else if (majority == 2)
		{
			blueTeamPower--;
		}

		if (pl.getTeam() == 1)
		{
			redTeamPower--;
			for (ArenaPlayer ap : active)
			{
				if (ap.getTeam() == 1)
				{
					ap.sendMessage("&3Your power is now: &e{0}", redTeamPower);
				}
				else
				{
					ap.sendMessage("&3The other team''s power is now: &e{0}", redTeamPower);
				}
			}
		}
		else if (pl.getTeam() == 2)
		{
			blueTeamPower--;
			for (ArenaPlayer ap : active)
			{
				if (ap.getTeam() == 2)
				{
					ap.sendMessage("&3Your power is now: &e{0}", blueTeamPower);
				}
				else
				{
					ap.sendMessage("&3The other team''s power is now: &e{0}", blueTeamPower);
				}
			}
		}
	}

	@Override
	public int getTeam()
	{
		return getBalancedTeam();
	}

	@Override
	public void check()
	{
		for (ArenaPlayer ap : getActivePlayers())
		{
			if (blueTeamPower <= 0)
			{
				if (ap.getTeam() == 2)
				{
					endPlayer(ap, false);
				}
			}
			else if (redTeamPower <= 0)
			{
				if (ap.getTeam() == 1)
				{
					endPlayer(ap, false);
				}
			}
		}

		if (blueTeamPower <= 0)
		{
			setWinningTeam(1);
		}

		if (redTeamPower <= 0)
		{
			setWinningTeam(2);
		}

		// TODO: Make sure this only fires once per second
		for (ArenaFlag flag : Util.newList(flags))
		{
			flag.checkNear(getActivePlayers());
		}

		if (startTimer <= 0)
		{
			if (! simpleTeamCheck())
			{
				setWinningTeam(-1);

				stop();

				rewardTeam(-1);
			}
		}
	}
}