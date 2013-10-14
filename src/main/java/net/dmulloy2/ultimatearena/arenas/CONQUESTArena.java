package net.dmulloy2.ultimatearena.arenas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.dmulloy2.ultimatearena.flags.ArenaFlag;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.ultimatearena.util.Util;

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
		this.startTimer = 180;
		this.maxGameTime = 60 * 20;
		this.maxDeaths = 900;

		for (int i = 0; i < az.getFlags().size(); i++)
		{
			flags.add(new ArenaFlag(this, az.getFlags().get(i), plugin));
		}
	}

	@Override
	public void onStart()
	{
		super.onStart();
		this.redTeamPower = getPlayerCount() * 4;
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
			if (flag.getTeam() == ap.getTeam())
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
				return flag.getLoc();
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
			if (flag.getColor() == 14)
			{
				if (flag.isCapped())
				{
					red++;
				}
			}
			else if (flag.getColor() == 11)
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
			for (ArenaPlayer ap : activePlayers)
			{
				if (ap.getTeam() == 1)
				{
					ap.sendMessage("&cYour power is now: &6" + redTeamPower);
				}
				else
				{
					ap.sendMessage("&cThe other team's power is now: &6" + redTeamPower);
				}
			}
		}
		else if (pl.getTeam() == 2)
		{
			blueTeamPower--;
			for (ArenaPlayer ap : activePlayers)
			{
				if (ap.getTeam() == 2)
				{
					ap.sendMessage("&cYour power is now: &6" + blueTeamPower);
				}
				else
				{
					ap.sendMessage("&cThe other team's power is now: &6" + blueTeamPower);
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
		for (ArenaPlayer ap : Collections.unmodifiableList(activePlayers))
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

		for (int i = 0; i < flags.size(); i++)
		{
			ArenaFlag flag = flags.get(i);

			flag.step();
			flag.checkNear(Collections.unmodifiableList(activePlayers));
		}

		if (startTimer <= 0)
		{
			if (!simpleTeamCheck(false))
			{
				stop();

				rewardTeam(-1);
			}
		}
	}
}