package net.dmulloy2.ultimatearena.arenas;

import lombok.Getter;
import net.dmulloy2.ultimatearena.flags.BombFlag;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;

/**
 * @author dmulloy2
 */

@Getter
public class BOMBArena extends Arena
{
	private int redTeamPower;

	private BombFlag bomb1;
	private BombFlag bomb2;

	public BOMBArena(ArenaZone az)
	{
		super(az);

		this.type = FieldType.BOMB;
		this.startTimer = 120;
		this.maxGameTime = 60 * 15;
		this.maxDeaths = 990;

		bomb1 = new BombFlag(this, az.getFlags().get(0), plugin);
		bomb2 = new BombFlag(this, az.getFlags().get(1), plugin);
		bomb1.setBombNumber(1);
		bomb2.setBombNumber(2);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		this.redTeamPower = getPlayerCount() * 3;
		if (redTeamPower < 10)
		{
			this.redTeamPower = 10;
		}
		if (redTeamPower > 150)
		{
			this.redTeamPower = 150;
		}
	}

	@Override
	public void onOutOfTime()
	{
		setWinningTeam(2);
		rewardTeam(winningTeam);
	}

	@Override
	public void onPlayerDeath(ArenaPlayer pl)
	{
		super.onPlayerDeath(pl);

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
	}

	@Override
	public int getTeam()
	{
		return getBalancedTeam();
	}

	@Override
	public void check()
	{
		if (startTimer <= 0)
		{
			simpleTeamCheck(true);
		}

		bomb1.checkNear(activePlayers);
		bomb2.checkNear(activePlayers);

		if (bomb1.isExploded() && bomb2.isExploded())
		{
			setWinningTeam(1);

			stop();

			rewardTeam(1);
			return;
		}

		if (redTeamPower <= 0)
		{
			setWinningTeam(2);

			stop();

			rewardTeam(2);
			return;
		}
	}
}